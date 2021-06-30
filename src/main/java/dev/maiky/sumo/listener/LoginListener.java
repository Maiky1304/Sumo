package dev.maiky.sumo.listener;

import dev.maiky.sumo.Sumo;
import dev.maiky.sumo.game.Game;
import dev.maiky.sumo.game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

/**
 * Door: Maiky
 * Info: Sumo - 11 Apr 2021
 * Package: dev.maiky.sumo.listener
 */

public class LoginListener implements Listener {

	@EventHandler
	public void onLogin(AsyncPlayerPreLoginEvent event) {
		if (Sumo.getGame() == null) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "mGameCore: Something went wrong, contact Maiky#0001");
			return;
		}

		Game game = Sumo.getGame();
		if (game.getGameState() == null) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "mGameCore: Something went wrong, contact Maiky#0001");
			return;
		}

		GameState state = game.getGameState();
		if (state == GameState.INGAME && !game.getHosts().contains(event.getUniqueId())) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "Deze game server is momenteel al bezig, probeer het later nogmaals.");
		}
	}

}
