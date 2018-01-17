package com.therandomlabs.randompatches;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class RPConfig {
	private static Configuration config;

	public static long readTimeout;
	public static long loginTimeout;

	public static void reloadConfig() {
		if(config == null) {
			final Path path = Paths.get("config", RandomPatches.MODID + ".cfg");
			config = new Configuration(path.toFile());
		}

		readTimeout = getLong("readTimeout", "server", 600, 1, Integer.MAX_VALUE,
				"The read timeout.", true);
		loginTimeout = getLong("loginTimeout", "server", 600, 1, Integer.MAX_VALUE,
				"The login timeout.", true);

		config.save();
	}

	static void init() {
		if(config == null) {
			reloadConfig();
		}
	}

	private static long getLong(String name, String category, int defaultValue, int minValue,
			int maxValue, String comment, boolean requiresRestart) {
		final Property prop = config.get(category, name, defaultValue);
		prop.setMinValue(minValue);
		prop.setMaxValue(maxValue);
		prop.setComment(comment + " [default: " + defaultValue + ", range: " + minValue + "-" +
				maxValue + "]");
		prop.setRequiresMcRestart(requiresRestart);
		//TODO config GUI
		//prop.setLanguageKey("rpconfig.config." + name);
		return prop.getLong(defaultValue);
	}
}
