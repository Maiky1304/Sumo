package dev.maiky.sumo.commands;

import dev.maiky.sumo.Sumo;
import dev.maiky.sumo.commands.lib.ICommand;
import dev.maiky.sumo.game.Game;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Door: Maiky
 * Info: Sumo - 11 Apr 2021
 * Package: dev.maiky.sumo.commands
 */

public class SumoCommand extends ICommand {

	public SumoCommand(){
		super("sumo");
	}

	@Override
	public boolean onCommand(Player p, String[] args) {
		Location location = p.getLocation();

		if (args.length == 0) {
			p.sendMessage("§cJe hebt geen sub-commando opgegeven.");
			return true;
		}

		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("addpoint1")) {
				Sumo.getLocations().getConfig().set("point1", location);
				Sumo.getLocations().saveConfig();

				p.sendMessage("§aPunt #1 opgeslagen!");
				return true;
			} else if (args[0].equalsIgnoreCase("addpoint2")) {
				Sumo.getLocations().getConfig().set("point2", location);
				Sumo.getLocations().saveConfig();

				p.sendMessage("§aPunt #2 opgeslagen!");
				return true;
			} else if (args[0].equalsIgnoreCase("setspawn")) {
				Sumo.getLocations().getConfig().set("spawn", location);
				Sumo.getLocations().saveConfig();

				p.sendMessage("§aSpawn opgeslagen!");
				return true;
			} else if (args[0].equalsIgnoreCase("tppoint1")) {
				Location tpTo = (Location) Sumo.getLocations().getConfig().get("point1");
				p.teleport(tpTo);
				return true;
			} else if (args[0].equalsIgnoreCase("tppoint2")) {
				Location tpTo = (Location) Sumo.getLocations().getConfig().get("point2");
				p.teleport(tpTo);
				return true;
			} else if (args[0].equalsIgnoreCase("tpspawn")) {
				Location tpTo = (Location) Sumo.getLocations().getConfig().get("spawn");
				p.teleport(tpTo);
				return true;
			} else if (args[0].equalsIgnoreCase("eventhost")) {
				Game game = Sumo.getGame();

				if (game.getHosts().contains(p.getUniqueId())) {
					p.sendMessage("§cJe bent al host van deze game, je kunt niet meer het publiek joinen dit kan pas na restart van de gameserver.");
					return true;
				}

				game.getScoreboardManager().broadcastUpdate(8, "§1");
				game.getHosts().add(p.getUniqueId());
				p.sendMessage("§aJe bent nu als event host ingesteld.");
				game.giveHostItems(p);
				p.sendMessage("§aEr zijn event host items aan je inventory toegevoegd.");
				return true;
			} else if (args[0].equalsIgnoreCase("start")) {
				Sumo.getGame().startProcedure();
				p.sendMessage("§aDe game zal nu starten...");
				return true;
			} else {
				p.sendMessage("§cOnbekend sub-commando.");
				p.performCommand("sumo");
				return true;
			}
		} else {
			p.sendMessage("§cOnbekend sub-commando.");
			p.performCommand("sumo");
			return true;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return Arrays.asList("addpoint1", "addpoint2", "setspawn", "tppoint1", "tppoint2", "tpspawn", "eventhost");
	}
}
