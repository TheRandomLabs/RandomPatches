/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2021 TheRandomLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.therandomlabs.randompatches;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.SpecDoubleInRange;
import com.electronwill.nightconfig.core.conversion.SpecFloatInRange;
import com.electronwill.nightconfig.core.conversion.SpecIntInRange;
import com.therandomlabs.autoconfigtoml.TOMLConfigSerializer;
import com.therandomlabs.randompatches.client.CauldronWaterTranslucencyHandler;
import com.therandomlabs.randompatches.client.RPKeyBindingHandler;
import com.therandomlabs.randompatches.client.RPWindowHandler;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.StringUtils;

/**
 * The RandomPatches configuration.
 */
@SuppressWarnings("CanBeFinal")
@TOMLConfigSerializer.Comment({
		"RandomPatches configuration.",
		"All configuration options not under [client] are server-sided unless otherwise stated."
})
@Config(name = RandomPatches.MOD_ID)
public final class RPConfig implements ConfigData {
	public static final class Client {
		@TOMLConfigSerializer.Comment("Client-sided bug fixes.")
		@ConfigEntry.Category("bug_fixes")
		@ConfigEntry.Gui.CollapsibleObject
		@ConfigEntry.Gui.Tooltip
		public ClientBugFixes bugFixes = new ClientBugFixes();

		@TOMLConfigSerializer.Comment("Options related to key bindings.")
		@ConfigEntry.Category("key_bindings")
		@ConfigEntry.Gui.CollapsibleObject
		@ConfigEntry.Gui.Tooltip
		public KeyBindings keyBindings = new KeyBindings();

		@TOMLConfigSerializer.Comment("Options related to the Minecraft window.")
		@ConfigEntry.Category("window")
		@ConfigEntry.Gui.CollapsibleObject
		@ConfigEntry.Gui.Tooltip
		public Window window = new Window();

		@TOMLConfigSerializer.Comment({
				"Optimizes bamboo rendering.",
				"This works by overriding the method that returns the ambient occlusion light " +
						"value for the bamboo block, which runs some expensive logic, but always " +
						"returns 1.0F.",
				"Changes to this option are applied after a game restart."
		})
		@ConfigEntry.Gui.Tooltip
		public boolean optimizeBambooRendering = true;

		@TOMLConfigSerializer.Comment({
				"Removes the glowing effect from potions.",
				"This makes the potion colors more visible."
		})
		@ConfigEntry.Gui.Tooltip
		public boolean removeGlowingEffectFromPotions = true;

		@TOMLConfigSerializer.Comment("Removes the glowing effect from enchanted books.")
		@ConfigEntry.Gui.Tooltip
		public boolean removeGlowingEffectFromEnchantedBooks =
				FabricLoader.getInstance().isDevelopmentEnvironment();

		@TOMLConfigSerializer.Comment(
				"Disables the warning that displays when loading a world that uses experimental " +
						"settings."
		)
		@ConfigEntry.Gui.Tooltip
		public boolean disableExperimentalSettingsWarning = true;

		@SpecFloatInRange(min = Float.MIN_VALUE, max = 260.0F)
		@TOMLConfigSerializer.Comment({
				"The framerate limit slider step size.",
				"The vanilla default is 10.0."
		})
		@ConfigEntry.Gui.Tooltip
		public float framerateLimitSliderStepSize = 1.0F;

		@TOMLConfigSerializer.Comment(
				"Causes Minecraft to show the main menu screen after disconnecting rather than " +
						"the Realms or multiplayer screen."
		)
		@ConfigEntry.Gui.Tooltip
		public boolean returnToMainMenuAfterDisconnect =
				FabricLoader.getInstance().isDevelopmentEnvironment();

		@TOMLConfigSerializer.Comment("Gives capes to RandomPatches contributors.")
		@ConfigEntry.Gui.Tooltip
		public boolean contributorCapes = true;

		@TOMLConfigSerializer.Comment({
				"The name of the command that reloads this configuration from disk on the client.",
				"Set this to an empty string to disable the command.",
				"Changes to this option are applied after a game restart."
		})
		@ConfigEntry.Gui.Tooltip
		public String configReloadCommand = "rpclientconfigreload";

		/**
		 * Returns whether contributor capes are enabled.
		 *
		 * @return {@code true} if contributor capes are enabled, or otherwise {@code false}.
		 */
		public boolean contributorCapes() {
			final List<String> mixinBlacklist = RandomPatches.config().misc.mixinBlacklist;
			return contributorCapes && !mixinBlacklist.contains("AbstractClientPlayerEntity") &&
					!mixinBlacklist.contains("PlayerEntityRendererContributorCapes") &&
					!mixinBlacklist.contains("PlayerListEntry");
		}
	}

	public static final class ClientBugFixes implements ConfigData {
		@TOMLConfigSerializer.Comment({
				"Fixes water in cauldrons rendering as opaque.",
				"This bug is reported as MC-13187: https://bugs.mojang.com/browse/MC-13187",
				"Changes to this option are applied after resources are reloaded or a world " +
						"restart."
		})
		@ConfigEntry.Gui.Tooltip
		public boolean fixWaterInCauldronsRenderingAsOpaque = true;

		@TOMLConfigSerializer.Comment({
				"Fixes end portals only rendering from above.",
				"This bug is reported as MC-3366: https://bugs.mojang.com/browse/MC-3366"
		})
		@ConfigEntry.Gui.Tooltip
		public boolean fixEndPortalsOnlyRenderingFromAbove = true;

		@TOMLConfigSerializer.Comment({
				"Fixes only 18 out of 20 rows of pixels showing of villager robe textures.",
				"This issue also affects witches.",
				"This bug is reported as MC-53312: https://bugs.mojang.com/browse/MC-53312",
				"Changes to this option are applied after a game restart."
		})
		@ConfigEntry.Gui.Tooltip
		public boolean fixVillagerRobeTextures = true;

		@TOMLConfigSerializer.Comment({
				"Fixes the player model sometimes disappearing in certain instances.",
				"This is most noticeable when flying with elytra in a straight line in " +
						"third-person mode.",
				"A video of this issue can be found here: https://youtu.be/YdbxknpfJHQ",
				"Changes to this option are applied after a game restart."
		})
		@ConfigEntry.Gui.Tooltip
		public boolean fixInvisiblePlayerModel = true;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void validatePostLoad() {
			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
				CauldronWaterTranslucencyHandler.onConfigReload();
			}
		}
	}

	public static final class KeyBindings implements ConfigData {
		@TOMLConfigSerializer.Comment({
				"The secondary sprint key binding.",
				"This allows double-tap sprinting to be disabled, " +
						"fixing MC-203401: https://bugs.mojang.com/browse/MC-203401"
		})
		@ConfigEntry.Gui.Tooltip
		public boolean secondarySprint = true;

		@TOMLConfigSerializer.Comment({
				"The dismount key binding.",
				"This allows the dismount key to be different from the sneak key."
		})
		@ConfigEntry.Gui.Tooltip
		public boolean dismount = true;

		@TOMLConfigSerializer.Comment({
				"The narrator toggle key binding.",
				"This fixes MC-122645: https://bugs.mojang.com/browse/MC-122645"
		})
		@ConfigEntry.Gui.Tooltip
		public boolean toggleNarrator = true;

		@TOMLConfigSerializer.Comment({
				"The pause key binding.",
				"This is only for pausing and unpausing the game; the Escape key is still used " +
						"to close GUI screens.",
				"This partially fixes MC-147718: https://bugs.mojang.com/browse/MC-147718"
		})
		@ConfigEntry.Gui.Tooltip
		public boolean pause = true;

		@Path("toggle_gui")
		@TOMLConfigSerializer.Comment({
				"The GUI toggle key binding.",
				"This partially fixes MC-147718: https://bugs.mojang.com/browse/MC-147718"
		})
		@ConfigEntry.Gui.Tooltip
		public boolean toggleGUI = true;

		@TOMLConfigSerializer.Comment({
				"The debug info toggle key binding.",
				"The F3 key is still used for F3 actions.",
				"This partially fixes MC-147718: https://bugs.mojang.com/browse/MC-147718"
		})
		@ConfigEntry.Gui.Tooltip
		public boolean toggleDebugInfo = true;

		@TOMLConfigSerializer.Comment({
				"Allows double-tap sprinting while flying.",
				"This fixes MC-68453: https://bugs.mojang.com/browse/MC-68453"
		})
		@ConfigEntry.Gui.Tooltip
		public boolean doubleTapSprintingWhileFlying = true;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void validatePostLoad() {
			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
				RPKeyBindingHandler.onConfigReload();
			}
		}

		/**
		 * Returns whether the secondary sprint key binding is enabled.
		 *
		 * @return {@code true} if the secondary sprint key binding is enabled, or otherwise
		 * {@code false}.
		 */
		public boolean secondarySprint() {
			return secondarySprint &&
					!RandomPatches.config().misc.mixinBlacklist.contains("ClientPlayerEntity");
		}

		/**
		 * Returns whether the dismount key binding is enabled.
		 *
		 * @return {@code true} if the dismount key binding is enabled, or otherwise {@code false}.
		 */
		public boolean dismount() {
			return dismount &&
					!RandomPatches.config().misc.mixinBlacklist.contains("ClientPlayerEntity");
		}
	}

	public static final class Window implements ConfigData {
		@ConfigEntry.Gui.Excluded
		public static final String DEFAULT_TITLE =
				FabricLoader.getInstance().isDevelopmentEnvironment() ?
						"RandomPatches (${username})" : "Minecraft ${mcversion}";

		@ConfigEntry.Gui.Excluded
		private static final String DEFAULT_ICON =
				FabricLoader.getInstance().isDevelopmentEnvironment() ?
						"../src/main/resources/assets/randompatches/icon.png" : "";

		@TOMLConfigSerializer.Comment({
				"The Minecraft window title.",
				"The current activity is not available.",
				"Variables:",
				" - ${mcversion}: The Minecraft version",
				" - ${username}: The username.",
				" - ${modsloaded}: The number of mods loaded.",
				" - ${modversion:modid}: The version of the mod with the specified ID.",
				"'$' can be escaped by using an extra '$'."
		})
		@ConfigEntry.Gui.Tooltip
		public String title = FabricLoader.getInstance().isDevelopmentEnvironment() ?
				"RandomPatches (${username}) - ${modsloaded} mods loaded" : DEFAULT_TITLE;

		@TOMLConfigSerializer.Comment({
				"The Minecraft window title that also takes into account the current activity.",
				"Variables:",
				" - ${mcversion}: The Minecraft version",
				" - ${activity}: The current activity.",
				" - ${username}: The username.",
				" - ${modsloaded}: The number of mods loaded.",
				" - ${modversion:modid}: The version of the mod with the specified ID.",
				"'$' can be escaped by using an extra '$'."
		})
		@ConfigEntry.Gui.Tooltip
		public String titleWithActivity = FabricLoader.getInstance().isDevelopmentEnvironment() ?
				"RandomPatches (${username}) - ${modsloaded} mods loaded - ${activity}" :
				"Minecraft ${mcversion} - ${activity}";

		@Path("icon_16x16")
		@TOMLConfigSerializer.Comment({
				"The path to the 16x16 Minecraft window icon relative to the Minecraft " +
						"instance directory.",
				"Forward slashes should be used even on Windows to preserve compatibility with " +
						"other platforms.",
				"Backward slashes will automatically be replaced."
		})
		@ConfigEntry.Gui.Tooltip
		public String icon16 = DEFAULT_ICON;

		@Path("icon_32x32")
		@TOMLConfigSerializer.Comment({
				"The path to the 16x16 Minecraft window icon relative to the Minecraft " +
						"instance directory.",
				"Forward slashes should be used even on Windows to preserve compatibility with " +
						"other platforms.",
				"Backward slashes will automatically be replaced."
		})
		@ConfigEntry.Gui.Tooltip
		public String icon32 = DEFAULT_ICON;

		@Path("icon_256x256")
		@TOMLConfigSerializer.Comment({
				"The path to the 256x256 Minecraft window icon relative to the Minecraft " +
						"instance directory.",
				"This is only used on Mac OS X."
		})
		@ConfigEntry.Gui.Tooltip
		public String icon256 = DEFAULT_ICON;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void validatePostLoad() {
			icon16 = validateIconPath(icon16);
			icon32 = validateIconPath(icon32);
			icon256 = validateIconPath(icon256);

			if (icon16.isEmpty()) {
				if (!icon256.isEmpty()) {
					icon16 = icon256;
				} else if (!icon32.isEmpty()) {
					icon16 = icon32;
				}
			}

			if (icon32.isEmpty()) {
				icon32 = icon256.isEmpty() ? icon16 : icon256;
			}

			if (icon256.isEmpty()) {
				icon256 = icon32;
			}

			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
				RPWindowHandler.onConfigReload();
			}
		}

		private String validateIconPath(String path) {
			try {
				Paths.get(path);
				return path.replace('\\', '/');
			} catch (InvalidPathException ex) {
				return DEFAULT_ICON;
			}
		}
	}

	public static final class ConnectionTimeouts implements ConfigData {
		@TOMLConfigSerializer.Comment({
				"The connection read timeout in seconds.",
				"This value is used on both the client and the server."
		})
		@SpecIntInRange(min = 1, max = Integer.MAX_VALUE)
		@ConfigEntry.Gui.Tooltip
		public int readTimeoutSeconds = 120;

		@TOMLConfigSerializer.Comment("The login timeout in ticks.")
		@SpecIntInRange(min = 1, max = Integer.MAX_VALUE)
		@ConfigEntry.Gui.Tooltip
		public int loginTimeoutTicks = 2400;

		@TOMLConfigSerializer.Comment(
				"The interval in seconds at which KeepAlive packets are sent to clients."
		)
		@SpecIntInRange(min = 1, max = Integer.MAX_VALUE)
		@ConfigEntry.Gui.Tooltip
		public int keepAlivePacketIntervalSeconds = 15;

		@TOMLConfigSerializer.Comment({
				"The KeepAlive timeout in seconds.",
				"This is how long the server waits for a player to return a KeepAlive packet " +
						"before disconnecting them.",
				"This is automatically rounded up to a multiple of the KeepAlive packet interval."
		})
		@SpecIntInRange(min = 1, max = Integer.MAX_VALUE)
		@ConfigEntry.Gui.Tooltip
		public int keepAliveTimeoutSeconds = 120;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void validatePostLoad() {
			if (keepAliveTimeoutSeconds < keepAlivePacketIntervalSeconds) {
				keepAliveTimeoutSeconds = keepAlivePacketIntervalSeconds;
			} else if (keepAliveTimeoutSeconds % keepAlivePacketIntervalSeconds != 0) {
				final int multiple = keepAliveTimeoutSeconds / keepAlivePacketIntervalSeconds + 1;
				keepAliveTimeoutSeconds = keepAlivePacketIntervalSeconds * multiple;
			}
		}
	}

	public static final class PacketSizeLimits {
		@SpecIntInRange(min = 0x100, max = Integer.MAX_VALUE)
		@TOMLConfigSerializer.Comment({
				"The maximum compressed packet size.",
				"The vanilla limit is " + 0x200000 + ".",
				"This option is both client and server-sided.",
				"Setting this to a higher value than the vanilla limit can fix MC-185901, " +
						"which may cause players to be disconnected: " +
						"https://bugs.mojang.com/browse/MC-185901"
		})
		@ConfigEntry.Gui.Tooltip
		public int maxCompressedPacketSize = 0x1000000;

		@Path("max_nbt_compound_tag_packet_size")
		@SpecIntInRange(min = 0x100, max = Integer.MAX_VALUE)
		@TOMLConfigSerializer.Comment({
				"The maximum NBT compound tag packet size.",
				"The vanilla limit is " + 0x200000 + ".",
				"This option is both client and server-sided.",
				"Setting this to a higher value than the vanilla limit may prevent players from " +
						"being disconnected."
		})
		@ConfigEntry.Gui.Tooltip
		public int maxNBTCompoundTagPacketSize = 0x1000000;

		@SpecIntInRange(min = 0x100, max = Integer.MAX_VALUE)
		@TOMLConfigSerializer.Comment({
				"The maximum client custom payload packet size.",
				"The vanilla limit is " + Short.MAX_VALUE + ".",
				"Setting this to a higher value than the vanilla limit may prevent the client " +
						"from being disconnected."
		})
		@ConfigEntry.Gui.Tooltip
		public int maxClientCustomPayloadPacketSize = 0x1000000;
	}

	public static final class PlayerSpeedLimits {
		@TOMLConfigSerializer.Comment({
				"The maximum player speed when not riding a vehicle or flying with elytra.",
				"The vanilla default is 100.0."
		})
		@SpecFloatInRange(min = 0.0F, max = Float.MAX_VALUE)
		@ConfigEntry.Gui.Tooltip
		public float defaultMaxSpeed = 1000000.0F;

		@TOMLConfigSerializer.Comment({
				"The maximum player elytra speed.",
				"The vanilla default is 300.0."
		})
		@SpecFloatInRange(min = 0.0F, max = Float.MAX_VALUE)
		@ConfigEntry.Gui.Tooltip
		public float maxElytraSpeed = 1000000.0F;

		@TOMLConfigSerializer.Comment({
				"The maximum player vehicle speed.",
				"The vanilla default is 100.0."
		})
		@SpecDoubleInRange(min = 0.0, max = Double.MAX_VALUE)
		@ConfigEntry.Gui.Tooltip
		public double maxVehicleSpeed = 1000000.0;
	}

	public static final class Misc implements ConfigData {
		@TOMLConfigSerializer.Comment("Miscellaneous bug fixes.")
		@ConfigEntry.Category("bug_fixes")
		@ConfigEntry.Gui.CollapsibleObject
		@ConfigEntry.Gui.Tooltip
		public MiscBugFixes bugFixes = new MiscBugFixes();

		@TOMLConfigSerializer.Comment({
				"The buoyancy of boats when they are under flowing water.",
				"The vanilla default is -0.0007.",
				"Setting this to a positive value allows boats to float up when they move into " +
						"a higher block of water, fixing MC-91206: " +
						"https://bugs.mojang.com/browse/MC-91206"
		})
		@ConfigEntry.Gui.Tooltip
		public double boatBuoyancyUnderFlowingWater =
				FabricLoader.getInstance().isDevelopmentEnvironment() ? 5.0 : 0.023;

		@SpecIntInRange(min = -1, max = Integer.MAX_VALUE)
		@TOMLConfigSerializer.Comment({
				"How long it takes in ticks for a boat passenger to be ejected when underwater.",
				"Set this to -1 to disable underwater boat passenger ejection."
		})
		@ConfigEntry.Gui.Tooltip
		public int underwaterBoatPassengerEjectionDelayTicks =
				FabricLoader.getInstance().isDevelopmentEnvironment() ? -1 : 60;

		@TOMLConfigSerializer.Comment({
				"The name of the command that reloads this configuration from disk.",
				"Set this to an empty string to disable the command.",
				"Changes to this option are applied when a server is loaded."
		})
		@ConfigEntry.Gui.Tooltip
		public String configReloadCommand = "rpconfigreload";

		@TOMLConfigSerializer.Comment({
				"Disables the execution of DataFixerUpper.",
				"This reduces RAM usage and decreases the Minecraft loading time.",
				"WARNING: THIS IS NOT RECOMMENDED! RandomPatches is not responsible for any " +
						"damage caused by this feature.",
				"- DataFixerUpper is responsible for the backwards compatibility of worlds.",
				"- Ensure you have used the Optimize feature on any worlds from previous " +
						"versions of Minecraft before enabling this feature.",
				"- Before migrating worlds to new versions of Minecraft, ensure this feature is " +
						"disabled, and use the Optimize feature again before re-enabling it.",
				"- Take regular backups of your worlds.",
				"Changes to this option are applied after a game restart."
		})
		@ConfigEntry.Gui.Tooltip
		public boolean disableDataFixerUpper =
				FabricLoader.getInstance().isDevelopmentEnvironment();

		@TOMLConfigSerializer.Comment({
				"A list of mixins that should not be applied.",
				"These are the mixins that are not automatically disabled when the features that " +
						"depend on them are:",
				"- AbstractClientPlayerEntity, PlayerEntityRendererContributorCapes, " +
						"PlayerListEntry: Required for contributor capes.",
				"- AnimalEntity: Required for fixing animal breeding hearts.",
				"- BoatEntity: Required for modifying boat options.",
				"- ChannelInitializer: Required for changing the read timeout.",
				"- ClientPlayerEntity: Required for the secondary sprint and dismount key " +
						"bindings.",
				"- ClientPlayNetworkHandler: Required for making the dismount overlay message " +
						"show the correct key when the dismount key binding is enabled.",
				"- CommandManager: Required for the server-sided configuration reload command.",
				"- CompoundTag: Required for fixing player head stacking.",
				"- CustomPayloadC2SPacket: Required for setting the maximum client custom " +
						"payload packet size.",
				"- EnchantedBookItem: Required for removing the glowing effect from enchanted " +
						"books.",
				"- EndPortalBlockEntityRenderer: Required for fixing end portal rendering.",
				"- Entity:",
				"  - Required for fixing MC-2025.",
				"  - Required for fixing entities not being considered wet in cauldrons " +
						"filled with water.",
				"- GameMenuScreen: Required for making Minecraft show the main menu screen after " +
						"disconnecting rather than the Realms or multiplayer screen.",
				"- GameOptions: Required for all key bindings added by RandomPatches.",
				"- InputSlotFiller: Required for fixing the recipe book not moving " +
						"ingredients with tags.",
				"- KeyBinding:",
				"  - Required for making the forward movement key not conflict with the " +
						"secondary sprint key.",
				"  - Required for making the sneak key not conflict with the dismount key.",
				"- Keyboard: Required for the narrator toggle, pause, GUI toggle and debug key " +
						"bindings.",
				"- MinecraftClient:",
				"  - Required for changing Minecraft window options.",
				"  - Required for disabling the warning that displays when loading a world that " +
						"uses experimental settings.",
				"- Mouse: Required for using mouse buttons for the narrator toggle, pause, GUI " +
						"toggle and debug key bindings.",
				"- Option: Required for modifying the framerate limit slider step size.",
				"- PacketByteBuf: Required for setting the maximum NBT compound tag packet size.",
				"- PacketInflater: Required for setting the maximum compressed packet size.",
				"- PotionItem: Required for removing the glowing effect from potions.",
				"- RenderLayers: Required for fixing water in cauldrons rendering as opaque.",
				"- ServerLoginNetworkHandler: Required for changing the login timeout.",
				"- ServerPlayNetworkHandlerKeepAlive: Required for changing KeepAlive packet " +
						"settings.",
				"- ServerPlayNetworkHandlerPlayerSpeedLimits: Required for changing player speed " +
						"limits.",
				"- ThreadedAnvilChunkStorageMixin: Required for fixing duplicate entity UUIDs.",
				"This option is both client and server-sided.",
				"Changes to this option are applied after a game restart."
		})
		@ConfigEntry.Gui.Tooltip
		public List<String> mixinBlacklist = new ArrayList<>();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void validatePostLoad() {
			configReloadCommand = configReloadCommand.trim();
			Collections.sort(mixinBlacklist);
		}

		/**
		 * Returns whether the specified RandomPatches mixin class is enabled.
		 *
		 * @param mixinClassName a RandomPatches mixin class name.
		 * @return {@code true} if the specified RandomPatches mixin class is enabled,
		 * or otherwise {@code false}.
		 */
		public boolean isMixinClassEnabled(String mixinClassName) {
			final String[] parts = StringUtils.split(mixinClassName, '.');
			final String simpleName = StringUtils.substring(parts[parts.length - 1], 0, -5);

			if ("MinecraftClientPostInit".equals(simpleName)) {
				return true;
			}

			if ("VillagerResemblingModel".equals(simpleName) &&
					!RandomPatches.config().client.bugFixes.fixVillagerRobeTextures) {
				return false;
			}

			if ("PlayerEntityRenderer".equals(simpleName) &&
					!RandomPatches.config().client.bugFixes.fixInvisiblePlayerModel) {
				return false;
			}

			if ("BambooBlock".equals(simpleName) &&
					!RandomPatches.config().client.optimizeBambooRendering) {
				return false;
			}

			if ("InputSlotFiller".equals(simpleName) &&
					FabricLoader.getInstance().isModLoaded("nbtcrafting")) {
				return false;
			}

			if ("ServerTickScheduler".equals(simpleName) && !bugFixes.fixTickSchedulerDesync) {
				return false;
			}

			if (mixinClassName.contains("datafixerupper") && !disableDataFixerUpper) {
				return false;
			}

			return !mixinBlacklist.contains(simpleName);
		}
	}

	public static final class MiscBugFixes {
		@TOMLConfigSerializer.Comment({
				"Fixes the \"TickNextTick list out of synch\" IllegalStateException.",
				"For more information, see: https://github.com/SleepyTrousers/EnderCore/issues/105",
				"This bug is reported as MC-28660: https://bugs.mojang.com/browse/MC-28660"
		})
		@ConfigEntry.Gui.Tooltip
		public boolean fixTickSchedulerDesync = true;

		@Path("fix_mc-2025")
		@TOMLConfigSerializer.Comment({
				"Fixes MC-2025: https://bugs.mojang.com/browse/MC-2025",
				"For more information, see: https://redd.it/8pgd4q"
		})
		@ConfigEntry.Gui.Tooltip
		public boolean fixMC2025 = true;

		@TOMLConfigSerializer.Comment({
				"Fixes animals which can breed only showing hearts once initially instead of " +
						"continuously.",
				"This bug is reported as MC-93826: https://bugs.mojang.com/browse/MC-93826"
		})
		@ConfigEntry.Gui.Tooltip
		public boolean fixAnimalBreedingHearts = true;

		@TOMLConfigSerializer.Comment({
				"Fixes entities not being considered wet in cauldrons filled with water.",
				"This allows players to use Riptide in cauldrons filled with water, " +
						"fixing MC-145311: https://bugs.mojang.com/browse/MC-145311",
				"This also allows players to receive the Conduit Power effect in cauldrons " +
						"filled with water.",
		})
		@ConfigEntry.Gui.Tooltip
		public boolean fixEntitiesNotBeingConsideredWetInCauldrons = true;

		@TOMLConfigSerializer.Comment({
				"Fixes player heads from the same player sometimes not stacking.",
				"DISABLED: Disables this fix.",
				"REQUIRE_SAME_PLAYER_AND_TEXTURE_URL: Player heads can stack if they are from " +
						"the same player and have the same texture URL.",
				"REQUIRE_SAME_PLAYER: Player heads can stack if they are from the same player.",
				"This bug is reported as MC-100044: https://bugs.mojang.com/browse/MC-100044"
		})
		@ConfigEntry.Gui.Tooltip
		public PlayerHeadStackingFixMode fixPlayerHeadStacking =
				PlayerHeadStackingFixMode.REQUIRE_SAME_PLAYER_AND_TEXTURE_URL;

		@Path("fix_duplicate_entity_uuids")
		@TOMLConfigSerializer.Comment({
				"Fixes duplicate entity UUIDs by assigning new UUIDs to the affected entities.",
				"This bug is reported as MC-95649: https://bugs.mojang.com/browse/MC-95649"
		})
		@ConfigEntry.Gui.Tooltip
		public boolean fixDuplicateEntityUUIDs = true;

		@TOMLConfigSerializer.Comment({
				"Fixes the recipe book not automatically moving ingredients with NBT tags to the " +
						"crafting grid.",
				"This bug is reported as MC-129057: https://bugs.mojang.com/browse/MC-129057"
		})
		@ConfigEntry.Gui.Tooltip
		public boolean fixRecipeBookNotMovingIngredientsWithTags = true;
	}

	/**
	 * Player head stacking fix modes.
	 */
	public enum PlayerHeadStackingFixMode {
		/**
		 * Disable the fix.
		 */
		DISABLED,
		/**
		 * Require the same player and texture URL.
		 */
		REQUIRE_SAME_PLAYER_AND_TEXTURE_URL,
		/**
		 * Require the same player.
		 */
		REQUIRE_SAME_PLAYER
	}

	@TOMLConfigSerializer.Comment("Client-sided options.")
	@ConfigEntry.Category("client")
	@ConfigEntry.Gui.TransitiveObject
	public Client client = new Client();

	@TOMLConfigSerializer.Comment("Options related to connection timeouts.")
	@ConfigEntry.Category("connection_timeouts")
	@ConfigEntry.Gui.TransitiveObject
	public ConnectionTimeouts connectionTimeouts = new ConnectionTimeouts();

	@TOMLConfigSerializer.Comment("Options related to packet size limits.")
	@ConfigEntry.Category("packet_size_limits")
	@ConfigEntry.Gui.TransitiveObject
	public PacketSizeLimits packetSizeLimits = new PacketSizeLimits();

	@TOMLConfigSerializer.Comment({
			"Options related to player speed limits.",
			"These options are used to fix MC-90062: https://bugs.mojang.com/browse/MC-90062"
	})
	@ConfigEntry.Category("player_speed_limits")
	@ConfigEntry.Gui.TransitiveObject
	public PlayerSpeedLimits playerSpeedLimits = new PlayerSpeedLimits();

	@TOMLConfigSerializer.Comment("Miscellaneous options.")
	@ConfigEntry.Category("misc")
	@ConfigEntry.Gui.TransitiveObject
	public Misc misc = new Misc();
}
