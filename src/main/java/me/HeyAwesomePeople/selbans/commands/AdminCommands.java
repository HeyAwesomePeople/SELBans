package me.HeyAwesomePeople.selbans.commands;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.HeyAwesomePeople.selbans.SELBans;
import net.md_5.bungee.api.ChatColor;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommands implements CommandExecutor {

	public SELBans plugin = SELBans.instance;

	public boolean onCommand(final CommandSender d, Command cmd,
			String commandLabel, final String[] args) {
		if (d instanceof Player) {
			Player s = (Player) d;
			if (commandLabel.equalsIgnoreCase("sb") || commandLabel.equalsIgnoreCase("selb")) {
				if (args.length == 0) {
					s.sendMessage(ChatColor.BLUE + "PAC Action Commands");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb ban <player> (duration) <reason> - Ban player");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb unban <player> - Unban player");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb kick <player> <reason> - Kick player");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb mute <player> (duration) <reason> - Mute player");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb unmute <player> - Unmute player");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb warn <player> <reason> - Warn player");
					s.sendMessage(ChatColor.BLUE + "PAC Lookup Commands");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb bans <player> (amount) - View all bans on a player");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb kicks <player> (amount) - View all kicks on a player");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb mutes <player> (amount) - View all mutes on a player");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb warns <player> (amount) - View all warns on a player");
					s.sendMessage(ChatColor.BLUE + "PAC Other Commands");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb reload - Reloads config");
				} else {
					if (args.length == 1) {
						if (s.hasPermission("selbans.admin") || s.isOp()) {
							if (args[0].equalsIgnoreCase("reload")) {
								plugin.reloadConfig();
								s.sendMessage(ChatColor.BLUE + "[Pac] Config reloaded!");
								return false;
							}
						}
					}
					if (args.length >= 1) {
						if (s.hasPermission("selbans.support") || s.hasPermission("selbans.mod") || s.hasPermission("selbans.admin") || s.isOp()) {
							if (args[0].equalsIgnoreCase("kick")) {
								if (args.length <= 2) {
									s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb kick <player> <reason> - Kick player");
									return false;
								}
								if (args[1] == null || args[2] == null) {
									s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb kick <player> <reason> - Kick player");
									return false;
								}
								s.sendMessage(plugin.punish.kickPlayer(args[1], StringUtils.join(args, ' ', 2, args.length) + ChatColor.GRAY + " by: " + ChatColor.RED + s.getDisplayName()));
								return false;
							}
							if (args[0].equalsIgnoreCase("mute")) {
								if (args.length <= 2) {
									s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb mute <player> (duration) <reason> - Mute player");
									return false;
								}
								if (args[1] == null) {
									s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb mute <player> (duration) <reason> - Mute player");
									return false;
								}
								if (args[2] == null) {
									s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb mute <player> (duration) <reason> - Mute player");
									return false;
								}
								if (isValidTime(args[2]) && args.length >= 4) {
									s.sendMessage(plugin.punish.mutePlayer(args[1], nowAndString(args[2]), StringUtils.join(args, ' ', 3, args.length) + ChatColor.GRAY + " by: " + ChatColor.RED + s.getDisplayName()));
								} else if (!isValidTime(args[2]) && args.length >= 3) {
									s.sendMessage(plugin.punish.mutePlayer(args[1], null, StringUtils.join(args, ' ', 2, args.length) + ChatColor.GRAY + " by: " + ChatColor.RED + s.getDisplayName()));
								} else {
									s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb mute <player> (duration) <reason> - Mute player");
									return false;
								}
								return false;
							}
							if (args[0].equalsIgnoreCase("warn")) {
								if (args.length <= 2) {
									s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb warn <player> <reason> - Warn player");
									return false;
								}
								if (args[1] == null || args[2] == null) {
									s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb warn <player> <reason> - Warn player");
									return false;
								}
								s.sendMessage(plugin.punish.warnPlayer(args[1], StringUtils.join(args, ' ', 2, args.length) + ChatColor.GRAY + " by: " + ChatColor.RED + s.getDisplayName()));
								return false;
							}
							if (args[0].equalsIgnoreCase("unmute")) {
								if (args.length <= 1) {
									s.sendMessage(ChatColor.DARK_AQUA + "/sb unmute <player> - Unmute player");
									return false;
								}
								if (args[1] == null) {
									s.sendMessage(ChatColor.DARK_AQUA + "/sb unmute <player> - Unmute player");
									return false;
								}
								if (args.length == 2) {
									s.sendMessage(plugin.punish.unmutePlayer(args[1]));
								} else {
									s.sendMessage(ChatColor.DARK_AQUA + "/sb unmute <player>");
									return false;
								}
							}
						}
						if (s.hasPermission("selbans.mod") || s.hasPermission("selbans.admin") || s.isOp()) {
							if (args[0].equalsIgnoreCase("ban")) {
								if (args.length <= 2) {
									s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb ban <player> (duration) <reason> - Ban player");
									return false;
								}
								if (args[1] == null) {
									s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb ban <player> (duration) <reason> - Ban player");
									return false;
								}
								if (args[2] == null) {
									s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb ban <player> (duration) <reason> - Ban player");
									return false;
								}
								if (isValidTime(args[2]) && args.length >= 4) {
									s.sendMessage(plugin.punish.banPlayer(args[1], nowAndString(args[2]), StringUtils.join(args, ' ', 3, args.length) + ChatColor.GRAY + " by: " + ChatColor.RED + s.getDisplayName()));
								} else if (!isValidTime(args[2]) && args.length >= 3) {
									s.sendMessage(plugin.punish.banPlayer(args[1], null, StringUtils.join(args, ' ', 2, args.length) + ChatColor.GRAY + " by: " + ChatColor.RED + s.getDisplayName()));
								} else {
									s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb ban <player> (duration) <reason> - Ban player");
									return false;
								}
								return false;
							}
							if (args[0].equalsIgnoreCase("unban")) {
								if (args.length <= 1) {
									s.sendMessage(ChatColor.DARK_AQUA + "/sb unban <player> - Unban player");
									return false;
								}
								if (args[1] == null) {
									s.sendMessage(ChatColor.DARK_AQUA + "/sb unban <player> - Unban player");
									return false;
								}
								if (args.length == 2) {
									s.sendMessage(plugin.punish.unbanPlayer(args[1]));
								} else {
									s.sendMessage(ChatColor.DARK_AQUA + "/sb unban <player>");
									return false;
								}
							}
							if (args[0].equalsIgnoreCase("bans")) {
								if (args.length <= 1) {
									s.sendMessage(ChatColor.DARK_AQUA + "/sb bans <player> (amount) - View all bans on a player");
									return false;
								}
								if (args[1] == null) {
									s.sendMessage(ChatColor.DARK_AQUA + "/sb bans <player> (amount) - View all bans on a player");
									return false;
								}
								if (args.length == 3) {
									if (plugin.isInteger(args[2])) {
										plugin.punish.showBans(args[1], s, Integer.parseInt(args[2]));
									} else {
										s.sendMessage(ChatColor.RED + "[Pac] Amount of bans to show must be a number");
										return false;
									}
								} else if (args.length == 2) {
									plugin.punish.showBans(args[1], s, 10);
								}
							}
							if (args[0].equalsIgnoreCase("kicks")) {
								if (args.length <= 1) {
									s.sendMessage(ChatColor.DARK_AQUA + "/sb kicks <player> (amount) - View all kicks on a player");
									return false;
								}
								if (args[1] == null) {
									s.sendMessage(ChatColor.DARK_AQUA + "/sb kicks <player> (amount) - View all kicks on a player");
									return false;
								}
								if (args.length == 3) {
									if (plugin.isInteger(args[2])) {
										plugin.punish.showKicks(args[1], s, Integer.parseInt(args[2]));
									} else {
										s.sendMessage(ChatColor.RED + "[Pac] Amount of kicks to show must be a number");
										return false;
									}
								} else if (args.length == 2) {
									plugin.punish.showKicks(args[1], s, 10);
								}
							}
							if (args[0].equalsIgnoreCase("mutes")) {
								if (args.length <= 1) {
									s.sendMessage(ChatColor.DARK_AQUA + "/sb mutes <player> (amount) - View all mutes on a player");
									return false;
								}
								if (args[1] == null) {
									s.sendMessage(ChatColor.DARK_AQUA + "/sb mutes <player> (amount) - View all mutes on a player");
									return false;
								}
								if (args.length == 3) {
									if (plugin.isInteger(args[2])) {
										plugin.punish.showMutes(args[1], s, Integer.parseInt(args[2]));
									} else {
										s.sendMessage(ChatColor.RED + "[Pac] Amount of mutes to show must be a number");
										return false;
									}
								} else if (args.length == 2) {
									plugin.punish.showMutes(args[1], s, 10);
								}
							}
							if (args[0].equalsIgnoreCase("warns")) {
								if (args.length <= 1) {
									s.sendMessage(ChatColor.DARK_AQUA + "/sb warns <player> (amount) - View all warns on a player");
									return false;
								}
								if (args[1] == null) {
									s.sendMessage(ChatColor.DARK_AQUA + "/sb warns <player> (amount) - View all warns on a player");
									return false;
								}
								if (args.length == 3) {
									if (plugin.isInteger(args[2])) {
										plugin.punish.showWarns(args[1], s, Integer.parseInt(args[2]));
									} else {
										s.sendMessage(ChatColor.RED + "[Pac] Amount of warns to show must be a number");
										return false;
									}
								} else if (args.length == 2) {
									plugin.punish.showWarns(args[1], s, 10);
								}
							}
						}
						if (s.hasPermission("selbans.admin") || s.isOp()) {
							if (args[0].equalsIgnoreCase("db")) {
								if (args[2] == null || args.length > 3) {
									s.sendMessage(ChatColor.RED + "[Pac] Incorrect Usage. Please see /sb for correct usage");
									return false;
								}
								if (args[1].equalsIgnoreCase("host")) {
									plugin.config.setDatabaseValue("host", args[2]);
									s.sendMessage(ChatColor.BLUE + "[Pac] Set host to " + args[2]);
								} else if (args[1].equalsIgnoreCase("port")) {
									plugin.config.setDatabaseValue("port", args[2]);
									s.sendMessage(ChatColor.BLUE + "[Pac] Set port to " + args[2]);
								} else if (args[1].equalsIgnoreCase("database")) {
									plugin.config.setDatabaseValue("database", args[2]);
									s.sendMessage(ChatColor.BLUE + "[Pac] Set database to " + args[2]);
								} else if (args[1].equalsIgnoreCase("user")) {
									plugin.config.setDatabaseValue("user", args[2]);
									s.sendMessage(ChatColor.BLUE + "[Pac] Set user to " + args[2]);
								} else if (args[1].equalsIgnoreCase("pass")) {
									plugin.config.setDatabaseValue("pass", args[2]);
									s.sendMessage(ChatColor.BLUE + "[Pac] Set pass to " + args[2]);
								} else {
									s.sendMessage(ChatColor.RED + "[Pac] No such argument /sb db " + args[1]);
								}
								s.sendMessage(ChatColor.BLUE + "[PAC - Reminder] Reload server when all database values set to reconnect to MySQL.");
								return false;
							}
						}
					}
				}
			}
		} else {
			CommandSender s = (CommandSender) d;
			if (commandLabel.equalsIgnoreCase("sb") || commandLabel.equalsIgnoreCase("selb")) {
				if (args.length == 0) {
					s.sendMessage(ChatColor.BLUE + "PAC Database Commands");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb db host <host> - Set MySQL host");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb db port <port> - Set MySQL port");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb db database <database> - Set MySQL database");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb db user <user> - Set MySQL username");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb db pass <pass> - set MySQL password");
					s.sendMessage(ChatColor.BLUE + "PAC Action Commands");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb ban <player> (duration) <reason> - Ban player");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb unban <player> - Unban player");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb kick <player> <reason> - Kick player");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb mute <player> (duration) <reason> - Mute player");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb unmute <player> - Unmute player");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb warn <player> <reason> - Warn player");
					s.sendMessage(ChatColor.BLUE + "PAC Lookup Commands");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb bans <player> (amount) - View all bans on a player");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb kicks <player> (amount) - View all kicks on a player");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb mutes <player> (amount) - View all mutes on a player");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb warns <player> (amount) - View all warns on a player");
					s.sendMessage(ChatColor.BLUE + "PAC Other Commands");
					s.sendMessage(ChatColor.DARK_AQUA + "/sb reload - Reloads config");
				} else {
					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("reload")) {
							plugin.reloadConfig();
							s.sendMessage(ChatColor.BLUE + "[Pac] Config reloaded!");
							return false;
						}
					}
					if (args.length >= 1) {
						if (args[0].equalsIgnoreCase("unmute")) {
							if (args.length <= 1) {
								s.sendMessage(ChatColor.DARK_AQUA + "/sb unmute <player> - Unmute player");
								return false;
							}
							if (args[1] == null) {
								s.sendMessage(ChatColor.DARK_AQUA + "/sb unmute <player> - Unmute player");
								return false;
							}
							if (args.length == 2) {
								s.sendMessage(plugin.punish.unmutePlayer(args[1]));
							} else {
								s.sendMessage(ChatColor.DARK_AQUA + "/sb unmute <player>");
								return false;
							}
						}
						if (args[0].equalsIgnoreCase("unban")) {
							if (args.length <= 1) {
								s.sendMessage(ChatColor.DARK_AQUA + "/sb unban <player> - Unban player");
								return false;
							}
							if (args[1] == null) {
								s.sendMessage(ChatColor.DARK_AQUA + "/sb unban <player> - Unban player");
								return false;
							}
							if (args.length == 2) {
								s.sendMessage(plugin.punish.unbanPlayer(args[1]));
							} else {
								s.sendMessage(ChatColor.DARK_AQUA + "/sb unban <player>");
								return false;
							}
						}
						if (args[0].equalsIgnoreCase("bans")) {
							if (args.length <= 1) {
								s.sendMessage(ChatColor.DARK_AQUA + "/sb bans <player> (amount) - View all bans on a player");
								return false;
							}
							if (args[1] == null) {
								s.sendMessage(ChatColor.DARK_AQUA + "/sb bans <player> (amount) - View all bans on a player");
								return false;
							}
							if (args.length == 3) {
								if (plugin.isInteger(args[2])) {
									plugin.punish.showBans(args[1], s, Integer.parseInt(args[2]));
								} else {
									s.sendMessage(ChatColor.RED + "[Pac] Amount of bans to show must be a number");
									return false;
								}
							} else if (args.length == 2) {
								plugin.punish.showBans(args[1], s, 10);
							}
						}
						if (args[0].equalsIgnoreCase("kicks")) {
							if (args.length <= 1) {
								s.sendMessage(ChatColor.DARK_AQUA + "/sb kicks <player> (amount) - View all kicks on a player");
								return false;
							}
							if (args[1] == null) {
								s.sendMessage(ChatColor.DARK_AQUA + "/sb kicks <player> (amount) - View all kicks on a player");
								return false;
							}
							if (args.length == 3) {
								if (plugin.isInteger(args[2])) {
									plugin.punish.showKicks(args[1], s, Integer.parseInt(args[2]));
								} else {
									s.sendMessage(ChatColor.RED + "[Pac] Amount of kicks to show must be a number");
									return false;
								}
							} else if (args.length == 2) {
								plugin.punish.showKicks(args[1], s, 10);
							}
						}
						if (args[0].equalsIgnoreCase("mutes")) {
							if (args.length <= 1) {
								s.sendMessage(ChatColor.DARK_AQUA + "/sb mutes <player> (amount) - View all mutes on a player");
								return false;
							}
							if (args[1] == null) {
								s.sendMessage(ChatColor.DARK_AQUA + "/sb mutes <player> (amount) - View all mutes on a player");
								return false;
							}
							if (args.length == 3) {
								if (plugin.isInteger(args[2])) {
									plugin.punish.showMutes(args[1], s, Integer.parseInt(args[2]));
								} else {
									s.sendMessage(ChatColor.RED + "[Pac] Amount of mutes to show must be a number");
									return false;
								}
							} else if (args.length == 2) {
								plugin.punish.showMutes(args[1], s, 10);
							}
						}
						if (args[0].equalsIgnoreCase("warns")) {
							if (args.length <= 1) {
								s.sendMessage(ChatColor.DARK_AQUA + "/sb warns <player> (amount) - View all warns on a player");
								return false;
							}
							if (args[1] == null) {
								s.sendMessage(ChatColor.DARK_AQUA + "/sb warns <player> (amount) - View all warns on a player");
								return false;
							}
							if (args.length == 3) {
								if (plugin.isInteger(args[2])) {
									plugin.punish.showWarns(args[1], s, Integer.parseInt(args[2]));
								} else {
									s.sendMessage(ChatColor.RED + "[Pac] Amount of warns to show must be a number");
									return false;
								}
							} else if (args.length == 2) {
								plugin.punish.showWarns(args[1], s, 10);
							}
						}
						if (args[0].equalsIgnoreCase("db")) {
							if (args[2] == null || args.length > 3) {
								s.sendMessage(ChatColor.RED + "[Pac] Incorrect Usage. Please see /sb for correct usage");
								return false;
							}
							if (args[1].equalsIgnoreCase("host")) {
								plugin.config.setDatabaseValue("host", args[2]);
								s.sendMessage(ChatColor.BLUE + "[Pac] Set host to " + args[2]);
							} else if (args[1].equalsIgnoreCase("port")) {
								plugin.config.setDatabaseValue("port", args[2]);
								s.sendMessage(ChatColor.BLUE + "[Pac] Set port to " + args[2]);
							} else if (args[1].equalsIgnoreCase("database")) {
								plugin.config.setDatabaseValue("database", args[2]);
								s.sendMessage(ChatColor.BLUE + "[Pac] Set database to " + args[2]);
							} else if (args[1].equalsIgnoreCase("user")) {
								plugin.config.setDatabaseValue("user", args[2]);
								s.sendMessage(ChatColor.BLUE + "[Pac] Set user to " + args[2]);
							} else if (args[1].equalsIgnoreCase("pass")) {
								plugin.config.setDatabaseValue("pass", args[2]);
								s.sendMessage(ChatColor.BLUE + "[Pac] Set pass to " + args[2]);
							} else {
								s.sendMessage(ChatColor.RED + "[Pac] No such argument /sb db " + args[1]);
							}
							s.sendMessage(ChatColor.BLUE + "[PAC - Reminder] Reload server when all database values set to reconnect to MySQL.");
							return false;
						}
						if (args[0].equalsIgnoreCase("kick")) {
							if (args.length <= 2) {
								s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb kick <player> <reason> - Kick player");
								return false;
							}
							if (args[1] == null || args[2] == null) {
								s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb kick <player> <reason> - Kick player");
								return false;
							}
							s.sendMessage(plugin.punish.kickPlayer(args[1], StringUtils.join(args, ' ', 2, args.length) + " by CONSOLE"));
							return false;
						}
						if (args[0].equalsIgnoreCase("ban")) {
							if (args.length <= 2) {
								s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb ban <player> (duration) <reason> - Ban player");
								return false;
							}
							if (args[1] == null) {
								s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb ban <player> (duration) <reason> - Ban player");
								return false;
							}
							if (args[2] == null) {
								s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb ban <player> (duration) <reason> - Ban player");
								return false;
							}
							if (isValidTime(args[2]) && args.length >= 4) {
								s.sendMessage(plugin.punish.banPlayer(args[1], nowAndString(args[2]), StringUtils.join(args, ' ', 3, args.length) + " by CONSOLE"));
							} else if (!isValidTime(args[2]) && args.length >= 3) {
								s.sendMessage(plugin.punish.banPlayer(args[1], null, StringUtils.join(args, ' ', 2, args.length) + " by CONSOLE"));
							} else {
								s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb ban <player> (duration) <reason> - Ban player");
								return false;
							}
							return false;
						}
						if (args[0].equalsIgnoreCase("mute")) {
							if (args.length <= 2) {
								s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb mute <player> (duration) <reason> - Mute player");
								return false;
							}
							if (args[1] == null) {
								s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb mute <player> (duration) <reason> - Mute player");
								return false;
							}
							if (args[2] == null) {
								s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb mute <player> (duration) <reason> - Mute player");
								return false;
							}
							if (isValidTime(args[2]) && args.length >= 4) {
								s.sendMessage(plugin.punish.mutePlayer(args[1], nowAndString(args[2]), StringUtils.join(args, ' ', 3, args.length) + " by CONSOLE"));
							} else if (!isValidTime(args[2]) && args.length >= 3) {
								s.sendMessage(plugin.punish.mutePlayer(args[1], null, StringUtils.join(args, ' ', 2, args.length) + " by CONSOLE"));
							} else {
								s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb mute <player> (duration) <reason> - Mute player");
								return false;
							}
							return false;
						}
						if (args[0].equalsIgnoreCase("warn")) {
							if (args.length <= 2) {
								s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb warn <player> <reason> - Warn player");
								return false;
							}
							if (args[1] == null || args[2] == null) {
								s.sendMessage(ChatColor.RED + "[Pac] Usage: /sb warn <player> <reason> - Warn player");
								return false;
							}
							s.sendMessage(plugin.punish.warnPlayer(args[1], StringUtils.join(args, ' ', 2, args.length) + " by CONSOLE"));
							return false;
						}
					}
				}
			}
		}
		return false;
	}

	public static boolean isValidTime(String input) {
		@SuppressWarnings("unused")
		String[] names = { "year", "years", "month", "months", "day", "days", "hour", "hours", "minute", "minutes", "second", "seconds" };

		Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?", 2);

		Matcher m = timePattern.matcher(input);
		while (m.find()) {
			if ((m.group() != null) && (!m.group().isEmpty())) {
				for (int i = 0; i < m.groupCount(); i++) {
					if ((m.group(i) != null) && (!m.group(i).isEmpty())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static Date nowAndString(String input) {
		boolean found = false;

		int years = 0;
		int months = 0;
		int weeks = 0;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		Date now = new Date();

		Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?", 2);

		Matcher m = timePattern.matcher(input);
		while (m.find()) {
			if ((m.group() != null) && (!m.group().isEmpty())) {
				for (int i = 0; i < m.groupCount(); i++) {
					if ((m.group(i) != null) && (!m.group(i).isEmpty())) {
						found = true;
					}
					if (found) {
						if ((m.group(1) != null) && (!m.group(1).isEmpty())) {
							years = Integer.parseInt(m.group(1));
						}
						if ((m.group(2) != null) && (!m.group(2).isEmpty())) {
							months = Integer.parseInt(m.group(2));
						}
						if ((m.group(3) != null) && (!m.group(3).isEmpty())) {
							weeks = Integer.parseInt(m.group(3));
						}
						if ((m.group(4) != null) && (!m.group(4).isEmpty())) {
							days = Integer.parseInt(m.group(4));
						}
						if ((m.group(5) != null) && (!m.group(5).isEmpty())) {
							hours = Integer.parseInt(m.group(5));
						}
						if ((m.group(6) != null) && (!m.group(6).isEmpty())) {
							minutes = Integer.parseInt(m.group(6));
						}
						if ((m.group(7) == null) || (m.group(7).isEmpty())) {
							break;
						}
						seconds = Integer.parseInt(m.group(7));
						break;
					}
				}
			}
		}
		Calendar c = Calendar.getInstance();
		if (years > 0) {
			c.add(1, years);
		}
		if (months > 0) {
			c.add(2, months);
		}
		if (weeks > 0) {
			c.add(3, weeks);
		}
		if (days > 0) {
			c.add(5, days);
		}
		if (hours > 0) {
			c.add(11, hours);
		}
		if (minutes > 0) {
			c.add(12, minutes);
		}
		if (seconds > 0) {
			c.add(13, seconds);
		}
		now = c.getTime();
		return now;
	}
}
