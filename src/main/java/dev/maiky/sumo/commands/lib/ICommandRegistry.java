package dev.maiky.sumo.commands.lib;

import dev.maiky.sumo.Sumo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * Door: Maiky
 * Info: Sumo - 11 Apr 2021
 * Package: dev.maiky.sumo.commands.lib
 */

public class ICommandRegistry implements CommandExecutor, TabCompleter {

	private static HashMap<String, ICommand> commandHashMap = new HashMap<>();

	public static void register(ICommand iCommand) {
		commandHashMap.put(iCommand.getCommand(), iCommand);

		Sumo.getSumo().getCommand(iCommand.getCommand()).setExecutor(new ICommandRegistry());
		Sumo.getSumo().getCommand(iCommand.getCommand()).setTabCompleter(new ICommandRegistry());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return true;
		ICommand iCommand = commandHashMap.get(command.getName());
		return iCommand.onCommand((Player)sender, args);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player)) return null;
		ICommand iCommand = commandHashMap.get(command.getName());
		return iCommand.onTabComplete(sender, command, alias, args);
	}
}
