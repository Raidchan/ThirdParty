package net.raiid.thirdparty.client.spigot;

import org.bukkit.plugin.java.JavaPlugin;

import net.raiid.thirdparty.client.ControlClient;
import net.raiid.thirdparty.client.ServerInfo;

public final class ThirdParty extends JavaPlugin {

	private ServerInfo info;
    private ControlClient client;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        String name = this.getConfig().getString("server-name");
        String token = this.getConfig().getString("token");
        String host = this.getConfig().getString("hub-host");
        int port = this.getConfig().getInt("hub-port");
        this.info = new SpigotInfo(this);
        this.client = new ControlClient(this.info, name, token, host, port);
        this.client.start();
    }

    @Override
    public void onDisable() {
        if (this.client != null) {
            this.client.stop();
            this.client = null;
        }
    }

}
