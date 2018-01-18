package com.therandomlabs.randompatches;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class RPConfig {
	private static Configuration config;

	public static long readTimeout;
	public static long loginTimeout;
	public static boolean patchForgeDefaultTimeouts;

	public static void reloadConfig() {
		if(config == null) {
			final Path path = Paths.get("config", RandomPatches.MODID + ".cfg");
			config = new Configuration(path.toFile());
		}

		readTimeout = getLong("readTimeout", "server", 60, 1, Integer.MAX_VALUE,
				"The read timeout.");
		loginTimeout = getLong("loginTimeout", "server", 300, 1, Integer.MAX_VALUE,
				"The login timeout.");
		patchForgeDefaultTimeouts = getBoolean("patchForgeDefaultTimeouts", "server", false,
				"Whether to patch the default Forge timeouts rather than forcibly changing " +
				"their values. Set this to true if you want to be able to use -Dfml.readTimeout " +
				"and -Dfml.loginTimeout in the JVM arguments.");

		config.save();
	}

	static void init() {
		if(config == null) {
			reloadConfig();
		}
	}

	private static long getLong(String name, String category, int defaultValue, int minValue,
			int maxValue, String comment) {
		final Property prop = config.get(category, name, defaultValue);
		prop.setMinValue(minValue);
		prop.setMaxValue(maxValue);
		prop.setComment(comment + " [default: " + defaultValue + ", range: " + minValue + "-" +
				maxValue + "]");
		prop.setRequiresMcRestart(true);
		//TODO config GUI
		//prop.setLanguageKey("rpconfig.config." + name);
		return prop.getLong(defaultValue);
	}

	private static boolean getBoolean(String name, String category, boolean defaultValue,
			String comment) {
		final Property prop = config.get(category, name, defaultValue);
		prop.setComment(comment + " [default: " + defaultValue + "]");
		prop.setRequiresMcRestart(true);
		//TODO config GUI
		//prop.setLanguageKey("rpconfig.config." + name);
		return prop.getBoolean(defaultValue);
	}
}
