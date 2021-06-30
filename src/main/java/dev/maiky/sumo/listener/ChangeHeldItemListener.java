package dev.maiky.sumo.listener;

import dev.maiky.sumo.Sumo;
import dev.maiky.sumo.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

/**
 * Door: Maiky
 * Info: Sumo - 12 Apr 2021
 * Package: dev.maiky.sumo.listener
 */

public class ChangeHeldItemListener implements Listener {

	@EventHandler
	public void onHeldChange(PlayerItemHeldEvent event) {
		Player p = event.getPlayer();
		Game game = Sumo.getGame();

		if (game.getInDuel().contains(p) && game.isKnockbackStick()) {
			event.setCancelled(true);
		}
	}

}
