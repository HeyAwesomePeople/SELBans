package me.HeyAwesomePeople.selbans;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlayersConfig {
	public SELBans plugin = SELBans.instance;

	private FileConfiguration playersConfig = null;
	private File playersConfigFile = null;

	private File file = new File(plugin.getDataFolder() + File.separator + "players.yml");

	public PlayersConfig() {
		if (!file.exists()) {
			getPlayersConfig().set("players.81e5ec2c-b9c1-4625-bb6c-a7b0321dbb83.lastname", "Nintendpro");
			savePlayersConfig();
		}
	}

	public void addPlayerToConfig(Player p) {
		this.getPlayersConfig().set("players." + p.getUniqueId() + ".lastName", p.getDisplayName());
		this.savePlayersConfig();
		return;
	}
	
	public UUID getPlayerUUID(String p) {
		for (String s : getPlayersConfig().getConfigurationSection("players").getKeys(true)) {
			if (s.contains("lastName")) {
					if (getPlayersConfig().contains("players." + s)) {
						String sp = getPlayersConfig().getString("players." + s);
						if (sp.equals(p)) {
							String[] r = s.split("\\.");
							return UUID.fromString(r[0]);
						} else {
							continue;
						}
					} else {
						continue;
					}
				
			}
		}
		return null;
	}
	
	public Boolean hasPlayerPlayedBefore(String playername) {
		for (String s : getPlayersConfig().getConfigurationSection("players").getKeys(true)) {
			if (s.contains("lastName")) {
				if (getPlayersConfig().contains("players." + s)) {
					return true;
				} else {
					continue;
				}
			}
		}
		return false;
	}
	
	
	// config api

	@SuppressWarnings("deprecation")
	public void reloadPlayersConfig() {
		if (playersConfigFile == null) {
			playersConfigFile = new File(plugin.getDataFolder(), "players.yml");
		}
		playersConfig = YamlConfiguration.loadConfiguration(playersConfigFile);

		// Look for defaults in the jar
		InputStream defConfigStream = plugin.getResource("players.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			playersConfig.setDefaults(defConfig);
		}
	}

	public FileConfiguration getPlayersConfig() {
		if (playersConfig == null) {
			this.reloadPlayersConfig();
		}
		return playersConfig;
	}

	public void savePlayersConfig() {
		if (playersConfig == null || playersConfigFile == null) {
			return;
		}
		try {
			getPlayersConfig().save(playersConfigFile);
		} catch (IOException ex) {
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + playersConfigFile, ex);
		}
	}

	public void save2playersConfig() {
		if (playersConfigFile == null) {
			playersConfigFile = new File(plugin.getDataFolder(), "players.yml");
		}
		if (!playersConfigFile.exists()) {
			plugin.saveResource("players.yml", false);
		}
	}

}
