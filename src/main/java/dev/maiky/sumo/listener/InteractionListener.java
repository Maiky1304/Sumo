package dev.maiky.sumo.listener;

import dev.maiky.sumo.Sumo;
import dev.maiky.sumo.game.Game;
import dev.maiky.sumo.gui.SettingsGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Door: Maiky
 * Info: Sumo - 12 Apr 2021
 * Package: dev.maiky.sumo.listener
 */

public class InteractionListener implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Game game = Sumo.getGame();
		Action action = event.getAction();
		Player p = event.getPlayer();

		if (!action.toString().startsWith("RIGHT_CLICK")) return;
		if (p.getItemInHand() == null) return;

		ItemStack hand = p.getItemInHand();
		if (hand.equals(game.getSettingsItem())) {
			if (!game.getHosts().contains(p.getUniqueId())) {
				p.sendMessage("§cJij bent geen host, je kunt dit item niet gebruiken!");
				return;
			}

			SettingsGUI settingsGUI = new SettingsGUI();
			settingsGUI.getInventory().open(p);
		}
	}

}
