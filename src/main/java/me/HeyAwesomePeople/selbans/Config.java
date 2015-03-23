package me.HeyAwesomePeople.selbans;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Config {
	public SELBans plugin = SELBans.instance;

	public String dHost;
	public String dPort;
	public String dDatabase;
	public String dUser;
	public String dPass;
	
	public List<String> admins = new ArrayList<String>();
	
	public Config() {
		dHost = plugin.getConfig().getString("database.host");
		dPort = plugin.getConfig().getString("database.port");
		dDatabase = plugin.getConfig().getString("database.database");
		dUser = plugin.getConfig().getString("database.user");
		dPass = plugin.getConfig().getString("database.pass");
		
		if (dHost == null || dPort == null || dDatabase == null || dUser == null || dPass == null) {
			plugin.connect = false;
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[SELBans] Full functionality will not work until database info is filled in.");
		} else {
			plugin.connect = true;
		}
	}
	
	public void setDatabaseValue(String d, String v) {
		plugin.getConfig().set("database." + d, v);
	}
	
}
