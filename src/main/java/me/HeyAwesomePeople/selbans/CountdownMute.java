package me.HeyAwesomePeople.selbans;

import java.util.UUID;


public class CountdownMute implements Runnable {
	public SELBans plugin = SELBans.instance;

	public void run() {
		for (UUID uid : plugin.timeout.keySet()) {
			if (plugin.timeout.get(uid) <= 0) {
				plugin.timeout.remove(uid);
			} else {
				plugin.timeout.put(uid, plugin.timeout.get(uid) - 1);
			}
		}
	}

}
