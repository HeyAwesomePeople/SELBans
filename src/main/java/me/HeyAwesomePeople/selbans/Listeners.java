package me.HeyAwesomePeople.selbans;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class Listeners implements Listener {

	public SELBans plugin = SELBans.instance;

	@EventHandler
	public void playerLogin(PlayerLoginEvent e) {
		final Player p = e.getPlayer();
		plugin.pConfig.addPlayerToConfig(p);
		String banned = checkRefuseJoin(p);
		if (banned != null) {
			e.disallow(Result.KICK_BANNED, banned);
		}
		Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			public void run() {
				UUID id = p.getUniqueId();
				plugin.mutes.put(id, plugin.mysqlmethods.sfetchLog("mutes", id.toString()));
			}

		});
	}

	@EventHandler
	public void playerChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (plugin.timeout.containsKey(p.getUniqueId())) {
			e.setCancelled(true);
			return;
		}
		if (canPlayerChat(p) == false) {
			e.setCancelled(true);
		}
	}

	public boolean canPlayerChat(final Player p) {
		if (plugin.mutes.get(p.getUniqueId()) == null) {
			Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
				public void run() {
					UUID id = p.getUniqueId();
					plugin.mutes.put(id, plugin.mysqlmethods.sfetchLog("mutes", id.toString()));
				}
			});
			return false;
		}
		List<Map<String, String>> pu = plugin.mutes.get(p.getUniqueId());
		for (Map<String, String> a : pu) {
			if (a.containsKey("None")) {
				return true;
			}
			if (a.containsKey("Tempmute")) {
				if (a.get("Tempmute").equals("0")) {
					p.sendMessage(ChatColor.RED + "[PAC] You are muted for: " + a.get("Reason"));
					plugin.timeout.put(p.getUniqueId(), 15);
					return false;
				} else if (a.get("Tempmute").equals("1")) {
					try {
						if (!plugin.punish.hasTimePassed(plugin.format.parse(a.get("TempmuteUnmuteDate")))) {
							p.sendMessage(ChatColor.RED + "[PAC] You are muted until " + a.get("TempmuteUnmuteDate"));
							plugin.timeout.put(p.getUniqueId(), 15);
							return false;
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else {
					return true;
				}
			}
		}
		return true;
	}

	// TODO not async
	public String checkRefuseJoin(Player p) {
		List<Map<String, String>> pu = plugin.punish.sactivePunishments(p.getUniqueId());
		for (Map<String, String> a : pu) {
			if (a.containsKey("banned")) {
				return ChatColor.RED + "[PAC] You are banned for: " + a.get("banned");
			} else if (a.containsKey("tempbanned")) {
				try {
					return ChatColor.RED + "[PAC] You are banned for" + countdown(plugin.format.parse(a.get("tempbanned")));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public String countdown(Date mute) {
		Date date = new Date();

		Date now = mute;
		Date start = date;

		long diff = now.getTime() - start.getTime();

		long dS = diff / 1000 % 60;
		long dM = diff / (60 * 1000) % 60;
		long dH = diff / (60 * 60 * 1000) % 24;
		long dD = diff / (24 * 60 * 60 * 1000);

		String time = "";
		if (dD != 0) {
			time = time + " " + dD + "d";
		}
		if (dH != 0) {
			time = time + " " + dH + "h";
		}
		if (dM != 0) {
			time = time + " " + dM + "m";
		}
		if (dS != 0) {
			time = time + " " + dS + "s";
		}
		return time;
	}
}
