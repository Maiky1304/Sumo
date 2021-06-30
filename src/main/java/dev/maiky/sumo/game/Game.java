package dev.maiky.sumo.game;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.maiky.blackmtcore.classes.nbtbuilder.NBTBuilder;
import dev.maiky.blackmtcore.tokens.manager.TokenManager;
import dev.maiky.sumo.Sumo;
import dev.maiky.sumo.scoreboard.ScoreboardManager;
import dev.maiky.sumo.util.Generator;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Door: Maiky
 * Info: Sumo - 11 Apr 2021
 * Package: dev.maiky.sumo.game
 */

public class Game {

	@Getter
	private final List<Player> playerList;
	@Getter
	private final List<Player> deathPlayers = new ArrayList<>();
	@Getter
	private final List<Player> inDuel = new ArrayList<>();
	@Getter
	private final List<Player> noMove = new ArrayList<>();

	@Getter
	private final String gameId;

	@Getter @Setter
	private GameState gameState;

	@Getter @Setter
	private int maxPlayers;

	@Getter @Setter
	private int minPlayers;

	@Getter @Setter
	private int preGameTimer = 30;

	@Getter @Setter
	private boolean knockbackStick;

	@Getter
	private final int duelEndY;

	@Getter
	private final ItemStack knockbackStickItem, settingsItem;

	private final Sound TICK_SOUND = Sound.NOTE_STICKS;
	private final Sound START_SOUND = Sound.NOTE_PLING;
	private final float TICK_SOUND_PITCH = 1f;
	private final float START_SOUND_PITCH = 0.8f;

	@Getter
	private final ScoreboardManager scoreboardManager;

	@Getter
	private final StatsManager statsManager;

	private final List<Integer> notifyAt;

	@Getter @Setter
	private List<UUID> hosts;

	@Getter @Setter
	private HashMap<Player, Integer> cps = new HashMap<>();

	public Game() {
		Bukkit.getLogger().info(" > \u001B[32mInitializing Game...");

		this.playerList = new ArrayList<>();
		this.gameId = Generator.generateGameId();
		this.scoreboardManager = new ScoreboardManager();
		this.statsManager = new StatsManager();
		this.hosts = new ArrayList<>();

		this.knockbackStick = Sumo.getSumo().getConfig().getBoolean("game.knockbackStick");
		this.maxPlayers = Sumo.getSumo().getConfig().getInt("game.max");
		this.minPlayers = Sumo.getSumo().getConfig().getInt("game.min");
		this.preGameTimer = Sumo.getSumo().getConfig().getInt("game.defaultTimer");
		this.duelEndY = Sumo.getSumo().getConfig().getInt("game.duelEndY");
		this.notifyAt = (List<Integer>) Sumo.getSumo().getConfig().getList("game.cooldown-notify");

		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				playerList.forEach(user -> cps.put(user, 1));
			}
		};
		runnable.runTaskTimer(Sumo.getSumo(), 0, 20);

		this.knockbackStickItem = new NBTBuilder(Material.FERMENTED_SPIDER_EYE)
				.setAmount(1)
				.setName("Knockback Stok")
				.setLore("","Event Item")
				.build();
		this.settingsItem = new NBTBuilder(Material.CHEST)
				.setName("&3Instellingen").build();
		this.knockbackStickItem.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

		if (!Sumo.getSumo().getConfig().getBoolean("developer.dev-mode")) {
			this.launch();
		} else {
			this.setGameState(GameState.DEVMODE);
		}
	}

	public void giveHostItems(Player player) {
		player.getInventory().clear();
		player.getInventory().addItem(this.settingsItem);
	}

	private void launch() {
		this.setGameState(GameState.WAITING);

		Bukkit.getLogger().info(" > \u001B[32mSuccesfully launched game instance \033[1;37m" + this.getGameId() + "\033[0m");
		Bukkit.getLogger().info(" > \u001B[32mNow awaiting players...");
	}
	
	public void broadcastMessage(String message) {
		this.playerList.forEach(user -> user.sendMessage(ChatColor.translateAlternateColorCodes('&', "&d&lSumo &7┃ &f" + message)));
	}

	public void handleMessage(Player user, String message) {
		user.sendMessage(ChatColor.translateAlternateColorCodes('&', "&d&lSumo &7┃ &f" + message));
	}

	public void broadcastSound(Sound sound, float pitch) {
		this.playerList.forEach(user -> {
			user.playSound(user.getLocation(), sound, .5f, pitch);
		});
	}

	public void handoutTokens(int amount, String message) {
		this.playerList.forEach(user -> giveTokens(user, amount));
		broadcastMessage(message);
	}

	public void giveTokens(Player player, int tokens) {
		TokenManager tokenManager = new TokenManager();
		tokenManager.setUuid(player.getUniqueId());
		tokenManager.fetch();
		try {
			tokenManager.add(tokens);
		} catch (IllegalAccessException exception) {
			Bukkit.getLogger().warning("Er is een fout opgetreden bij het proberen te geven van tokens aan " + player.getName());
		}
	}

	public void broadcastLevel(int level) {
		this.playerList.forEach(user -> user.setLevel(level));
	}

	public boolean isAlive(Player p) {
		return this.getPlayerList().contains(p) && !this.getDeathPlayers().contains(p);
	}

	public void startProcedure() {
		BukkitRunnable timerNotifier = new BukkitRunnable() {

			@Override
			public void run() {

				if ((getPlayerList().size() - getHosts().size()) < minPlayers) {
					this.cancel();
					setGameState(GameState.WAITING);
					setPreGameTimer(30);
					broadcastLevel(0);
					getScoreboardManager().restoreDefault();
					broadcastMessage("Het starten van de game is geannuleerd omdat er &dgeen &fgenoeg spelers meer in de game aanwezig zijn.");
					return;
				}

				setPreGameTimer(getPreGameTimer() - 1);
				broadcastLevel(getPreGameTimer());
				getScoreboardManager().broadcastUpdate(6,
						getScoreboardManager().f("&fStart over " + getPreGameTimer() + "s"));

				if (notifyAt.contains(getPreGameTimer())) {
					broadcastMessage("De game zal starten over &a&l" + getPreGameTimer() + " " + (getPreGameTimer()
					== 1 ? "seconde" : "seconden") + "&f.");
				}

				if (getPreGameTimer() == 0) {
					broadcastLevel(0);
					setGameState(GameState.INGAME);
					broadcastMessage("De game is gestart!");
					startGame();
					cancel();
				}
			}
		};
		timerNotifier.runTaskTimerAsynchronously(Sumo.getSumo(), 0, 20);
	}

	private void startGame() {
		BukkitRunnable checkEnd = new BukkitRunnable() {
			@Override
			public void run() {
				if (getAlivePlayers().size() <= 1) {
					this.cancel();

					setGameState(GameState.RESTARTING);
					getScoreboardManager().restoreDefault();

					getStatsManager().updateStatistic(getAlivePlayers().get(0).getUniqueId(),
							StatsManager.Statistic.EVENT_WIN,
							getStatsManager().getStatistic(getAlivePlayers().get(0).getUniqueId(),
									StatsManager.Statistic.EVENT_WIN) + 1);

					broadcastMessage("De winnaar van dit &a&lSumo Event &fis &d" + getAlivePlayers().get(0).getName() + "&f!");
					int waitRestart = 10;
					broadcastMessage("Iedereen wordt naar de &alobby &fgestuurd over &d&l" + waitRestart + " &fseconden.");

					if (Sumo.getSumo().getConfig().getBoolean("developer.rewards-enabled")) {
						handoutTokens(5, "Bedankt voor het meedoen aan dit &d&lEvent &fvoor je participatie heb je &d&l5 &f&ltokens ontvangen.");

						giveTokens(getAlivePlayers().get(0), 50);
						handleMessage(getAlivePlayers().get(0), "Gefeliciteerd met het winnen van dit &a&lSumo Event &fje hebt als reward &d&l10&f tokens ontvangen.");
					}

					BukkitRunnable sendLobby = new BukkitRunnable() {
						@Override
						public void run() {
							sendToLobby();
						}
					};
					sendLobby.runTaskLater(Sumo.getSumo(), 20 * waitRestart);
				}
			}
		};
		checkEnd.runTaskTimerAsynchronously(Sumo.getSumo(), 0L, 1L);

		List<Player> randomPlayers = chooseRandomPlayers();
		handleMatch(randomPlayers);
	}

	public void sendAB(String message) {
		this.getHosts().forEach(user -> {
			if (!Bukkit.getOfflinePlayer(user).isOnline()) return;

			PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(getScoreboardManager().f(message)), (byte)2);
			((CraftPlayer) Bukkit.getPlayer(user)).getHandle().playerConnection.sendPacket(packet);
		});
	}

	public void sendToLobby() {
		setGameState(GameState.RESTARTING);

		Bukkit.getOnlinePlayers().forEach(user -> {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF("general-lobbies");

			user.sendPluginMessage(Sumo.getSumo(), "playerbalancer:main", out.toByteArray());
		});

		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
	}

	public void handleMatchEnd(Player loser) {
		List<Player> copyOfInDuel = new ArrayList<>(inDuel);
		copyOfInDuel.remove(loser);
		Player winner = copyOfInDuel.get(0);

		getInDuel().clear();

		this.getDeathPlayers().add(loser);
		loser.setAllowFlight(true);

		Location location = (Location) Sumo.getLocations().getConfig().get("spawn");
		loser.getInventory().remove(knockbackStickItem);
		winner.getInventory().remove(knockbackStickItem);
		loser.teleport(location);
		winner.teleport(location);

		getStatsManager().updateStatistic(winner.getUniqueId(),
				StatsManager.Statistic.WIN,
				getStatsManager().getStatistic(winner.getUniqueId(),
						StatsManager.Statistic.WIN) + 1);
		getStatsManager().updateStatistic(loser.getUniqueId(),
				StatsManager.Statistic.LOSE,
				getStatsManager().getStatistic(loser.getUniqueId(),
						StatsManager.Statistic.LOSE) + 1);

		broadcastMessage("Winnaar: &a" + winner.getName() + "   " + "&fVerliezer: &c" + loser.getName());

		if (getGameState() == GameState.RESTARTING || getAlivePlayers().size() <= 1) return;

		broadcastMessage("Er wordt een nieuw duel gestart over &a&l10 seconden&f.");

		BukkitRunnable chooseNewPlayers = new BukkitRunnable() {
			@Override
			public void run() {
				List<Player> randomPlayers = chooseRandomPlayers();
				handleMatch(randomPlayers);
			}
		};
		chooseNewPlayers.runTaskLater(Sumo.getSumo(), 20L * 10L);
	}

	private List<Player> getAlivePlayers() {
		List<Player> alivePlayers = new ArrayList<>();

		for (Player player : this.getPlayerList()) {
			if (isAlive(player) && !getHosts().contains(player.getUniqueId()))
				alivePlayers.add(player);
		}

		return alivePlayers;
	}

	private void handleMatch(List<Player> players) {
		getInDuel().clear();
		getInDuel().addAll(players);

		broadcastMessage(String.format("Sumo match wordt gestart: &a%s &fvs &a%s &f",
				players.get(0).getName(), players.get(1).getName()));

		getScoreboardManager().broadcastUpdate(8, "&d" + players.get(0).getName()
				+ " &f(" + ((CraftPlayer)players.get(0)).getHandle().ping + " ms)");
		getScoreboardManager().broadcastUpdate(7, "&fvs.");
		getScoreboardManager().broadcastUpdate(6, "&d" + players.get(1).getName()
				+ " &f(" + ((CraftPlayer)players.get(1)).getHandle().ping + " ms)");

		BukkitRunnable scoreboard = new BukkitRunnable() {
			@Override
			public void run() {
				if (!getInDuel().containsAll(players)) {
					this.cancel();
					getScoreboardManager().restoreDefault();
					return;
				}

				sendAB("&d" + players.get(0).getName() + " &7(&5" + (getCps().get(players.get(0))-1) + " cps&7)   "
				+ "&d" + players.get(1).getName() + " &7(&5" + (getCps().get(players.get(1))-1) + " cps&7)");

				getScoreboardManager().broadcastUpdate(8, "&d" + players.get(0).getName()
						+ " &f(" + ((CraftPlayer)players.get(0)).getHandle().ping + " ms)");
				getScoreboardManager().broadcastUpdate(6, "&d" + players.get(1).getName()
						+ " &f(" + ((CraftPlayer)players.get(1)).getHandle().ping + " ms)");
			}
		};
		scoreboard.runTaskTimerAsynchronously(Sumo.getSumo(), 0, 20);

		Location[] points = new Location[]{(Location) Sumo.getLocations().getConfig().get("point1"),
				(Location) Sumo.getLocations().getConfig().get("point2")};

		BukkitRunnable noJump = new BukkitRunnable() {
			@Override
			public void run() {
				final int[] k = {0};
				players.forEach(p -> {
					if (knockbackStick) {
						p.getInventory().addItem(knockbackStickItem);
						p.getInventory().setHeldItemSlot(0);
					}
					p.teleport(points[k[0]]);
					k[0]++;
				});

				getNoMove().addAll(players);

				BukkitRunnable timer = new BukkitRunnable() {
					private int countdown = 7;

					@Override
					public void run() {
						broadcastSound(TICK_SOUND, TICK_SOUND_PITCH);
						broadcastLevel(countdown - 1);
						broadcastMessage("De game start in &d" + countdown + " &f" + (countdown == 1 ? "seconde..." : "seconden..."));

						if (countdown > 5) {
							countdown--;
							return;
						}

						countdown--;

						if (countdown == 0) {
							this.cancel();
							broadcastSound(START_SOUND, START_SOUND_PITCH);
							broadcastMessage("&aDe game is gestart, veel succes!");
							broadcastLevel(0);
							getNoMove().clear();
						}
					}
				};
				timer.runTaskTimer(Sumo.getSumo(), 0, 20);
			}
		};
		noJump.runTask(Sumo.getSumo());
	}

	private List<Player> chooseRandomPlayers() {
		List<Player> chosen = new ArrayList<>();

		SecureRandom random = new SecureRandom();

		List<Player> alive = new ArrayList<>(getPlayerList());
		alive.removeAll(getDeathPlayers());
		getHosts().forEach(uuid -> alive.remove(Bukkit.getPlayer(uuid)));

		for (int i = 0; i < 2; i++) {
			Player chosenPlayer = alive.get(random.nextInt(alive.size()));
			while(chosen.contains(chosenPlayer)) {
				chosenPlayer = alive.get(random.nextInt(alive.size()));
			}
			chosen.add(chosenPlayer);
		}

		return chosen;
	}

}
