package net.raiid.thirdparty.client.fabric;

import java.nio.file.Path;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.raiid.thirdparty.client.ControlClient;
import net.raiid.thirdparty.client.ServerInfo;
import net.raiid.thirdparty.client.Config;

public final class ThirdParty implements DedicatedServerModInitializer {

	private ServerInfo info;
    private ControlClient client;
    private Config config;

    public Config getConfig() {
    	return this.config;
    }

	@Override
	public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
        	this.onServerStarting(server);
        });
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
        	this.onServerStopping(server);
        });
	}

    private void onServerStarting(MinecraftServer server) {
        Path cfgDir = FabricLoader.getInstance().getConfigDir().resolve("ThirdParty");
        Config.saveDefaultConfig(cfgDir);
        this.config = new Config(cfgDir);

        String name = this.getConfig().getString("server-name");
        String token = this.getConfig().getString("token");
        String host = this.getConfig().getString("hub-host");
        int port = this.getConfig().getInt("hub-port");
        this.info = new FabricInfo(server);
        this.client = new ControlClient(this.info, name, token, host, port);
        this.client.start();
    }

    private void onServerStopping(MinecraftServer server) {
        if (this.client != null) {
            this.client.stop();
            this.client = null;
        }
    }

}
