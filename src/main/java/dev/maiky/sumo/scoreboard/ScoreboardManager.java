package dev.maiky.sumo.scoreboard;

import dev.maiky.sumo.Sumo;
import dev.maiky.sumo.game.Game;
import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

/**
 * Door: Maiky
 * Info: Sumo - 12 Apr 2021
 * Package: dev.maiky.sumo.scoreboard
 */

public class ScoreboardManager {

	public void initializeScoreboard(Player p) {
		Game game = Sumo.getGame();
		BPlayerBoard board = Netherboard.instance().createBoard(p, f("&5&lSUMO &7(" + Sumo.getGame().getGameId() + ")"));

		board.getScoreboard().registerNewTeam("death");
		board.getScoreboard().registerNewTeam("alive");
		board.getScoreboard().registerNewTeam("host");

		Team death = board.getScoreboard().getTeam("death");
		Team alive = board.getScoreboard().getTeam("alive");
		Team host = board.getScoreboard().getTeam("host");

		death.setPrefix("§7");
		alive.setPrefix("§c");
		host.setPrefix("§4");

		game.getDeathPlayers().forEach(death::addPlayer);
		game.getPlayerList().forEach(user -> {
			if (!game.getDeathPlayers().contains(user)) {
				alive.addPlayer(user);
			}

			if (game.getHosts().contains(user.getUniqueId())) {
				if (alive.getPlayers().contains(user)) {
					alive.removePlayer(user);
				}

				if (death.getPlayers().contains(user)) {
					death.removePlayer(user);
				}

				host.addPlayer(user);
			}
		});

		death.setNameTagVisibility(NameTagVisibility.ALWAYS);
		alive.setNameTagVisibility(NameTagVisibility.ALWAYS);
		host.setNameTagVisibility(NameTagVisibility.ALWAYS);

		board.set(f("&7&m---------------------"), 9);
		board.set("§1", 8);
		board.set(f("&dStatus:"), 7);
		board.set(f("&f" + game.getGameState().getLabel()), 6);
		board.set("§2", 5);
		board.set(f("&fSpelers: &d" + game.getPlayerList().size() + "/" + game.getMaxPlayers()), 4);
		board.set("§3", 3);
		board.set("§dplay.blackmt.nl", 2);
		board.set(f("&7&m--------------------"), 1);
	}

	public void restoreDefault() {
		Game game = Sumo.getGame();
		game.getPlayerList().forEach(user ->
		{
			BPlayerBoard board = Netherboard.instance().getBoard(user);

			if (board != null) {
				board.set(f("&7&m---------------------"), 9);
				board.set("§1", 8);
				board.set(f("&dStatus:"), 7);
				board.set(f("&f" + game.getGameState().getLabel()), 6);
				board.set("§2", 5);
				board.set(f("&fSpelers: &d" + game.getPlayerList().size() + "/" + game.getMaxPlayers()), 4);
				board.set("§3", 3);
				board.set("§dplay.blackmt.nl", 2);
				board.set(f("&7&m--------------------"), 1);
			}
		});
	}

	public void broadcastUpdate(int line, String text) {
		Game game = Sumo.getGame();
		game.getPlayerList().forEach(user ->
		{
			BPlayerBoard board = Netherboard.instance().getBoard(user);

			if (board != null) {

				game.getPlayerList().forEach(user2 -> {
					Team death = board.getScoreboard().getTeam("death");
					Team alive = board.getScoreboard().getTeam("alive");
					Team host = board.getScoreboard().getTeam("host");

					if (game.getDeathPlayers().contains(user2) && !death.getPlayers().contains(user2)) {
						death.addPlayer(user2);
						if (alive.getPlayers().contains(user2)) {
							alive.removePlayer(user2);
						}
					}

					if (!game.getDeathPlayers().contains(user2) && !alive.getPlayers().contains(user2)) {
						alive.addPlayer(user2);
						if (death.getPlayers().contains(user2)) {
							death.removePlayer(user2);
						}
					}

					if (game.getHosts().contains(user2.getUniqueId())) {
						if (alive.getPlayers().contains(user2)) {
							alive.removePlayer(user2);
						}

						if (death.getPlayers().contains(user2)) {
							death.removePlayer(user2);
						}
						host.addPlayer(user2);
					}
				});

				board.set(f(text), line);
			}
		});
	}

	public String f(String s) {
		return ChatColor.translateAlternateColorCodes('&',s);
	}

}
