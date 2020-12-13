/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 TheRandomLabs
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.SpecIntInRange;
import com.google.common.reflect.ClassPath;
import com.therandomlabs.randompatches.client.RPWindowHandler;
import com.therandomlabs.randompatches.mixin.RPMixinConfig;
import me.shedaniel.autoconfig1u.ConfigData;
import me.shedaniel.autoconfig1u.annotation.Config;
import me.shedaniel.autoconfig1u.annotation.ConfigEntry;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.StringUtils;

/**
 * The RandomPatches configuration.
 */
@SuppressWarnings("CanBeFinal")
@TOMLConfigSerializer.Comment({
		"RandomPatches configuration.",
		"All configuration options not under the \"client\" category are server-sided unless " +
				"otherwise stated."
})
@Config(name = RandomPatches.MOD_ID)
public final class RPConfig implements ConfigData {
	public static final class Client {
		@TOMLConfigSerializer.Comment("Options related to the Minecraft window.")
		@ConfigEntry.Category("window")
		@ConfigEntry.Gui.CollapsibleObject
		@ConfigEntry.Gui.Tooltip
		public Window window = new Window();
	}

	public static final class Window implements ConfigData {
		@ConfigEntry.Gui.Excluded
		public static final String DEFAULT_TITLE = FMLEnvironment.production ?
				"Minecraft* %s" : "RandomPatches";

		@ConfigEntry.Gui.Excluded
		private static final String DEFAULT_ICON =
				FMLEnvironment.production ? "" : "../src/main/resources/logo.png";

		@TOMLConfigSerializer.Comment({
				"The Minecraft window title.",
				"The Minecraft version is provided as an argument."
		})
		@ConfigEntry.Gui.Tooltip
		public String title = DEFAULT_TITLE;

		@TOMLConfigSerializer.Comment({
				"The Minecraft window title that takes into account the current activity.",
				"The Minecraft version and current activity are provided as arguments.",
				"For example: \"RandomPatches - %2$s\""
		})
		@ConfigEntry.Gui.Tooltip
		public String titleWithActivity = FMLEnvironment.production ?
				"Minecraft* %s - %s" : "RandomPatches - %2$s";

		@Path("icon_16x16")
		@TOMLConfigSerializer.Comment("The path to the 16x16 Minecraft window icon.")
		@ConfigEntry.Gui.Tooltip
		public String icon16 = DEFAULT_ICON;

		@Path("icon_32x32")
		@TOMLConfigSerializer.Comment("The path to the 16x16 Minecraft window icon.")
		@ConfigEntry.Gui.Tooltip
		public String icon32 = DEFAULT_ICON;

		@Path("icon_256x256")
		@TOMLConfigSerializer.Comment({
				"The path to the 256x256 Minecraft window icon.",
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

			RPWindowHandler.onConfigReload();
		}

		private String validateIconPath(String path) {
			try {
				Paths.get(path);
				return path;
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

	public static final class PlayerSpeedLimits {
		@TOMLConfigSerializer.Comment({
				"The maximum player speed.",
				"The vanilla default is 100.0."
		})
		@ConfigEntry.Gui.Tooltip
		public float maxSpeed = 1000000.0F;

		@TOMLConfigSerializer.Comment({
				"The maximum player elytra speed.",
				"The vanilla default is 300.0."
		})
		@ConfigEntry.Gui.Tooltip
		public float maxElytraSpeed = 1000000.0F;

		@TOMLConfigSerializer.Comment({
				"The maximum player vehicle speed.",
				"The vanilla default is 100.0."
		})
		@ConfigEntry.Gui.Tooltip
		public double maxVehicleSpeed = 1000000.0;
	}

	@SuppressWarnings("UnstableApiUsage")
	public static final class Misc implements ConfigData {
		@ConfigEntry.Gui.Excluded
		private static final Map<String, String> mixins = RPMixinConfig.getMixinClasses().stream().
				collect(Collectors.toMap(
						ClassPath.ClassInfo::getName,
						info -> StringUtils.substring(info.getSimpleName(), -5)
				));

		@TOMLConfigSerializer.Comment({
				"The name of the command that reloads this configuration from disk.",
				"Set this to an empty string to disable the command.",
				"Changes to this option are applied when a server is loaded."
		})
		@ConfigEntry.Gui.Tooltip
		public String configReloadCommand = "rpconfigreload";

		@TOMLConfigSerializer.Comment({
				"Fixes the \"TickNextTick list out of synch\" IllegalStateException.",
				"For more information, see: https://github.com/SleepyTrousers/EnderCore/issues/105"
		})
		@ConfigEntry.Gui.Tooltip
		public boolean fixTickSchedulerDesync = true;

		@TOMLConfigSerializer.Comment({
				"A list of mixins that should not be applied. Available mixins:",
				"- Minecraft: Required for changing Minecraft window options.",
				"- ReadTimeoutHandler: Required for changing the read timeout.",
				"- ServerLoginNetHandler: Required for changing the login timeout.",
				"- ServerPlayNetHandlerKeepAlive: Required for changing KeepAlive packet settings.",
				"- ServerPlayNetHandlerPlayerSpeedLimits: Required for changing player speed " +
						"limits.",
				"- ServerTickList: Required for fixing tick scheduler desync.",
				"Changes to this option are applied after a game restart."
		})
		@ConfigEntry.Gui.Tooltip
		public List<String> mixinBlacklist = new ArrayList<>();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void validatePostLoad() throws ValidationException {
			configReloadCommand = configReloadCommand.trim();
			//Remove invalid values and sort alphabetically.
			mixinBlacklist = mixinBlacklist.stream().
					filter(mixins::containsValue).
					sorted().collect(Collectors.toList());
		}

		/**
		 * Returns whether the specified RandomPatches mixin class is enabled.
		 *
		 * @param mixinClassName a RandomPatches mixin class name.
		 * @return {@code true} if the specified RandomPatches mixin class is enabled,
		 * or otherwise {@code false}.
		 */
		public boolean isMixinClassEnabled(String mixinClassName) {
			return !mixinBlacklist.contains(mixins.get(mixinClassName));
		}
	}

	@TOMLConfigSerializer.Comment("Client-sided options.")
	@ConfigEntry.Category("client")
	@ConfigEntry.Gui.TransitiveObject
	public Client client = new Client();

	@TOMLConfigSerializer.Comment("Options related to connection timeouts.")
	@ConfigEntry.Category("connection_timeouts")
	@ConfigEntry.Gui.TransitiveObject
	public ConnectionTimeouts connectionTimeouts = new ConnectionTimeouts();

	@TOMLConfigSerializer.Comment("Options related to player speed limits.")
	@ConfigEntry.Category("player_speed_limits")
	@ConfigEntry.Gui.TransitiveObject
	public PlayerSpeedLimits playerSpeedLimits = new PlayerSpeedLimits();

	@TOMLConfigSerializer.Comment("Miscellaneous options.")
	@ConfigEntry.Category("misc")
	@ConfigEntry.Gui.TransitiveObject
	public Misc misc = new Misc();
}
