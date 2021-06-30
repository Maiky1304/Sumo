package dev.maiky.sumo.listener;

import dev.maiky.sumo.Sumo;
import dev.maiky.sumo.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Door: Maiky
 * Info: Sumo - 11 Apr 2021
 * Package: dev.maiky.sumo.listener
 */

public class QuitListener implements Listener {

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Player p = event.getPlayer();

		// Cancel Default Quit Message
		event.setQuitMessage("");

		// Add player to game list
		Game game = Sumo.getGame();
		game.getPlayerList().remove(p);

		// Broadcast
		game.broadcastMessage("&a" + p.getName() + " &fheeft de game verlaten. (&a" + game.getPlayerList().size() + "&f/&a"
				+ game.getMaxPlayers() + "&f)");
	}

}
