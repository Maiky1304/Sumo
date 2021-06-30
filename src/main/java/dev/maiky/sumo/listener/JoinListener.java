package dev.maiky.sumo.listener;

import dev.maiky.sumo.Sumo;
import dev.maiky.sumo.game.Game;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Door: Maiky
 * Info: Sumo - 11 Apr 2021
 * Package: dev.maiky.sumo.listener
 */

public class JoinListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();

		// Cancel Default Join Message
		event.setJoinMessage("");

		// Add player to game list
		Game game = Sumo.getGame();
		game.getPlayerList().add(p);
		game.getStatsManager().insertUser(p.getUniqueId());

		// GameMode
		p.setGameMode(GameMode.SURVIVAL);

		// Update Others
		game.getScoreboardManager().broadcastUpdate(4, game.getScoreboardManager().f("&fSpelers: &c" + game.getPlayerList().size() + "/" + game.getMaxPlayers()));

		// Teleport to Spawn Location
		Location location = (Location) Sumo.getLocations().getConfig().get("spawn");
		if (location != null) {
			p.teleport(location);
		}

		// Disable Fly
		p.setAllowFlight(Sumo.getSumo().getConfig().getBoolean("dev-mode"));

		// Set Health & Hunger
		p.setHealth(p.getMaxHealth());
		p.setFoodLevel(20);

		// Reset Level
		p.setLevel(0);

		// Clear Inventory
		p.getInventory().clear();
		if (game.getHosts().contains(p.getUniqueId())) {
			game.giveHostItems(p);
			p.setAllowFlight(true);
		}

		// Scoreboard
		game.getScoreboardManager().initializeScoreboard(p);

		// Broadcast
		game.broadcastMessage("&a" + p.getName() + " &fis de game gejoined. (&a" + game.getPlayerList().size() + "&f/&a"
		+ game.getMaxPlayers() + "&f)");
	}

}
