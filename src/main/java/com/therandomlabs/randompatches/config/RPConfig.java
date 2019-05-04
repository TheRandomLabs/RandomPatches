package com.therandomlabs.randompatches.config;

import com.therandomlabs.randomlib.TRLUtils;
import com.therandomlabs.randomlib.config.Config;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.util.WindowIconHandler;
import org.lwjgl.opengl.Display;

@Config(RandomPatches.MOD_ID)
public final class RPConfig {
	public static final class Boats {
		@Config.RequiresMCRestart
		@Config.Property({
				"Whether to patch EntityBoat.",
				"This only works on 1.9 and above."
		})
		public static boolean patchEntityBoat = true;

		@Config.Property(
				"Prevents underwater boat passengers from being ejected after 60 ticks (3 seconds)."
		)
		public static boolean preventUnderwaterBoatPassengerEjection =
				RandomPatches.IS_DEOBFUSCATED;

		@Config.Property({
				"The buoyancy of boats when they are under flowing water.",
				"The vanilla default is -0.0007."
		})
		public static double underwaterBoatBuoyancy = 0.023;
	}

	public static final class Client {
		@Config.Category("Options related to the Minecraft window.")
		public static final Window window = null;

		@Config.RequiresMCRestart
		@Config.Property("Speeds up language switching.")
		public static boolean fastLanguageSwitch = true;

		@Config.Property(
				"Forces Minecraft to show the title screen after disconnecting rather than " +
						"the Multiplayer or Realms menu."
		)
		public static boolean forceTitleScreenOnDisconnect = RandomPatches.IS_DEOBFUSCATED;

		@Config.RequiresMCRestart
		@Config.Property({
				"Whether to add the Toggle Narrator keybind to the controls.",
				"This only works on 1.12 as the narrator does not exist in previous versions."
		})
		public static boolean narratorKeybind = true;

		@Config.RequiresMCRestart
		@Config.Property(
				"Set this to false to disable the Minecraft class patches " +
						"(the Toggle Narrator keybind and custom window title/icon)."
		)
		public static boolean patchMinecraftClass = true;

		@Config.RequiresMCRestart
		@Config.Property(
				"Set this to false to force disable the \"force title screen on disconnect\" " +
						"patch."
		)
		public static boolean patchTitleScreenOnDisconnect = true;

		@Config.RequiresMCRestart
		@Config.Property(
				"Whether to apply the potion glint patch so that the potion glowing effect can " +
						"be toggled."
		)
		public static boolean patchPotionGlint = true;

		@Config.Property("Whether to remove the glowing effect from potions.")
		public static boolean removePotionGlint = RandomPatches.IS_DEOBFUSCATED;

		@Config.RequiresWorldReload
		@Config.Property("Enables the /rpreloadclient command.")
		public static boolean rpreloadclient = true;

		public static boolean isNarratorKeybindEnabled() {
			return narratorKeybind && TRLUtils.MC_VERSION_NUMBER > 11 &&
					!RandomPatches.REBIND_INSTALLED && !RandomPatches.REBIND_NARRATOR_INSTALLED &&
					TRLUtils.IS_CLIENT;
		}
	}

	public static final class Misc {
		@Config.RequiresMCRestart
		@Config.Property({
				"Fixes the End portal and End gateway break particle textures and " +
						"improves End portal rendering.",
				"This only works on Minecraft 1.11 and above."
		})
		public static boolean endPortalTweaks = true;

		@Config.RequiresMCRestart
		@Config.Property({
				"Fixes MC-2025.",
				"This only works on 1.10 and above.",
				"More information can be found here: " +
						"https://www.reddit.com/r/Mojira/comments/8pgd4q/final_and_proper_fix_to_" +
						"mc2025_simple_reliable/"
		})
		public static boolean mc2025Fix = true;

		@Config.RequiresMCRestart
		@Config.Property(
				"Fixes MC-64836, which causes non-player entities to be allowed to control " +
						"minecarts using their AI."
		)
		public static boolean minecartAIFix = true;

		@Config.RequiresMCRestart
		@Config.Property({
				"Set this to false to disable the NetHandlerPlayServer patches " +
						"(the speed limits and disconnect timeouts).",
				"On 1.8, 1.8.8 and 1.8.9, these patches are always disabled."
		})
		public static boolean patchNetHandlerPlayServer = true;

		@Config.RequiresMCRestart
		@Config.Property(
				"Fixes MC-11944, which allows players to replace End portals, " +
						"End gateways and Nether portals using buckets."
		)
		public static boolean portalBucketReplacementFix = true;

		@Config.Property("Enables the portal bucket replacement fix for Nether portals.")
		public static boolean portalBucketReplacementFixForNetherPortals;

		@Config.RequiresMCRestart
		@Config.Property(
				"Fixes MC-129057, which prevents ingredients with NBT data from being " +
						"transferred to the crafting grid when a recipe is clicked in the " +
						"recipe book."
		)
		public static boolean recipeBookNBTFix = true;

		@Config.RequiresMCRestart
		@Config.Property(
				"Whether to allow other mods (namely RandomPortals) to replace the default " +
						"Teleporter on 1.12."
		)
		public static boolean replaceTeleporter = true;

		@Config.RequiresWorldReload
		@Config.Property("Enables the /rpreload command.")
		public static boolean rpreload = true;

		@Config.RequiresMCRestart
		@Config.Property("Fixes player skull stacking.")
		public static boolean skullStackingFix = true;

		@Config.Property(
				"Whether skull stacking requires the same textures or just the same player profile."
		)
		public static boolean skullStackingRequiresSameTextures = true;

		public static boolean areEndPortalTweaksEnabled() {
			return endPortalTweaks && TRLUtils.MC_VERSION_NUMBER > 10 && TRLUtils.IS_CLIENT;
		}

		public static boolean isRecipeBookNBTFixEnabled() {
			return recipeBookNBTFix && TRLUtils.MC_VERSION_NUMBER > 11 &&
					!RandomPatches.VANILLAFIX_INSTALLED;
		}
	}

	public static final class SpeedLimits {
		@Config.RangeDouble(min = 1.0)
		@Config.Property({
				"The maximum player speed.",
				"The vanilla default is 100.0."
		})
		public static float maxPlayerSpeed = 1000000.0F;

		@Config.RangeDouble(min = 1.0)
		@Config.Property({
				"The maximum player elytra speed.",
				"The vanilla default is 300.0."
		})
		public static float maxPlayerElytraSpeed = 1000000.0F;

		@Config.RangeDouble(min = 1.0)
		@Config.Property({
				"The maximum player vehicle speed.",
				"The vanilla default is 100.0."
		})
		public static double maxPlayerVehicleSpeed = 1000000.0;
	}

	public static final class Timeouts {
		@Config.RangeInt(min = 1)
		@Config.Property("The interval at which the server sends the KeepAlive packet.")
		public static int keepAlivePacketInterval = 15;

		@Config.RangeInt(min = 1)
		@Config.Property("The login timeout.")
		public static int loginTimeout = 900;

		@Config.RequiresMCRestart
		@Config.Property("Whether to apply the login timeout.")
		public static boolean patchLoginTimeout = true;

		@Config.RangeInt(min = 1)
		@Config.Property({
				"The read timeout.",
				"This is the time it takes for a player to be disconnected after not " +
						"responding to a KeepAlive packet.",
				"This value is automatically rounded up to a product of keepAlivePacketInterval.",
				"This only works on 1.12 and above."
		})
		public static int readTimeout = 90;

		public static long keepAlivePacketIntervalMillis;
		public static long keepAlivePacketIntervalLong;
		public static long readTimeoutMillis;

		public static void onReload() {
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
		}
	}

	public static final class Window {
		public static final String DEFAULT_ICON = RandomPatches.IS_DEOBFUSCATED ?
				"../src/main/resources/assets/randompatches/logo.png" : "";

		@Config.Property({
				"The path to the 16x16 Minecraft window icon.",
				"Leave this and the 32x32 icon blank to use the default icon."
		})
		public static String icon16 = DEFAULT_ICON;

		@Config.Property({
				"The path to the 32x32 Minecraft window icon.",
				"Leave this and the 16x16 icon blank to use the default icon."
		})
		public static String icon32 = DEFAULT_ICON;

		@Config.Property("The Minecraft window title.")
		public static String title = RandomPatches.IS_DEOBFUSCATED ?
				RandomPatches.NAME : RandomPatches.DEFAULT_WINDOW_TITLE;

		public static boolean setWindowSettings = true;

		public static void onReload() {
			if(icon16.isEmpty() && !icon32.isEmpty()) {
				icon16 = icon32;
			}

			if(icon32.isEmpty() && !icon16.isEmpty()) {
				icon32 = icon16;
			}
		}

		public static void onReloadClient() {
			if(Display.isCreated()) {
				setWindowSettings();
			}
		}

		public static void setWindowSettings() {
			if(!setWindowSettings || !TRLUtils.IS_CLIENT || RandomPatches.ITLT_INSTALLED) {
				return;
			}

			if(!icon16.isEmpty()) {
				//If icon16 is empty, WindowIconHandler loads the Minecraft class too early
				WindowIconHandler.setWindowIcon();
			}

			Display.setTitle(title);
		}
	}

	@Config.Category("Options related to boats.")
	public static final Boats boats = null;

	@Config.Category("Options related to client-sided features.")
	public static final Client client = null;

	@Config.Category("Options that don't fit into any other categories.")
	public static final Misc misc = null;

	@Config.Category("Options related to the movement speed limits.")
	public static final SpeedLimits speedLimits = null;

	@Config.Category("Options related to the disconnect timeouts.")
	public static final Timeouts timeouts = null;
}
