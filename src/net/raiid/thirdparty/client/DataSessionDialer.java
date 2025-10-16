package net.raiid.thirdparty.client;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public final class DataSessionDialer {

    private final int localPort;
    private final String host;
    private final int port;
    private final String token;
    private final String sid;

    public DataSessionDialer(int localPort, String host, int port, String token, String sid) {
        this.localPort = localPort;
        this.host = host;
        this.port = port;
        this.token = token;
        this.sid = sid;
    }

    public void start() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runDialer();
            }
        }, "thirdparty-dialer-" + sid);
        thread.setDaemon(true);
        thread.start();
    }
    
    private void runDialer() {
        Socket hubData = null;
        Socket toPaper = null;
        try {
            // Hub接続を作成
            hubData = new Socket(host, port);
            hubData.setTcpNoDelay(true); // 低遅延設定
            
            PrintWriter hout = new PrintWriter(
                new OutputStreamWriter(hubData.getOutputStream(), "UTF-8"), 
                false // auto-flushを無効化
            );
            hout.println(Json.obj("op", "datasession", "sid", sid, "token", token));
            hout.flush(); // 明示的にフラッシュ
            
            // Paperサーバーへの接続
            toPaper = new Socket("127.0.0.1", localPort);
            toPaper.setTcpNoDelay(true); // 低遅延設定
            
            // 改善版Relayを使用
            Relay.start(hubData, toPaper);
            
        } catch (Exception e) {
            // エラー時のクリーンアップ
            closeQuietly(hubData);
            closeQuietly(toPaper);
        }
    }
    
    private static void closeQuietly(Socket socket) {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (Exception ignored) {}
        }
    }
}