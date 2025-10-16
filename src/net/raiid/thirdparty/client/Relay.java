package net.raiid.thirdparty.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public final class Relay {

    public static void start(final Socket a, final Socket b) {
        Thread t1 = new Thread(() -> {
        	pipe(a, b, true);
        }, "tp-a2b");
        Thread t2 = new Thread(() -> {
        	pipe(b, a, false);
        }, "tp-b2a");
        t1.setDaemon(true);
        t2.setDaemon(true);
        t1.start();
        t2.start();
    }

    private static void pipe(Socket src, Socket dst, boolean shutdownOut) {
        try {
            InputStream in = src.getInputStream();
            OutputStream out = dst.getOutputStream();
            byte[] buf = new byte[64*1024];
            int n;
            while((n = in.read(buf)) >= 0) {
            	out.write(buf,0,n);
            	out.flush();
            }
        } catch (Exception ignored) {
        } finally {
            try {
            	if (shutdownOut)
            		dst.shutdownOutput();
            	else
            		dst.shutdownInput();
            	} catch (Exception ignored) {}
            try {
            	src.close();
            } catch (Exception ignored) {}
            try {
            	dst.close();
            } catch (Exception ignored) {}
        }
    }

}
