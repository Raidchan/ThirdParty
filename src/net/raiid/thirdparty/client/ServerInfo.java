package net.raiid.thirdparty.client;

public abstract class ServerInfo {

	public abstract void log(String str);
	public abstract void logWarning(String str);
	public abstract int getPort();
	public abstract int getOnlines();
	public abstract int getMax();
	public abstract String getMotd();

}
