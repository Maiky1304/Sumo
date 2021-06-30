package dev.maiky.sumo;

import dev.maiky.sumo.commands.SumoCommand;
import dev.maiky.sumo.commands.Top3Command;
import dev.maiky.sumo.commands.lib.ICommand;
import dev.maiky.sumo.commands.lib.ICommandRegistry;
import dev.maiky.sumo.game.Game;
import dev.maiky.sumo.listener.*;
import dev.maiky.sumo.util.ConfigUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Sumo extends JavaPlugin {

	@Getter
	private static Sumo sumo;

	@Getter @Setter
	private static Game game;

	@Getter @Setter
	private static ConfigUtil locations;

	@Override
	public void onEnable() {
		sumo = this;

		// Configuration
		getConfig().options().copyDefaults(true);
		saveConfig();

		setLocations(new ConfigUtil("locations.yml"));
		getLocations().loadConfig();

		// Initialize Game
		Bukkit.getLogger().info("----------------------------------------------------");
		Bukkit.getLogger().info("\033[1;36mLoading " + getDescription().getName() + " v" + getDescription().getVersion() + " by \033[1;31m" + getDescription().getAuthors().get(0));
		Bukkit.getLogger().info(" ");
		game = new Game();
		Bukkit.getLogger().info(" ");
		Bukkit.getLogger().info("----------------------------------------------------");

		// Listeners
		Listener[] listeners = new Listener[]{new LoginListener(),new JoinListener(),new QuitListener(),
		new PreventMinecraftListener(), new MoveListener(), new PingListener(), new DamageListener(),
		new ChatListener(), new BlockBreakListener(), new InventoryClickListener(), new DropItemListener(),
		new InteractionListener(), new ChangeHeldItemListener(), new BlockPlaceListener(), new CPSListener()};
		for (Listener listener : listeners) Bukkit.getPluginManager().registerEvents(listener, this);

		// Commands
		ICommand[] commands = new ICommand[]{new SumoCommand(),new Top3Command()};
		for (ICommand command : commands) ICommandRegistry.register(command);

		// BungeeCord
		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "playerbalancer:main");
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}
}
