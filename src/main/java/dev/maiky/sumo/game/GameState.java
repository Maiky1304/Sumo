package dev.maiky.sumo.game;

import lombok.Getter;

/**
 * Door: Maiky
 * Info: Sumo - 11 Apr 2021
 * Package: dev.maiky.sumo.game
 */

public enum GameState {

	DEVMODE("Developer Mode"),
	WAITING("Wachten op spelers.."),
	STARTING("Starten..."),
	INGAME("In-Game"),
	RESTARTING("Restarten...");

	@Getter
	private String label;
	GameState(String label) {
		this.label = label;
	}

}
