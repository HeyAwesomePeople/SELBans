package me.HeyAwesomePeople.selbans;

import java.sql.SQLException;

public class DatabaseCheck implements Runnable {
	public SELBans plugin = SELBans.instance;

	public void run() {
		java.sql.PreparedStatement statement;
		try {
			statement = plugin.sql.openConnection().prepareStatement("CREATE TABLE IF NOT EXISTS bans (PlayerId tinytext, TimeBanned datetime, Tempban boolean, TempbanUnbanDate datetime, Reason tinytext)");
			statement.executeUpdate();
		} catch (SQLException sqlE) {
			sqlE.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}
