package com.therandomlabs.randompatches.config;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.util.RPUtils;
import com.therandomlabs.randompatches.util.WindowIconHandler;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.lwjgl.opengl.Display;

public class RPStaticConfig {
	public static class Comments {
		public static final String PATCH_ENTITYBOAT =
				"Whether to patch EntityBoat.\nThis only works on 1.9 and above.";
		public static final String PREVENT_UNDERWATER_BOAT_PASSENGER_EJECTION = "Prevents " +
				"underwater boat passengers from being ejected after 60 ticks (3 seconds).";
		public static final String UNDERWATER_BOAT_BUOYANCY = "The underwater boat buoyancy.\n" +
				"The vanilla default is -0.0007.";

		public static final String FAST_LANGUAGE_SWITCH = "Speeds up language switching.";
		public static final String FORCE_TITLE_SCREEN_ON_DISCONNECT = "Forces Minecraft to show " +
				"the title screen after disconnecting rather than the Multiplayer or Realms menu.";
		public static final String NARRATOR_KEYBIND = "Whether to add the Toggle Narrator keybind" +
				"to the controls.\nThis only works on 1.12 as the narrator does not exist in " +
				"previous versions.";
		public static final String PATCH_MINECRAFT_CLASS = "Set this to false to disable the " +
				"Minecraft class patches (the Toggle Narrator keybind and custom window " +
				"title/icon).";
		public static final String PATCH_TITLE_SCREEN_ON_DISCONNECT = "Set this to false to " +
				"force disable the \"force title screen on disconnect\" apply.";
		public static final String REMOVE_POTION_GLINT =
				"Whether to remove the glowing effect from potions.";
		public static final String RPRELOADCLIENT = "Enables the /rpreloadclient command.";

		public static final String ICON_16 = "The path to the 16x16 Minecraft window " +
				"icon.\nLeave this and the 32x32 icon blank to use the default icon.";
		public static final String ICON_32 = "The path to the 32x32 Minecraft window " +
				"icon.\nLeave this and the 16x16 icon blank to use the default icon.";
		public static final String TITLE = "The Minecraft window title.";

		public static final String END_PORTAL_TWEAKS = "Fixes the End portal and End gateway " +
				"break particle textures and improves End portal rendering.\n" +
				"This only works on Minecraft 1.11 and above.";
		public static final String MC_2025_FIX = "Fixes MC-2025.\nThis only works on 1.10 and " +
				"above.\nMore information can be found here: " +
				"https://www.reddit.com/r/Mojira/comments/8pgd4q/final_and_proper_fix_to_" +
				"mc2025_simple_reliable/";
		public static final String MINECART_AI_FIX = "Fixes MC-64836, which causes non-player " +
				"entities to be allowed to control minecarts using their AI.";
		public static final String PATCH_NETHANDLERPLAYSERVER = "Set this to false to " +
				"disable the NetHandlerPlayServer patches (the speed limits and disconnect " +
				"timeouts).\nOn 1.8, 1.8.8 and 1.8.9, these patches are always disabled.";
		public static final String RECIPE_BOOK_NBT_FIX = "Fixes MC-129057, which prevents " +
				"ingredients with NBT data from being transferred to the crafting grid when a " +
				"recipe is clicked in the recipe book.";
		public static final String REPLACE_TELEPORTER = "Whether to allow other mods " +
				"(namely RandomPortals) to replace the default Teleporter on 1.12.";
		public static final String RPRELOAD = "Enables the /rpreload command.";
		public static final String SKULL_STACKING_FIX = "Fixes player skull stacking.";
		public static final String SKULL_STACKING_REQUIRES_SAME_TEXTURES = "Whether skull " +
				"stacking requires the same textures or just the same player profile.";

		public static final String MAX_PLAYER_SPEED =
				"The maximum player speed.\nThe vanilla default is 100.0.";
		public static final String MAX_PLAYER_ELYTRA_SPEED =
				"The maximum player elytra speed.\nThe vanilla default is 300.0.";
		public static final String MAX_PLAYER_VEHICLE_SPEED =
				"The maximum player vehicle speed.\nThe vanilla default is 100.0.";

		public static final String KEEP_ALIVE_PACKET_INTERVAL =
				"The interval at which the server sends the KeepAlive packet.";
		public static final String LOGIN_TIMEOUT = "The login timeout.";
		public static final String PATCH_LOGIN_TIMEOUT = "Whether to apply the login timeout.";
		public static final String READ_TIMEOUT = "The read timeout.\nThis is the time it takes " +
				"for a player to be disconnected after not responding to a KeepAlive packet.\n" +
				"This value is automatically rounded up to a product of " +
				"keepAlivePacketInterval.\nThis only works on 1.12 and above.";
	}

	public static class Defaults {
		public static final boolean PATCH_ENTITYBOAT = true;
		public static final boolean PREVENT_UNDERWATER_BOAT_PASSENGER_EJECTION =
				RandomPatches.IS_DEOBFUSCATED;
		public static final double UNDERWATER_BOAT_BUOYANCY = 0.023;

		public static final boolean FAST_LANGUAGE_SWITCH = true;
		public static final boolean FORCE_TITLE_SCREEN_ON_DISCONNECT =
				RandomPatches.IS_DEOBFUSCATED;
		public static final boolean NARRATOR_KEYBIND = true;
		public static final boolean PATCH_MINECRAFT_CLASS = true;
		public static final boolean PATCH_TITLE_SCREEN_ON_DISCONNECT = true;
		public static final boolean REMOVE_POTION_GLINT = RandomPatches.IS_DEOBFUSCATED;
		public static final boolean RPRELOADCLIENT = true;

		public static final String ICON_16 = RandomPatches.IS_DEOBFUSCATED ?
				"../src/main/resources/assets/randompatches/logo.png" : "";
		public static final String ICON_32 = ICON_16;
		public static final String TITLE = RandomPatches.IS_DEOBFUSCATED ?
				RandomPatches.NAME : RandomPatches.DEFAULT_WINDOW_TITLE;

		public static final boolean END_PORTAL_TWEAKS = true;
		public static final boolean MC_2025_FIX = true;
		public static final boolean MINECART_AI_FIX = true;
		public static final boolean PATCH_NETHANDLERPLAYSERVER = true;
		public static final boolean RECIPE_BOOK_NBT_FIX = true;
		public static final boolean REPLACE_TELEPORTER = true;
		public static final boolean RPRELOAD = true;
		public static final boolean SKULL_STACKING_FIX = true;
		public static final boolean SKULL_STACKING_REQUIRES_SAME_TEXTURES = true;

		public static final float MAX_PLAYER_SPEED = 1000000.0F;
		public static final float MAX_PLAYER_ELYTRA_SPEED = 1000000.0F;
		public static final double MAX_PLAYER_VEHICLE_SPEED = 1000000.0;

		public static final int KEEP_ALIVE_PACKET_INTERVAL = 15;
		public static final int LOGIN_TIMEOUT = 900;
		public static final boolean PATCH_LOGIN_TIMEOUT = true;
		public static final int READ_TIMEOUT = 90;
	}

	public static final String BOATS_COMMENT = "Options related to boats.";
	public static final String CLIENT_COMMENT = "Options related to client-sided features.";
	public static final String MISC_COMMENT = "Options that don't fit into any other categories.";
	public static final String SPEED_LIMITS_COMMENT =
			"Options related to the movement speed limits.";
	public static final String TIMEOUTS_COMMENT = "Options related to the disconnect timeouts.";
	public static final String WINDOW_COMMENT = "Options related to the Minecraft window.";

	//Boats

	public static boolean patchEntityBoat;
	public static boolean preventUnderwaterBoatPassengerEjection;
	public static double underwaterBoatBuoyancy;

	//Client

	public static boolean fastLanguageSwitch;
	public static boolean forceTitleScreenOnDisconnect;
	public static boolean narratorKeybind;
	public static boolean patchMinecraftClass;
	public static boolean patchTitleScreenOnDisconnect;
	public static boolean removePotionGlint;
	public static boolean replacePortalRenderer;
	public static boolean rpreloadclient;

	//Client->Borderless Fullscreen

	public static boolean enableBorderlessFullscreen;
	public static int fullscreenMonitor;

	//Client->Window

	public static String icon16;
	public static String icon32;
	public static String title;

	//Misc

	public static boolean endPortalTweaks;
	public static boolean mc2025Fix;
	public static boolean minecartAIFix;
	public static boolean patchNetHandlerPlayServer;
	public static boolean recipeBookNBTFix;
	public static boolean replaceTeleporter;
	public static boolean rpreload;
	public static boolean skullStackingFix;
	public static boolean skullStackingRequiresSameTextures;

	//Speed limits

	public static float maxPlayerSpeed;
	public static float maxPlayerElytraSpeed;
	public static double maxPlayerVehicleSpeed;

	//Timeouts

	public static int keepAlivePacketInterval;
	public static long keepAlivePacketIntervalMillis;
	public static long keepAlivePacketIntervalLong;

	public static int loginTimeout;
	public static boolean patchLoginTimeout;

	public static int readTimeout;
	public static long readTimeoutMillis;

	static boolean setWindowSettings = true;

	private static final Field COMMENT = RandomPatches.MC_VERSION == 8 ?
			RPUtils.findField(Property.class, "comment") : null;

	private static final List<Runnable> reloadListeners = new ArrayList<>(1);

	private static Configuration config;
	private static Configuration currentConfig;

	public static boolean isNarratorKeybindEnabled() {
		return narratorKeybind && RandomPatches.MC_VERSION > 11 &&
				!RandomPatches.REBIND_NARRATOR_INSTALLED &&
				RandomPatches.IS_CLIENT;
	}

	public static boolean isEndPortalTweaksEnabled() {
		return endPortalTweaks && RandomPatches.MC_VERSION > 10 && RandomPatches.IS_CLIENT;
	}

	public static boolean isRecipeBookNBTFixEnabled() {
		return recipeBookNBTFix && RandomPatches.MC_VERSION > 11 &&
				!RandomPatches.VANILLAFIX_INSTALLED;
	}

	public static void setCurrentConfig(Configuration config) {
		currentConfig = config;
	}

	public static void reload() {
		if(config == null) {
			config = new Configuration(new File("config", RandomPatches.MOD_ID + ".cfg"));
		} else {
			config.load();
		}

		currentConfig = config;

		config.addCustomCategoryComment("boats", BOATS_COMMENT);

		patchEntityBoat = getBoolean(
				"patchEntityBoat",
				"boats",
				Defaults.PATCH_ENTITYBOAT,
				Comments.PATCH_ENTITYBOAT,
				false,
				true
		);

		preventUnderwaterBoatPassengerEjection = getBoolean(
				"preventUnderwaterBoatPassengerEjection",
				"boats",
				Defaults.PREVENT_UNDERWATER_BOAT_PASSENGER_EJECTION,
				Comments.PREVENT_UNDERWATER_BOAT_PASSENGER_EJECTION,
				false,
				false
		);

		underwaterBoatBuoyancy = getDouble(
				"underwaterBoatBuoyancy",
				"boats",
				Defaults.UNDERWATER_BOAT_BUOYANCY,
				Comments.UNDERWATER_BOAT_BUOYANCY
		);

		config.addCustomCategoryComment("client", CLIENT_COMMENT);

		fastLanguageSwitch = getBoolean(
				"fastLanguageSwitch",
				"client",
				Defaults.FAST_LANGUAGE_SWITCH,
				Comments.FAST_LANGUAGE_SWITCH,
				false,
				true
		);

		forceTitleScreenOnDisconnect = getBoolean(
				"forceTitleScreenOnDisconnect",
				"client",
				Defaults.FORCE_TITLE_SCREEN_ON_DISCONNECT,
				Comments.FORCE_TITLE_SCREEN_ON_DISCONNECT,
				false,
				false
		);

		narratorKeybind = getBoolean(
				"narratorKeybind",
				"client",
				Defaults.NARRATOR_KEYBIND,
				Comments.NARRATOR_KEYBIND,
				false,
				true
		);

		patchMinecraftClass = getBoolean(
				"patchMinecraftClass",
				"client",
				Defaults.PATCH_MINECRAFT_CLASS,
				Comments.PATCH_MINECRAFT_CLASS,
				false,
				true
		);

		patchTitleScreenOnDisconnect = getBoolean(
				"patchTitleScreenOnDisconnect",
				"client",
				Defaults.PATCH_TITLE_SCREEN_ON_DISCONNECT,
				Comments.PATCH_TITLE_SCREEN_ON_DISCONNECT,
				false,
				true
		);

		removePotionGlint = getBoolean(
				"removePotionGlint",
				"client",
				Defaults.REMOVE_POTION_GLINT,
				Comments.REMOVE_POTION_GLINT,
				false,
				true
		);

		rpreloadclient = getBoolean(
				"rpreloadclient",
				"client",
				Defaults.RPRELOADCLIENT,
				Comments.RPRELOADCLIENT,
				false,
				true
		);

		config.addCustomCategoryComment("client.window", WINDOW_COMMENT);

		icon16 = getString("icon16", "client.window", Defaults.ICON_16, Comments.ICON_16);
		icon32 = getString("icon32", "client.window", Defaults.ICON_32, Comments.ICON_32);
		title = getString("title", "client.window", Defaults.TITLE, Comments.TITLE);

		config.addCustomCategoryComment("misc", MISC_COMMENT);

		endPortalTweaks = getBoolean(
				"endPortalTweaks",
				"misc",
				Defaults.END_PORTAL_TWEAKS,
				Comments.END_PORTAL_TWEAKS,
				false,
				true
		);

		mc2025Fix = getBoolean(
				"mc2025Fix",
				"misc",
				Defaults.MC_2025_FIX,
				Comments.MC_2025_FIX,
				false,
				true
		);

		minecartAIFix = getBoolean(
				"minecartAIFix",
				"misc",
				Defaults.MINECART_AI_FIX,
				Comments.MINECART_AI_FIX,
				false,
				true
		);

		patchNetHandlerPlayServer = getBoolean(
				"patchNetHandlerPlayServer",
				"misc",
				Defaults.PATCH_NETHANDLERPLAYSERVER,
				Comments.PATCH_NETHANDLERPLAYSERVER,
				false,
				true
		);

		recipeBookNBTFix = getBoolean(
				"recipeBookNBTFix",
				"misc",
				Defaults.RECIPE_BOOK_NBT_FIX,
				Comments.RECIPE_BOOK_NBT_FIX,
				false,
				true
		);

		replaceTeleporter = getBoolean(
				"replaceTeleporter",
				"misc",
				Defaults.REPLACE_TELEPORTER,
				Comments.REPLACE_TELEPORTER,
				false,
				true
		);

		rpreload = getBoolean(
				"rpreload",
				"misc",
				Defaults.RPRELOAD,
				Comments.RPRELOAD,
				true,
				false
		);

		skullStackingFix = getBoolean(
				"skullStackingFix",
				"misc",
				Defaults.SKULL_STACKING_FIX,
				Comments.SKULL_STACKING_FIX,
				false,
				true
		);

		skullStackingRequiresSameTextures = getBoolean(
				"skullStackingRequiresSameTextures",
				"misc",
				Defaults.SKULL_STACKING_REQUIRES_SAME_TEXTURES,
				Comments.SKULL_STACKING_REQUIRES_SAME_TEXTURES,
				false,
				false
		);

		config.addCustomCategoryComment("speedLimits", SPEED_LIMITS_COMMENT);

		maxPlayerSpeed = (float) getDouble(
				"maxPlayerSpeed",
				"speedLimits",
				Defaults.MAX_PLAYER_SPEED,
				1.0,
				Comments.MAX_PLAYER_SPEED
		);

		maxPlayerElytraSpeed = (float) getDouble(
				"maxPlayerElytraSpeed",
				"speedLimits",
				Defaults.MAX_PLAYER_ELYTRA_SPEED,
				1.0,
				Comments.MAX_PLAYER_ELYTRA_SPEED
		);

		maxPlayerVehicleSpeed = getDouble(
				"maxPlayerVehicleSpeed",
				"speedLimits",
				Defaults.MAX_PLAYER_VEHICLE_SPEED,
				1.0,
				Comments.MAX_PLAYER_VEHICLE_SPEED
		);

		config.addCustomCategoryComment("timeouts", TIMEOUTS_COMMENT);

		keepAlivePacketInterval = getInt(
				"keepAlivePacketInterval",
				"timeouts",
				Defaults.KEEP_ALIVE_PACKET_INTERVAL,
				1,
				Integer.MAX_VALUE,
				Comments.KEEP_ALIVE_PACKET_INTERVAL
		);

		loginTimeout = getInt(
				"loginTimeout",
				"timeouts",
				Defaults.LOGIN_TIMEOUT,
				1,
				Integer.MAX_VALUE,
				Comments.LOGIN_TIMEOUT
		);

		patchLoginTimeout = getBoolean(
				"patchLoginTimeout",
				"timeouts",
				Defaults.PATCH_LOGIN_TIMEOUT,
				Comments.PATCH_LOGIN_TIMEOUT,
				false,
				true
		);

		readTimeout = getInt(
				"readTimeout",
				"timeouts",
				Defaults.READ_TIMEOUT,
				1,
				Integer.MAX_VALUE,
				Comments.READ_TIMEOUT
		);

		removeOldProperties(config);
		onReload();
		config.save();
	}

	public static void onReload() {
		if(icon16.isEmpty() && !icon32.isEmpty()) {
			icon16 = icon32;
		}

		if(icon32.isEmpty() && !icon16.isEmpty()) {
			icon32 = icon16;
		}

		if(RandomPatches.IS_CLIENT && Display.isCreated()) {
			setWindowSettings();
		}

		if(readTimeout < keepAlivePacketInterval) {
			readTimeout = keepAlivePacketInterval * 2;
		} else if(readTimeout % keepAlivePacketInterval != 0) {
			readTimeout = keepAlivePacketInterval * (readTimeout / keepAlivePacketInterval + 1);
		}

		keepAlivePacketIntervalMillis = keepAlivePacketInterval * 1000L;
		keepAlivePacketIntervalLong = keepAlivePacketInterval;
		readTimeoutMillis = readTimeout * 1000L;

		System.setProperty("fml.readTimeout", Integer.toString(readTimeout));
		System.setProperty("fml.loginTimeout", Integer.toString(loginTimeout));

		reloadListeners.forEach(Runnable::run);
	}

	public static void registerReloadListener(Runnable runnable) {
		reloadListeners.add(Objects.requireNonNull(runnable));
	}

	@SuppressWarnings("Duplicates")
	public static int getInt(String name, String category, int defaultValue, int minValue,
			int maxValue, String comment) {
		final Property property = currentConfig.get(category, name, defaultValue);

		property.setMinValue(minValue);
		property.setMaxValue(maxValue);
		setComment(
				property,
				comment + "\nMin: " + minValue + "\nMax: " + maxValue + "\nDefault: " + defaultValue
		);

		return property.getInt(defaultValue);
	}

	public static double getDouble(String name, String category, double defaultValue,
			String comment) {
		return getDouble(name, category, defaultValue, Double.MIN_VALUE, comment);
	}

	public static double getDouble(String name, String category, double defaultValue,
			double minValue, String comment) {
		return getDouble(name, category, defaultValue, minValue, Double.MAX_VALUE, comment);
	}

	@SuppressWarnings("Duplicates")
	public static double getDouble(String name, String category, double defaultValue,
			double minValue, double maxValue, String comment) {
		final Property property = currentConfig.get(category, name, defaultValue);

		property.setMinValue(minValue);
		property.setMaxValue(maxValue);
		setComment(
				property,
				comment + "\nMin: " + minValue + "\nMax: " + maxValue + "\nDefault: " + defaultValue
		);

		return property.getDouble(defaultValue);
	}

	@SuppressWarnings("Duplicates")
	public static boolean getBoolean(String name, String category, boolean defaultValue,
			String comment, boolean requiresWorldRestart, boolean requiresMcRestart) {
		final Property property = currentConfig.get(category, name, defaultValue);

		setComment(property, comment + "\nDefault: " + defaultValue);

		if(requiresMcRestart) {
			property.setRequiresMcRestart(true);
		} else if(requiresWorldRestart) {
			property.setRequiresWorldRestart(true);
		}

		return property.getBoolean(defaultValue);
	}

	public static String getString(String name, String category, String defaultValue,
			String comment) {
		return getString(name, category, defaultValue, comment, false, false);
	}

	@SuppressWarnings("Duplicates")
	public static String getString(String name, String category, String defaultValue,
			String comment, boolean requiresWorldRestart, boolean requiresMcRestart) {
		final Property property = currentConfig.get(category, name, defaultValue);

		setComment(property, comment + "\nDefault: " + defaultValue);

		if(requiresMcRestart) {
			property.setRequiresMcRestart(true);
		} else if(requiresWorldRestart) {
			property.setRequiresWorldRestart(true);
		}

		return property.getString();
	}

	public static String getComment(Property property) {
		if(RandomPatches.MC_VERSION == 8) {
			try {
				return (String) COMMENT.get(property);
			} catch(Exception ex) {
				RPUtils.crashReport("Error while getting comment", ex);
			}
		}

		return property.getComment();
	}

	public static void setComment(Property property, String comment) {
		if(RandomPatches.MC_VERSION == 8) {
			try {
				COMMENT.set(property, comment);
			} catch(Exception ex) {
				RPUtils.crashReport("Error while setting comment", ex);
			}

			return;
		}

		property.setComment(comment);
	}

	public static void removeOldProperties(Configuration config) {
		for(String name : config.getCategoryNames()) {
			final ConfigCategory category = config.getCategory(name);

			category.getValues().forEach((key, property) -> {
				final String comment = getComment(property);

				if(comment == null || comment.isEmpty()) {
					category.remove(key);
				}
			});

			if(category.getValues().isEmpty() || category.getComment() == null) {
				config.removeCategory(category);
			}
		}
	}

	public static void setWindowSettings() {
		if(!setWindowSettings || !RandomPatches.IS_CLIENT || RandomPatches.ITLT_INSTALLED) {
			return;
		}

		if(!RPStaticConfig.icon16.isEmpty()) {
			//If icon16 is empty, WindowIconHandler loads the Minecraft class too early
			WindowIconHandler.setWindowIcon();
		}

		Display.setTitle(RPStaticConfig.title);
	}

	public static void doNotSetWindowSettings() {
		setWindowSettings = false;
	}

	public static void doSetWindowSettings() {
		setWindowSettings = true;
	}
}
