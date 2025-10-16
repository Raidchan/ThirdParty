package net.raiid.thirdparty.client.fabric;

import net.minecraft.server.MinecraftServer;
import net.raiid.thirdparty.client.ServerInfo;

public class FabricInfo extends ServerInfo {

	private MinecraftServer server;

	public FabricInfo(MinecraftServer server) {
		this.server = server;
	}

	@Override
	public void log(String str) {
		System.out.println(str);
	}

	@Override
	public void logWarning(String str) {
		System.out.println(str);
	}

	@Override
	public int getPort() {
		return 7777;
		//return this.server.getServerPort();
	}

	@Override
	public int getOnlines() {
		return 0;
		//return this.server.getCurrentPlayerCount();
	}

	@Override
	public int getMax() {
		return 999;
		//return this.server.getMaxPlayerCount();
	}

	@Override
	public String getMotd() {
		return "";
		//return this.server.getServerMotd();
	}

}
