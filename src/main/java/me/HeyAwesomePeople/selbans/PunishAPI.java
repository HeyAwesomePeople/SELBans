package me.HeyAwesomePeople.selbans;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PunishAPI {
	public SELBans plugin = SELBans.instance;

	public String banPlayer(String playername, Date unbanDate, String reason) {
		reason.replace("'", "");
		if (plugin.pConfig.getPlayerUUID(playername) == null) return ChatColor.RED + "[PaC] Player(" + playername + ") has never played before!";

		Player online = Bukkit.getPlayer(playername);
		if (online != null) {
			if (unbanDate != null) {
				online.kickPlayer(reason);
				plugin.mysqlmethods.logPunish("ban", 1, unbanDate, reason, online.getUniqueId());
				Bukkit.broadcastMessage(ChatColor.BLUE + playername + ChatColor.GRAY + " has been banned for" + ChatColor.BLUE + countdown(unbanDate) + ChatColor.GRAY + " for " + ChatColor.BLUE + reason);
				return ChatColor.BLUE + "[PaC] Player(" + playername + ") has been banned until " + plugin.format.format(unbanDate) + ".";
			} else {
				online.kickPlayer(reason);
				plugin.mysqlmethods.logPunish("ban", 0, null, reason, online.getUniqueId());
				Bukkit.broadcastMessage(ChatColor.BLUE + playername + ChatColor.GRAY + " has been banned for " + ChatColor.BLUE + reason);
				return ChatColor.BLUE + "[PaC] Player(" + playername + ") has been banned!";
			}
		} else {
			UUID id = plugin.pConfig.getPlayerUUID(playername);
			if (id == null) {
				return ChatColor.RED + "[PaC] Player(" + playername + ") has never played before!";
			} else {
				if (unbanDate != null) {
					plugin.mysqlmethods.logPunish("ban", 1, unbanDate, reason, id);
					Bukkit.broadcastMessage(ChatColor.BLUE + playername + ChatColor.GRAY + " has been banned for" + ChatColor.BLUE + countdown(unbanDate) + ChatColor.GRAY + " for " + ChatColor.BLUE + reason);
					return ChatColor.BLUE + "[PaC] Player(" + playername + ") has been banned until " + plugin.format.format(unbanDate) + ".";
				} else {
					plugin.mysqlmethods.logPunish("ban", 0, null, reason, id);
					Bukkit.broadcastMessage(ChatColor.BLUE + playername + ChatColor.GRAY + " has been banned for " + ChatColor.BLUE + reason);
					return ChatColor.BLUE + "[PaC] Player(" + playername + ") has been banned!";
				}
			}
		}
	}

	public String kickPlayer(String playername, String reason) {
		reason.replace("'", "");
		if (plugin.pConfig.getPlayerUUID(playername) == null) return ChatColor.RED + "[PaC] Player(" + playername + ") has never played before!";
		Player online = Bukkit.getPlayer(playername);

		if (online != null) {
			online.kickPlayer(reason);
			plugin.mysqlmethods.logPunish("kick", 0, null, reason, online.getUniqueId());
			Bukkit.broadcastMessage(ChatColor.BLUE + playername + ChatColor.GRAY + " has been kicked for " + ChatColor.BLUE + reason);
			return ChatColor.BLUE + "[PaC] Player(" + playername + ") has been kicked!";
		} else {
			return ChatColor.RED + "[PaC] Player(" + playername + ") is offline! Cannot kick.";
		}
	}

	public String mutePlayer(String playername, Date unmuteDate, String reason) {
		reason.replace("'", "");
		if (plugin.pConfig.getPlayerUUID(playername) == null) return ChatColor.RED + "[PaC] Player(" + playername + ") has never played before!";

		final UUID id = plugin.pConfig.getPlayerUUID(playername);
		if (unmuteDate != null) {
			plugin.mysqlmethods.logPunish("mute", 1, unmuteDate, reason, id);
			Bukkit.broadcastMessage(ChatColor.BLUE + "" + playername + ChatColor.GRAY + " has been muted for" + ChatColor.BLUE + countdown(unmuteDate) + ChatColor.GRAY + " for " + ChatColor.BLUE + reason);
			return ChatColor.BLUE + "[PaC] Player(" + playername + ") has been muted until " + plugin.format.format(unmuteDate);
		} else {
			plugin.mysqlmethods.logPunish("mute", 0, null, reason, id);
			Bukkit.broadcastMessage(ChatColor.BLUE + "" + playername + ChatColor.GRAY + " has been muted for " + ChatColor.BLUE + reason);
			return ChatColor.BLUE + "[PaC] Player(" + playername + ") has been muted!";
		}

	}

	public String warnPlayer(String playername, String reason) {
		reason.replace("'", "");
		if (plugin.pConfig.getPlayerUUID(playername) == null) return ChatColor.RED + "[PaC] Player(" + playername + ") has never played before!";

		Player online = Bukkit.getPlayer(playername);
		if (online != null) {
			plugin.mysqlmethods.logPunish("warn", 0, null, reason, online.getUniqueId());
			online.sendMessage(ChatColor.RED + "[Warning] " + reason);
			Bukkit.broadcastMessage(ChatColor.BLUE + "" + playername + ChatColor.GRAY + " has been warned for " + ChatColor.BLUE + reason);
			return ChatColor.BLUE + "[PaC] Player(" + playername + ") has been warned!";
		} else {
			return ChatColor.RED + "[PaC] Player(" + playername + ") is offline! Cannot warn.";
		}
	}

	public String unmutePlayer(String playername) {
		if (plugin.pConfig.getPlayerUUID(playername) == null) return ChatColor.RED + "[PaC] Player(" + playername + ") has never played before!";

		final UUID id = plugin.pConfig.getPlayerUUID(playername);

		Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			public void run() {
				List<Map<String, String>> pu = sactivePunishments(id);
				for (Map<String, String> a : pu) {
					if (a.containsKey("muted")) {
						try {
							plugin.mysqlmethods.sunPunish("mute", plugin.format.parse(a.get("time")), id);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					} else if (a.containsKey("tempmuted")) {
						try {
							plugin.mysqlmethods.sunPunish("mute", plugin.format.parse(a.get("time")), id);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}

				plugin.mutes.put(id, plugin.mysqlmethods.sfetchLog("mutes", id.toString()));
			}
		});

		return ChatColor.BLUE + "[PaC] Player(" + playername + ") has been unmuted!";
	}

	public String unbanPlayer(String playername) {
		if (plugin.pConfig.getPlayerUUID(playername) == null) return ChatColor.RED + "[PaC] Player(" + playername + ") has never played before!";

		final UUID id = plugin.pConfig.getPlayerUUID(playername);

		Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			public void run() {
				List<Map<String, String>> pu = sactivePunishments(id);
				for (Map<String, String> a : pu) {
					if (a.containsKey("banned")) {
						try {
							plugin.mysqlmethods.sunPunish("ban", plugin.format.parse(a.get("time")), id);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					} else if (a.containsKey("tempbanned")) {
						try {
							plugin.mysqlmethods.sunPunish("ban", plugin.format.parse(a.get("time")), id);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
			}

		});
		return ChatColor.BLUE + "[PaC] Player(" + playername + ") has been unbaned!";
	}

	public List<Map<String, String>> sactivePunishments(UUID id) {
		List<Map<String, String>> punishments = new ArrayList<Map<String, String>>();
		List<Map<String, String>> bans = plugin.mysqlmethods.sfetchLog("bans", id.toString());
		List<Map<String, String>> mutes = plugin.mysqlmethods.sfetchLog("mutes", id.toString());

		if (bans != null) {
			for (Map<String, String> m : bans) {
				Map<String, String> map = new HashMap<String, String>();
				if (m.containsKey("None")) {
					continue;
				}
				if (m.get("Tempban").equalsIgnoreCase("0")) {
					map.put("banned", m.get("Reason"));
					map.put("time", m.get("Time"));
					punishments.add(map);
					continue;
				} else if (m.get("Tempban").equalsIgnoreCase("1")) {
					try {
						if (!hasTimePassed(plugin.format.parse(m.get("TempbanUnbanDate")))) {
							map.put("tempbanned", m.get("TempbanUnbanDate").toString());
							map.put("time", m.get("Time"));
							punishments.add(map);
							continue;
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}

		if (mutes != null) {
			for (Map<String, String> m : mutes) {
				Map<String, String> map = new HashMap<String, String>();
				if (m.containsKey("None")) {
					continue;
				}
				if (m.get("Tempmute").equalsIgnoreCase("0")) {
					map.put("muted", m.get("Reason"));
					map.put("time", m.get("Time"));
					punishments.add(map);
					continue;
				} else if (m.get("Tempmute").equalsIgnoreCase("1")) {
					try {
						if (!hasTimePassed(plugin.format.parse(m.get("TempmuteUnmuteDate")))) {
							map.put("tempmuted", m.get("TempmuteUnmuteDate").toString());
							map.put("time", m.get("Time"));
							punishments.add(map);
							continue;
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return punishments;
	}

	public Boolean hasTimePassed(Date time) {
		if (time.before(new Date())) {
			return true;
		} else {
			return false;
		}
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

	public void showBans(String playername, final CommandSender p,
			final Integer show) {
		if (plugin.pConfig.getPlayerUUID(playername) == null) {
			p.sendMessage(ChatColor.RED + "[PaC] Player(" + playername + ") has never played before!");
			return;
		}
		final UUID id = plugin.pConfig.getPlayerUUID(playername);
		p.sendMessage(ChatColor.BLUE + "[PaC] Loading bans...");
		Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			public void run() {
				Integer count = 0;

				List<Map<String, String>> bans = plugin.mysqlmethods.sfetchLog("bans", id.toString());

				if (bans == null) {
					p.sendMessage(ChatColor.BLUE + "[PaC] Player has no bans.");
					return;
				}
				if (bans.get(0).get("None") != null) {
					p.sendMessage(ChatColor.BLUE + "[PaC] Player has no bans.");
					return;
				}
				for (Map<String, String> m : bans) {
					if (count > show) {
						break;
					}
					if (m.get("Tempban").equals("0")) {
						p.sendMessage(ChatColor.RED + "[" + count + "][Banned] " + m.get("Time") + ChatColor.BLUE + " for " + ChatColor.RED + m.get("Reason"));
						count++;
						continue;
					} else if (m.get("Tempban").equals("1")) {
						try {
							if (hasTimePassed(plugin.format.parse(m.get("TempbanUnbanDate")))) {
								p.sendMessage(ChatColor.BLUE + "[" + count + "]" + ChatColor.RED + "[Tempban]" + ChatColor.BLUE + "[Expired] " + ChatColor.RED + m.get("Time") + ChatColor.BLUE + " for " + ChatColor.RED + m.get("Reason") + ChatColor.RED + ".");
								count++;
								continue;
							} else {
								p.sendMessage(ChatColor.RED + "[" + count + "][Tempban] " + m.get("Time") + ChatColor.BLUE + " for " + ChatColor.RED + m.get("Reason") + ".");
								count++;
								continue;
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
					} else if (m.get("Tempban").equals("2")) {
						p.sendMessage(ChatColor.BLUE + "[" + count + "]" + ChatColor.RED + "[Banned]" + ChatColor.BLUE + "[Excused] " + ChatColor.RED + m.get("Time") + ChatColor.BLUE + " for " + ChatColor.RED + m.get("Reason") + ".");
						count++;
						continue;
					}
				}
			}
		});
	}

	public void showKicks(String playername, final CommandSender p,
			final Integer show) {
		if (plugin.pConfig.getPlayerUUID(playername) == null) {
			p.sendMessage(ChatColor.RED + "[PaC] Player(" + playername + ") has never played before!");
			return;
		}
		final UUID id = plugin.pConfig.getPlayerUUID(playername);
		p.sendMessage(ChatColor.BLUE + "[PaC] Loading kicks...");
		Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			public void run() {
				Integer count = 0;

				List<Map<String, String>> kicks = plugin.mysqlmethods.sfetchLog("kicks", id.toString());

				if (kicks == null) {
					p.sendMessage(ChatColor.BLUE + "[PaC] Player has no kicks.");
					return;
				}

				if (kicks.get(0).get("None") != null) {
					p.sendMessage(ChatColor.BLUE + "[PaC] Player has no kicks.");
					return;
				}
				
				for (Map<String, String> m : kicks) {
					if (count > show) {
						break;
					}
					p.sendMessage(ChatColor.BLUE + "[" + count + "]" + ChatColor.RED + "[Kicked] " + m.get("Time") + ChatColor.BLUE + " for " + ChatColor.RED + m.get("Reason"));
					count++;
					continue;
				}
			}
		});
	}

	public void showMutes(String playername, final CommandSender p,
			final Integer show) {
		if (plugin.pConfig.getPlayerUUID(playername) == null) {
			p.sendMessage(ChatColor.RED + "[PaC] Player(" + playername + ") has never played before!");
			return;
		}
		final UUID id = plugin.pConfig.getPlayerUUID(playername);
		p.sendMessage(ChatColor.BLUE + "[PaC] Loading mutes...");
		Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			public void run() {
				Integer count = 0;

				List<Map<String, String>> mutes = plugin.mysqlmethods.sfetchLog("mutes", id.toString());

				if (mutes == null) {
					p.sendMessage(ChatColor.BLUE + "[PaC] Player has no mutes.");
					return;
				}
				
				if (mutes.get(0).get("None") != null) {
					p.sendMessage(ChatColor.BLUE + "[PaC] Player has no mutes.");
					return;
				}

				for (Map<String, String> m : mutes) {
					if (count > show) {
						break;
					}
					if (m.get("Tempmute").equals("0")) {
						p.sendMessage(ChatColor.BLUE + "[" + count + "]" + ChatColor.RED + "[Muted] " + m.get("Time") + ChatColor.BLUE + " for " + ChatColor.RED + m.get("Reason"));
						count++;
						continue;
					} else if (m.get("Tempmute").equals("1")) {
						try {
							if (hasTimePassed(plugin.format.parse(m.get("TempmuteUnmuteDate")))) {
								p.sendMessage(ChatColor.BLUE + "[" + count + "]" + ChatColor.RED + "[Muted]" + ChatColor.BLUE + "[Expired] " + ChatColor.RED + m.get("Time") + ChatColor.BLUE + " for " + ChatColor.RED + m.get("Reason") + ChatColor.BLUE + "" + ChatColor.RED + ".");
								count++;
								continue;
							} else {
								p.sendMessage(ChatColor.BLUE + "[" + count + "]" + ChatColor.RED + "[Muted] " + m.get("Time") + ChatColor.BLUE + " for " + ChatColor.RED + m.get("Reason") + ".");
								count++;
								continue;
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
					} else if (m.get("Tempmute").equals("2")) {
						p.sendMessage(ChatColor.BLUE + "[" + count + "]" + ChatColor.RED + "[Muted]" + ChatColor.BLUE + "[Excused] " + ChatColor.RED + m.get("Time") + ChatColor.BLUE + " for " + ChatColor.RED + m.get("Reason") + ".");
						count++;
						continue;
					}
				}
			}
		});
	}

	public void showWarns(String playername, final CommandSender p,
			final Integer show) {
		if (plugin.pConfig.getPlayerUUID(playername) == null) {
			p.sendMessage(ChatColor.RED + "[PaC] Player(" + playername + ") has never played before!");
			return;
		}
		final UUID id = plugin.pConfig.getPlayerUUID(playername);
		p.sendMessage(ChatColor.BLUE + "[PaC] Loading warns...");
		Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			public void run() {
				Integer count = 0;
				List<Map<String, String>> warns = plugin.mysqlmethods.sfetchLog("warns", id.toString());

				if (warns == null) {
					p.sendMessage(ChatColor.BLUE + "[PaC] Player has no warns.");
					return;
				}
				
				if (warns.get(0).get("None") != null) {
					p.sendMessage(ChatColor.BLUE + "[PaC] Player has no warns.");
					return;
				}

				for (Map<String, String> m : warns) {
					if (count > show) {
						break;
					}
					p.sendMessage(ChatColor.BLUE + "[" + count + "]" + ChatColor.RED + "[Warned] " + m.get("Time") + ChatColor.BLUE + " for " + ChatColor.RED + m.get("Reason"));
					count++;
					continue;
				}
			}
		});
	}

}
