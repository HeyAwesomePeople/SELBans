package me.HeyAwesomePeople.selbans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.HeyAwesomePeople.selbans.mysql.MySQL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class MySQLMethods {

	public SELBans plugin = SELBans.instance;

	public void acreateTables() {
		Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			public void run() {
				java.sql.PreparedStatement statement;
				try {
					statement = plugin.sql.openConnection().prepareStatement("CREATE TABLE IF NOT EXISTS bans (PlayerId tinytext, Time datetime, Tempban boolean, TempbanUnbanDate datetime, Reason tinytext)");
					statement.executeUpdate();
					statement = plugin.sql.openConnection().prepareStatement("CREATE TABLE IF NOT EXISTS mutes (PlayerId tinytext, Time datetime, Tempmute boolean, TempmuteUnmuteDate datetime, Reason tinytext)");
					statement.executeUpdate();
					statement = plugin.sql.openConnection().prepareStatement("CREATE TABLE IF NOT EXISTS warns (PlayerId tinytext, Time datetime, Reason tinytext)");
					statement.executeUpdate();
					statement = plugin.sql.openConnection().prepareStatement("CREATE TABLE IF NOT EXISTS kicks (PlayerId tinytext, Time datetime, Reason tinytext)");
					statement.executeUpdate();
				} catch (SQLException sqlE) {
					sqlE.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}

		});
	}

	public void sunPunish(String type, Date time, UUID uuid) {
		java.sql.PreparedStatement statement;

		try {
			if (type.equalsIgnoreCase("ban")) {
				statement = plugin.sql.openConnection().prepareStatement("UPDATE bans SET Tempban=? WHERE PlayerId=? AND Time=?");
				statement.setInt(1, 2);
				statement.setString(2, uuid.toString());
				statement.setString(3, plugin.format.format(time));
				statement.executeUpdate();
			}
			if (type.equalsIgnoreCase("mute")) {
				statement = plugin.sql.openConnection().prepareStatement("UPDATE mutes SET Tempmute=? WHERE PlayerId=? AND Time=?");
				statement.setInt(1, 2);
				statement.setString(2, uuid.toString());
				statement.setString(3, plugin.format.format(time));
				statement.executeUpdate();
			}

		} catch (SQLException sqlE) {
			sqlE.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void logPunish(final String type, final Integer tempban,
			Date unbanDate, final String reason, final UUID uuid) {
		final Date date = new Date();
		if (unbanDate == null) {
			unbanDate = date;
		}
		final Date unPunishDate = unbanDate;

		Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			public void run() {
				java.sql.PreparedStatement statement;

				try {
					if (type.equalsIgnoreCase("ban")) {
						statement = plugin.sql.openConnection().prepareStatement("INSERT INTO bans (PlayerId, Time, Tempban, TempbanUnbanDate, Reason) VALUES ('" + uuid.toString() + "','" + plugin.format.format(date) + "','" + tempban + "','" + plugin.format.format(unPunishDate) + "','" + reason + "')");
						statement.executeUpdate();
					}
					if (type.equalsIgnoreCase("mute")) {
						statement = plugin.sql.openConnection().prepareStatement("INSERT INTO mutes (PlayerId, Time, Tempmute, TempmuteUnmuteDate, Reason) VALUES ('" + uuid.toString() + "','" + plugin.format.format(date) + "','" + tempban + "','" + plugin.format.format(unPunishDate) + "','" + reason + "')");
						statement.executeUpdate();
						plugin.mutes.put(uuid, plugin.mysqlmethods.sfetchLog("mutes", uuid.toString()));
					}
					if (type.equalsIgnoreCase("warn")) {
						statement = plugin.sql.openConnection().prepareStatement("INSERT INTO warns (PlayerId, Time, Reason) VALUES ('" + uuid.toString() + "','" + plugin.format.format(date) + "','" + reason + "')");
						statement.executeUpdate();
					}
					if (type.equalsIgnoreCase("kick")) {
						statement = plugin.sql.openConnection().prepareStatement("INSERT INTO kicks (PlayerId, Time, Reason) VALUES ('" + uuid.toString() + "','" + plugin.format.format(date) + "','" + reason + "')");
						statement.executeUpdate();
					}
				} catch (SQLException sqlE) {
					sqlE.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public List<Map<String, String>> sfetchLog(String type, String uuid) {
		java.sql.PreparedStatement statement;
		List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		try {
			statement = plugin.sql.openConnection().prepareStatement("SELECT * FROM " + type + " WHERE PlayerId=? ORDER BY Time DESC");
			statement.setString(1, "" + uuid);
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				Map<String, String> data = new HashMap<String, String>();
				for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
					data.put(result.getMetaData().getColumnName(i), result.getString(result.getMetaData().getColumnName(i)));
				}
				results.add(data);
			}
		} catch (SQLException sqlE) {
			sqlE.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (results.size() < 1) {
			Map<String, String> data = new HashMap<String, String>();
			data.put("None", "None");
			results.add(data);
			return results;
		}
		return results;
	}

	public void sattemptReconnect() {
		if (plugin.connect) {
			plugin.sql = new MySQL(plugin, plugin.config.dHost, plugin.config.dPort, plugin.config.dDatabase, plugin.config.dUser, plugin.config.dPass);
			Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[SELBans] Reconnecting database...");
			try {
				plugin.c = plugin.sql.openConnection();
				plugin.connect = true;
				Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[SELBans] Done.");
				plugin.mysqlmethods.acreateTables();
			} catch (SQLException e) {
				e.printStackTrace();
				plugin.connect = false;
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[SELBans] MySQL Exception Error! Full functionality will not work until problem is resolved.");
			} catch (ClassNotFoundException c) {
				c.printStackTrace();
				plugin.connect = false;
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[SELBans] MySQL ClassNotFound Error! Full functionality will not work until problem is resolved.");
			}
		}
	}

}
