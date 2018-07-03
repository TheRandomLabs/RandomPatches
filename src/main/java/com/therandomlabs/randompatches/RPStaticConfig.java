package com.therandomlabs.randompatches;

import java.nio.file.Paths;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class RPStaticConfig {
	private static Configuration config;

	public static class Comments {
		public static final String FORCE_TITLE_SCREEN_ON_DISCONNECT = "Forces Minecraft to show " +
				"the title screen after disconnecting rather than the Multiplayer or Realms menu.";
		public static final String RPRELOAD = "Enables the /rpreload command. " +
				"This only takes effect after a world restart.";

		public static final String KEEP_ALIVE_PACKET_INTERVAL =
				"The interval at which the server sends the KeepAlive packet.";
		public static final String LOGIN_TIMEOUT = "The login timeout.";
		public static final String READ_TIMEOUT = "The read timeout. This is the time it takes " +
				"for a player to be disconnected after not responding to a KeepAlive packet. " +
				"This value is automatically rounded up to a product of keepAlivePacketInterval.";
	}

	public static class Defaults {
		public static final boolean FORCE_TITLE_SCREEN_ON_DISCONNECT = false;
		public static final boolean RPRELOAD = true;

		public static final int KEEP_ALIVE_PACKET_INTERVAL = 15;
		public static final int LOGIN_TIMEOUT = 900;
		public static final int READ_TIMEOUT = 90;
	}

	public static final String MISC_COMMENT = "Options that don't fit into any other categories.";
	public static final String TIMEOUTS_COMMENT = "Options related to disconnect timeouts.";

	//Commands

	public static boolean rpreload;
	public static boolean rpreloadclient;

	//Misc

	public static boolean forceTitleScreenOnDisconnect;

	//Timeouts

	public static int keepAlivePacketInterval;
	public static long keepAlivePacketIntervalMillis;

	public static int loginTimeout;

	public static int readTimeout;
	public static long readTimeoutMillis;

	public static void reload() {
		if(config == null) {
			config = new Configuration(Paths.get("config", RandomPatches.MODID + ".cfg").toFile());
		} else {
			config.load();
		}

		config.addCustomCategoryComment("misc", MISC_COMMENT);

		forceTitleScreenOnDisconnect = getBoolean("forceTitleScreenOnDisconnect", "misc",
				Defaults.FORCE_TITLE_SCREEN_ON_DISCONNECT,
				Comments.FORCE_TITLE_SCREEN_ON_DISCONNECT, false, false);
		rpreload = getBoolean("rpreload", "misc", Defaults.RPRELOAD, Comments.RPRELOAD, true,
				false);

		config.addCustomCategoryComment("timeouts", TIMEOUTS_COMMENT);

		keepAlivePacketInterval = getInt("keepAlivePacketInterval", "timeouts",
				Defaults.KEEP_ALIVE_PACKET_INTERVAL, 1, Integer.MAX_VALUE,
				Comments.KEEP_ALIVE_PACKET_INTERVAL);
		loginTimeout = getInt("loginTimeout", "timeouts", Defaults.LOGIN_TIMEOUT, 1,
				Integer.MAX_VALUE, Comments.LOGIN_TIMEOUT);
		readTimeout = getInt("readTimeout", "timeouts", Defaults.READ_TIMEOUT, 1, Integer.MAX_VALUE,
				Comments.READ_TIMEOUT);

		removeOldProperties();
		onReload();
		config.save();
	}

	public static void onReload() {
		if(readTimeout < keepAlivePacketInterval) {
			readTimeout = keepAlivePacketInterval * 2;
		} else if(readTimeout % keepAlivePacketInterval != 0) {
			readTimeout = readTimeout / keepAlivePacketInterval + 1;
		}

		keepAlivePacketIntervalMillis = keepAlivePacketInterval * 1000L;
		readTimeoutMillis = readTimeout * 1000L;

		System.setProperty("fml.readTimeout", Integer.toString(RPStaticConfig.readTimeout));
		System.setProperty("fml.loginTimeout", Integer.toString(RPStaticConfig.loginTimeout));
	}

	private static int getInt(String name, String category, int defaultValue, int minValue,
			int maxValue, String comment) {
		final Property prop = config.get(category, name, defaultValue);
		prop.setMinValue(minValue);
		prop.setMaxValue(maxValue);
		prop.setComment(comment + "\nMin: " + minValue + "\nMax: " + maxValue + "\nDefault: " +
				defaultValue);
		return prop.getInt(defaultValue);
	}

	private static boolean getBoolean(String name, String category, boolean defaultValue,
			String comment, boolean requiresWorldRestart, boolean requiresMcRestart) {
		final Property prop = config.get(category, name, defaultValue);
		prop.setComment(comment + "\nDefault: " + defaultValue);

		if(requiresMcRestart) {
			prop.setRequiresMcRestart(true);
		} else if(requiresWorldRestart) {
			prop.setRequiresWorldRestart(true);
		}

		return prop.getBoolean(defaultValue);
	}

	private static void removeOldProperties() {
		for(String name : config.getCategoryNames()) {
			final ConfigCategory category = config.getCategory(name);

			category.getValues().forEach((key, property) -> {
				final String comment = property.getComment();

				if(comment == null || comment.isEmpty()) {
					category.remove(key);
				}
			});

			if(category.getValues().isEmpty() || category.getComment() == null) {
				config.removeCategory(category);
			}
		}
	}
}
