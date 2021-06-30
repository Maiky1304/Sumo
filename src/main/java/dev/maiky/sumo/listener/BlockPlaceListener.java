package dev.maiky.sumo.listener;

import dev.maiky.sumo.Sumo;
import dev.maiky.sumo.game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Door: Maiky
 * Info: Sumo - 12 Apr 2021
 * Package: dev.maiky.sumo.listener
 */

public class BlockPlaceListener implements Listener {

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (Sumo.getGame().getGameState() != GameState.DEVMODE) {
			event.setCancelled(true);
		}
	}

}
