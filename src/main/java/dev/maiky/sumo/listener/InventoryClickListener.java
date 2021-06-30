package dev.maiky.sumo.listener;

import dev.maiky.sumo.Sumo;
import dev.maiky.sumo.game.Game;
import dev.maiky.sumo.game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Door: Maiky
 * Info: Sumo - 12 Apr 2021
 * Package: dev.maiky.sumo.listener
 */

public class InventoryClickListener implements Listener {

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Game game = Sumo.getGame();
		if (game.getGameState() != GameState.DEVMODE) {
			event.setCancelled(true);
		}
	}

}
