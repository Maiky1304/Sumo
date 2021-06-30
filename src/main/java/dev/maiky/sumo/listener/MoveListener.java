package dev.maiky.sumo.listener;

import dev.maiky.sumo.Sumo;
import dev.maiky.sumo.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

/**
 * Door: Maiky
 * Info: Sumo - 11 Apr 2021
 * Package: dev.maiky.sumo.listener
 */

public class MoveListener implements Listener {

	@EventHandler
	public void onMoveDuringMatchCountdown(PlayerMoveEvent e) {
		Player p = e.getPlayer();

		Game game = Sumo.getGame();
		if (game.getNoMove().contains(p)) {
			if (e.getFrom().distanceSquared(e.getTo()) > 0.0D) {
				e.getPlayer().teleport(e.getFrom());
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		Location to = event.getTo();

		if (to.getBlock() == null) return;

		Block block = to.getBlock();
		Material type = block.getType();

		Game game = Sumo.getGame();
		List<Player> inDuel = game.getInDuel();

		if (!inDuel.contains(p)) {
			return;
		}

		if (!type.toString().contains("WATER")) return;

		game.handleMatchEnd(p);
	}

	@EventHandler
	public void onMove2(PlayerMoveEvent event) {
		Player p = event.getPlayer();

		Game game = Sumo.getGame();
		List<Player> inDuel = game.getInDuel();

		if (!inDuel.contains(p)) {
			return;
		}

		if (p.getLocation().getY() > game.getDuelEndY()) return;

		game.handleMatchEnd(p);
	}

}
