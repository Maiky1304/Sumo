package dev.maiky.sumo.gui;

import dev.maiky.blackmtcore.classes.inventories.ClickableItem;
import dev.maiky.blackmtcore.classes.inventories.SmartInventory;
import dev.maiky.blackmtcore.classes.inventories.content.InventoryContents;
import dev.maiky.blackmtcore.classes.inventories.content.InventoryProvider;
import dev.maiky.blackmtcore.classes.inventories.content.SlotPos;
import dev.maiky.blackmtcore.classes.nbtbuilder.NBTBuilder;
import dev.maiky.sumo.Sumo;
import dev.maiky.sumo.game.Game;
import dev.maiky.sumo.game.GameState;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Door: Maiky
 * Info: Sumo - 12 Apr 2021
 * Package: dev.maiky.sumo.gui
 */

public class SettingsGUI implements InventoryProvider {

	@Getter
	private final SmartInventory inventory;

	public SettingsGUI(){
		this.inventory = SmartInventory.builder()
				.id("settings")
				.title("&d&lSumo &8- &7Instellingen")
				.size(1, 9)
				.provider(this)
				.build();
	}

	@Override
	public void init(Player player, InventoryContents inventoryContents) {
		Game game = Sumo.getGame();

		ItemStack kbStick = new NBTBuilder(Material.FERMENTED_SPIDER_EYE)
				.setName("&bKnockback Stick")
				.setLore("","&7Geef spelers een Knockback stok","&7bij het sumo gevecht!",
						"","&7Huidige status: " + (game.isKnockbackStick() ? "&aIngeschakeld" : "&cUitgeschakeld"))
				.build();
		ItemStack maxPlayers = new NBTBuilder(Material.PAPER)
				.setName("&bSpelers")
				.setLore("", "&7Verander maximale spelers die kunnen",
						"&7joinen in de gameserver.", "",
						"&7Huidige waarde: &f" + game.getMaxPlayers(),
						"","&a+1 &7met linkermuisknop",
						"&c-1 &7met rechtermuisknop")
				.build();
		ItemStack stopServer = new NBTBuilder(Material.BARRIER)
				.setName("&cStop gameserver")
				.setLore("", "&7Stop hiermee de gehele gameserver",
						"&7iedereen wordt dan naar de lobby gesend.")
				.build();
		ItemStack status;
		if (game.getGameState() == GameState.DEVMODE) {
			status = new NBTBuilder(Material.STAINED_CLAY)
					.setDurability((short)9).setName("&eDeveloper Mode")
			.setLore("", "&7Klik om uit te zetten & te restarten").build();
		} else if (game.getGameState() == GameState.WAITING) {
			status = new NBTBuilder(Material.STAINED_CLAY)
					.setDurability((short)4).setName("&aGame")
					.setLore("&7Klik om game te &astarten&7.").build();
		} else {
			status = new NBTBuilder(Material.STAINED_CLAY)
					.setDurability((short)14).setName("&4Deze game is bezig")
					.setLore("", "&7Je kunt nu niets veranderen.").build();
		}

		ClickableItem kbCI = ClickableItem.of(kbStick, event ->
		{
			if (game.getGameState() == GameState.INGAME) {
				player.sendMessage("§cDe game is al §4bezig §cje kunt dit niet veranderen op dit moment.");
				return;
			}

			game.setKnockbackStick(!game.isKnockbackStick());
			this.init(player, inventoryContents);
		}),
		mpCI = ClickableItem.of(maxPlayers, event ->
		{
			if (game.getGameState() == GameState.INGAME) {
				player.sendMessage("§cDe game is al §4bezig §cje kunt dit niet veranderen op dit moment.");
				return;
			}

			ClickType type = event.getClick();
			if (type == ClickType.LEFT) {
				if (game.getMaxPlayers() == 50) {
					player.sendMessage("§cJe kan de maximale aantal spelers niet hoger dan §450 §czetten.");
					return;
				}

				game.setMaxPlayers(game.getMaxPlayers() + 1);
				game.getScoreboardManager().broadcastUpdate(4, game.getScoreboardManager().f("&fSpelers: &d" + game.getPlayerList().size() + "/" + game.getMaxPlayers()));
				this.init(player, inventoryContents);
			}
			if (type == ClickType.RIGHT) {
				if (game.getMaxPlayers() == 2) {
					player.sendMessage("§cJe kan de maximale aantal spelers niet lager dan §42 §czetten.");
					return;
				}

				game.setMaxPlayers(game.getMaxPlayers() - 1);
				game.getScoreboardManager().broadcastUpdate(4, game.getScoreboardManager().f("&fSpelers: &d" + game.getPlayerList().size() + "/" + game.getMaxPlayers()));
				this.init(player, inventoryContents);
			}
		}),
		stopCI = ClickableItem.of(stopServer, event ->
		{
			player.closeInventory();
			player.sendMessage("§cServer wordt afgesloten...");
			BukkitRunnable runnable = new BukkitRunnable() {
				@Override
				public void run() {
					Bukkit.shutdown();
				}
			};
			runnable.runTaskLater(Sumo.getSumo(), 20L * 3L);
		}),
		statusCI = ClickableItem.of(status, event ->
		{
			if (game.getGameState() == GameState.DEVMODE) {
				player.closeInventory();
				Sumo.getSumo().getConfig().set("developer.dev-mode", false);
				Sumo.getSumo().saveConfig();
				Bukkit.shutdown();
			} else if (game.getGameState() == GameState.WAITING) {
				player.closeInventory();
				game.setGameState(GameState.STARTING);
				game.broadcastMessage("De game is gestart door de Event Host &d" + player.getName() + "&f.");
				game.getScoreboardManager().broadcastUpdate(6, "&f" + game.getGameState().getLabel());
				game.startProcedure();
			}
		});

		inventoryContents.set(SlotPos.of(0, 0), kbCI);
		inventoryContents.set(SlotPos.of(0, 1), mpCI);
		inventoryContents.set(SlotPos.of(0, 2), stopCI);
		inventoryContents.set(SlotPos.of(0, 3), statusCI);
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}
