package net.raiid.thirdparty.client.spigot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import net.raiid.thirdparty.client.ServerInfo;

public class SpigotInfo extends ServerInfo {

	private JavaPlugin plugin;

	public SpigotInfo(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void log(String str) {
		this.plugin.getLogger().info(str);
	}

	@Override
	public void logWarning(String str) {
		this.plugin.getLogger().warning(str);
	}

	@Override
	public int getPort() {
		return Bukkit.getPort();
	}

	@Override
	public int getOnlines() {
		return Bukkit.getOnlinePlayers().size();
	}

	@Override
	public int getMax() {
		return Bukkit.getMaxPlayers();
	}

	@Override
	public String getMotd() {
		return Bukkit.getMotd();
	}

}
