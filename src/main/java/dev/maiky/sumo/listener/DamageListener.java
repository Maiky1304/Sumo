package dev.maiky.sumo.listener;

import dev.maiky.sumo.Sumo;
import dev.maiky.sumo.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Door: Maiky
 * Info: Sumo - 11 Apr 2021
 * Package: dev.maiky.sumo.listener
 */

public class DamageListener implements Listener {

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		Player player = (Player) event.getEntity();
		Game game = Sumo.getGame();

		if (!game.getInDuel().contains(player)) {
			event.setCancelled(true);
			return;
		}

		if (game.getNoMove().contains(player)) {
			event.setCancelled(true);
			return;
		}

		event.setCancelled(false);
	}

}
