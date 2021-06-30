package dev.maiky.sumo.util;

import dev.maiky.sumo.Sumo;

import java.security.SecureRandom;

/**
 * Door: Maiky
 * Info: Sumo - 11 Apr 2021
 * Package: dev.maiky.sumo.util
 */

public class Generator {

	public static String generateGameId() {
		char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', '1', '2', '3', '5', '6','0'};
		String pattern = Sumo.getSumo().getConfig().getString("developer.id-pattern");

		SecureRandom random = new SecureRandom();
		StringBuilder builder = new StringBuilder();
		for (char c : pattern.toCharArray()){
			if (c == '#') {
				builder.append(chars[random.nextInt(chars.length)]);
			} else {
				builder.append(c);
			}
		}

		return builder.toString();
	}

}
