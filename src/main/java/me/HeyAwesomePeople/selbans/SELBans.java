package me.HeyAwesomePeople.selbans;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.HeyAwesomePeople.selbans.commands.AdminCommands;
import me.HeyAwesomePeople.selbans.mysql.MySQL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class SELBans extends JavaPlugin {

	public static SELBans instance;
	
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public HashMap<UUID, Integer> timeout = new HashMap<UUID, Integer>();
	
	public Boolean connect;
	
	public Config config;
	public MySQLMethods mysqlmethods;
	public PlayersConfig pConfig;
	public PunishAPI punish;

	public MySQL sql;
	public Connection c;
	
	public HashMap<UUID, List<Map<String, String>>> mutes = new HashMap<UUID, List<Map<String, String>>>();
	
	@Override
	public void onEnable() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[SELBans] Loading plugin...");
		instance = this;

		if (!new File(this.getDataFolder() + File.separator + "config.yml").exists()) {
			saveDefaultConfig();
		}

		config = new Config();
		mysqlmethods = new MySQLMethods();
		pConfig = new PlayersConfig();
		punish = new PunishAPI();
		
		mySql();
		
		getCommand("sb").setExecutor(new AdminCommands());
		getCommand("selb").setExecutor(new AdminCommands());
		
		getServer().getPluginManager().registerEvents(new Listeners(), this);
		Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, new DatabaseCheck(), 60L, 20 * 30L);
		Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, new CountdownMute(), 60L, 20 * 1L);
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[SELBans] Plugin loaded!");
	}

	@Override
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[SELBans] Plugin shutdown successfully!");
	}
	
	public void mySql() {
		if (connect) {
			sql = new MySQL(this, config.dHost, config.dPort, config.dDatabase, config.dUser, config.dPass);
			attemptMySQLConnection();
		}
	}

	public void attemptMySQLConnection() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[SELBans] Attempting to connect to MySQL... This may take up to 30 seconds.");
		try {
			c = sql.openConnection();
			connect = true;
			Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[SELBans] Successfully connected to MySQL.");
			mysqlmethods.acreateTables();
		} catch (SQLException e) {
			e.printStackTrace();
			connect = false;
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[SELBans] MySQL Exception Error! Full functionality will not work until problem is resolved.");
		} catch (ClassNotFoundException c) {
			c.printStackTrace();
			connect = false;
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[SELBans] MySQL ClassNotFound Error! Full functionality will not work until problem is resolved.");
		}
	}
	
	public boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
