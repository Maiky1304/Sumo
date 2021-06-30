package dev.maiky.sumo.commands.lib;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Door: Maiky
 * Info: Sumo - 11 Apr 2021
 * Package: dev.maiky.sumo.commands.lib
 */

public abstract class ICommand {

	@Getter
	private final String command;

	public ICommand(String command) {
		this.command = command;
	}

	public abstract boolean onCommand(Player p, String[] args);
	public abstract List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args);

}
