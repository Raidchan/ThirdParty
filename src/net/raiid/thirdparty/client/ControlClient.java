package net.raiid.thirdparty.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ControlClient {

    private final ServerInfo info;
    private final String name;
    private final String token;
    private final String host;
    private final int port;

    private volatile boolean running;

    private Socket sock;
    private PrintWriter out;
    private BufferedReader in;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private ScheduledExecutorService heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();

    public ControlClient(ServerInfo info, String name, String token, String host, int port) {
        this.info = info;
        this.name = name;
        this.token = token;
        this.host = host;
        this.port = port;
    }

    public void start() {
        this.info.log("ThirdParty started. (hub=" + this.host + ":" + this.port + " name=" + this.name + ")");
    	this.running = true;
    	this.executor.submit(() -> {
            long backoff = 1000L;
            while (this.running) {
                try {
                	this.sock = new Socket(this.host, this.port);
                	this.out = new PrintWriter(new OutputStreamWriter(this.sock.getOutputStream(), "UTF-8"), true);
                	this.in  = new BufferedReader(new InputStreamReader(this.sock.getInputStream(), "UTF-8"));

                	this.out.println(Json.obj("op", "register", "name", this.name, "token", this.token));

                    String first = this.in.readLine();
                    if (first == null || first.indexOf("\"ok\":true") < 0) {
                        throw new IOException("register failed: " + first);
                    }
                    this.info.log("register resp: " + first);

                    this.startHeartbeat();

                    backoff = 1000L;
                    String line;
                    while (this.running && (line = in.readLine()) != null) {
                        if (line.contains("\"op\":\"open\"")) {
                            String sid = Json.getString(line, "sid");
                            if (sid != null && sid.length() > 0) {
                            	this.info.log("open sid=" + sid);
                                new DataSessionDialer(this.info.getPort(), this.host, this.port, this.token, sid).start();
                            }
                        } else if (line.contains("\"op\":\"close\"")) {
                        	this.info.log("control close requested by hub");
                            break;
                        }
                    }
                } catch (Exception e) {
                    if (this.running) {
                    	this.info.logWarning("control lost: " + e.getMessage());
                    }
                } finally {
                    this.close();
                }
                if (!this.running)
                	break;
                try {
                	Thread.sleep(backoff);
                } catch (InterruptedException ignored) {}
                backoff = Math.min(backoff * 2, 30000L);
            }
        });
    }

    private void startHeartbeat() {
    	if (this.heartbeatScheduler != null && !this.heartbeatScheduler.isShutdown())
    	    this.heartbeatScheduler.shutdownNow();
    	this.heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
    	this.heartbeatScheduler.scheduleAtFixedRate(() -> {
            try {
                int online = this.info.getOnlines();
                int max = this.info.getMax();
                String motd = this.info.getMotd();

                int paperPort = this.info.getPort();
                long start = System.nanoTime();
                try (Socket s = new Socket("127.0.0.1", paperPort)) {}
                long end = System.nanoTime();
                int ping = (int) ((end - start) / 1_000_000L);
                // ---------------------------

                if (this.out != null) {
                	this.out.println(Json.obj("op", "hb",
                            "online", "#" + online,
                            "max", "#" + max,
                            "motd", motd,
                            "ping", "#" + ping));
                }
            } catch (Exception ignored) {}
        }, 2000L, 5000L, TimeUnit.MILLISECONDS);
    }

    public void close() {
        try {
        	if (this.executor != null)
        		this.executor.shutdownNow();
        	} catch (Exception ignored) {}
        try {
        	if (this.heartbeatScheduler != null)
        		this.heartbeatScheduler.shutdown();
        	} catch (Exception ignored) {}
        this.heartbeatScheduler = null;
        try {
        	if (this.sock != null)
        		this.sock.close();
        	} catch (Exception ignored) {}
        this.sock = null;
        this.out = null;
        this.in = null;
    }

    public void stop() {
        this.info.log("ThirdParty disabled.");
    	this.running = false;
    	this.close();
    }

}
