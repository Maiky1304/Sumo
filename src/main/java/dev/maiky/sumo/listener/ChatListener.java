package dev.maiky.sumo.listener;

import dev.maiky.sumo.Sumo;
import dev.maiky.sumo.game.Game;
import dev.maiky.sumo.game.StatsManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Door: Maiky
 * Info: Sumo - 12 Apr 2021
 * Package: dev.maiky.sumo.listener
 */

public class ChatListener implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		String message = event.getMessage();
		Game game = Sumo.getGame();

		// Cancel Minecraft Chat System
		// Without disabling Console logging
		event.getRecipients().clear();

		// Send custom message
		String nameColor = "";

		if (game.getHosts().contains(event.getPlayer().getUniqueId())) {
			nameColor = "§4";
		} else {
			if (game.isAlive(event.getPlayer())) {
				nameColor = "§c";
			} else {
				nameColor = "§7";
			}
		}

		TextComponent playerName = new TextComponent(nameColor + event.getPlayer().getPlayerListName());
		TextComponent column = new TextComponent("§f: ");
		TextComponent text = new TextComponent(message);

		// Set events for hovering
		TextComponent stats = new TextComponent(
				"§7§m--------------------------\n" +
				"§dWins: §f" + game.getStatsManager().getStatistic(event.getPlayer().getUniqueId(), StatsManager.Statistic.WIN) + "\n" +
						"§dVerloren: §f" + game.getStatsManager().getStatistic(event.getPlayer().getUniqueId(), StatsManager.Statistic.LOSE) + "\n" +
						"§dEvents gewonnen: §f" + game.getStatsManager().getStatistic(event.getPlayer().getUniqueId(), StatsManager.Statistic.EVENT_WIN) + "\n" +
						"§7§m--------------------------"
		);
		playerName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new BaseComponent[]{stats}));

		// Set events for hovering text
		text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new BaseComponent[]{new TextComponent("§7" + new SimpleDateFormat("HH:mm:ss").format(new Date()))}));

		// Send
		game.getPlayerList().forEach(user -> user.spigot().sendMessage(playerName, column, text));
	}

}
