package dev.maiky.sumo.commands;

import dev.maiky.sumo.Sumo;
import dev.maiky.sumo.commands.lib.ICommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Door: Maiky
 * Info: Sumo - 12 Apr 2021
 * Package: dev.maiky.sumo.commands
 */

public class Top3Command extends ICommand {

	public Top3Command(){
		super("top");
	}

	@Override
	public boolean onCommand(Player p, String[] args) {
		HashMap<UUID, Integer> hashMap = Sumo.getGame().getStatsManager().getTop3();
		p.sendMessage("§7§m---------------------------------");
		int i = 1;
		for (UUID u : hashMap.keySet()) {
			p.sendMessage(" §d#" + i + " §8- §f" + Bukkit.getOfflinePlayer(u).getName() + "");
			i++;
		}
		p.sendMessage("§7§m---------------------------------");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return null;
	}
}
