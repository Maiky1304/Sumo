package dev.maiky.sumo.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Door: Maiky
 * Info: Sumo - 11 Apr 2021
 * Package: dev.maiky.sumo.listener
 */

public class PreventMinecraftListener implements Listener {

	@EventHandler
	public void onHunger(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		event.setDamage(0);
	}

}
