package com.therandomlabs.randompatches;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class RPConfig {
	private static Configuration config;

	public static int readTimeout;
	//So I don't have to do the conversions in bytecode
	public static long readTimeoutMillis;
	public static String readTimeoutString;

	public static int loginTimeout;
	public static String loginTimeoutString;

	public static boolean patchForgeDefaultTimeouts;

	public static boolean forceTitleScreenOnDisconnect;

	public static boolean rpreload;
	public static boolean rpreloadclient;

	public static void reload() {
		if(config == null) {
			final Path path = Paths.get("config", RandomPatches.MODID + ".cfg");
			config = new Configuration(path.toFile());
		} else {
			config.load();
		}

		readTimeout = getInt("readTimeout", "timeouts", 80, 1, Integer.MAX_VALUE,
				"The read timeout.");
		readTimeoutMillis = readTimeout * 1000L;
		readTimeoutString = Integer.toString(readTimeout);

		loginTimeout = getInt("loginTimeout", "timeouts", 900, 1, Integer.MAX_VALUE,
				"The login timeout.");
		loginTimeoutString = Integer.toString(loginTimeout);

		patchForgeDefaultTimeouts = getBoolean("patchForgeDefaults", "timeouts", false,
				"Whether to patch the default Forge timeouts rather than forcibly changing " +
				"their values. Set this to true if you want to be able to use -Dfml.readTimeout " +
				"and -Dfml.loginTimeout in the JVM arguments.");

		forceTitleScreenOnDisconnect = getBoolean("forceTitleScreenOnDisconnect", "misc", false,
				"Forces Minecraft to show the title screen on disconnect, rather than the " +
				"Multiplayer or Realms menu.");

		rpreload = getBoolean("rpreload", "commands", true, "Enables the /rpreload command, " +
				"which reloads the configuration. This command is server-sided. This " +
				"configuration option only takes effect after a world restart.");
		rpreloadclient = getBoolean("rpreloadclient", "commands", true, "Enables the " +
				"/rpreloadclient command, which reloads the configuration. This command is " +
				"client-sided. This configuration option only takes effect after a Minecraft " +
				"restart.");

		config.save();
	}

	private static int getInt(String name, String category, int defaultValue, int minValue,
			int maxValue, String comment) {
		final Property prop = config.get(category, name, defaultValue);
		prop.setMinValue(minValue);
		prop.setMaxValue(maxValue);
		prop.setComment(comment + " [default: " + defaultValue + ", range: " + minValue + "-" +
				maxValue + "]");
		prop.setRequiresMcRestart(true);
		return prop.getInt(defaultValue);
	}

	private static boolean getBoolean(String name, String category, boolean defaultValue,
			String comment) {
		final Property prop = config.get(category, name, defaultValue);
		prop.setComment(comment + " [default: " + defaultValue + "]");
		prop.setRequiresMcRestart(true);
		return prop.getBoolean(defaultValue);
	}
}
