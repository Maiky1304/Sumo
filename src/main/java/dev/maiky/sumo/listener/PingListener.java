package dev.maiky.sumo.listener;

import dev.maiky.sumo.Sumo;
import dev.maiky.sumo.game.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * Door: Maiky
 * Info: Sumo - 11 Apr 2021
 * Package: dev.maiky.sumo.listener
 */

public class PingListener implements Listener {

	@EventHandler
	public void onPing(ServerListPingEvent event) {
		Game game = Sumo.getGame();

		event.setMaxPlayers(game.getMaxPlayers());
		event.setMotd(game.getGameState().toString() + "\n" + game.getGameId());
	}

}
