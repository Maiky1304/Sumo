package dev.maiky.sumo.listener;

import dev.maiky.sumo.Sumo;
import dev.maiky.sumo.game.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Door: Maiky
 * Info: Sumo - 12 Apr 2021
 * Package: dev.maiky.sumo.listener
 */

public class CPSListener implements Listener {

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Game game = Sumo.getGame();

		if (e.getAction().toString().startsWith("LEFT")) {
			game.getCps().merge(e.getPlayer(), 1, Integer::sum);
		}
	}

}
