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

import me.shedaniel.autoconfig1u.ConfigData;
import me.shedaniel.autoconfig1u.annotation.Config;
import me.shedaniel.autoconfig1u.annotation.ConfigEntry;

/**
 * The RandomPatches configuration.
 */
@SuppressWarnings("CanBeFinal")
@TOMLConfigSerializer.Comment({
		"RandomPatches configuration.",
		"All configuration options not under the \"client\" category are server-sided."
})
@Config(name = RandomPatches.MOD_ID)
public final class RPConfig implements ConfigData {
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

	public static final class ConnectionTimeouts implements ConfigData {
		@TOMLConfigSerializer.Comment(
				"The interval at which KeepAlive packets are sent to clients."
		)
		@ConfigEntry.Gui.Tooltip
		public int keepAlivePacketIntervalSeconds = 15;

		@TOMLConfigSerializer.Comment("The read timeout.")
		@ConfigEntry.Gui.Tooltip
		public int readTimeoutSeconds = 120;

		@TOMLConfigSerializer.Comment("The login timeout.")
		@ConfigEntry.Gui.Tooltip
		public int loginTimeoutTicks = 2400;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void validatePostLoad() {
			if (keepAlivePacketIntervalSeconds <= 0) {
				keepAlivePacketIntervalSeconds = 15;
			}

			if (readTimeoutSeconds <= 0L) {
				readTimeoutSeconds = 90;
			}

			if (readTimeoutSeconds < keepAlivePacketIntervalSeconds) {
				readTimeoutSeconds = keepAlivePacketIntervalSeconds;
			} else if (readTimeoutSeconds % keepAlivePacketIntervalSeconds != 0) {
				final int multiple = readTimeoutSeconds / keepAlivePacketIntervalSeconds + 1;
				readTimeoutSeconds = keepAlivePacketIntervalSeconds * multiple;
			}
		}
	}

	public static final class Misc implements ConfigData {
		@TOMLConfigSerializer.Comment({
				"The name of the command that reloads this configuration from disk.",
				"Set this to an empty string to disable the command."
		})
		@ConfigEntry.Gui.Tooltip
		public String configReloadCommand = "rpconfigreload";

		@TOMLConfigSerializer.Comment({
				"Fixes the \"TickNextTick list out of synch\" IllegalStateException.",
				"For more information, see: https://github.com/SleepyTrousers/EnderCore/issues/105"
		})
		@ConfigEntry.Gui.Tooltip
		public boolean fixTickSchedulerDesync = true;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void validatePostLoad() throws ValidationException {
			configReloadCommand = configReloadCommand.trim();
		}
	}

	@TOMLConfigSerializer.Comment("Options related to player speed limits.")
	@ConfigEntry.Category("player_speed_limits")
	@ConfigEntry.Gui.TransitiveObject
	public PlayerSpeedLimits playerSpeedLimits = new PlayerSpeedLimits();

	@TOMLConfigSerializer.Comment("Options related to connection timeouts.")
	@ConfigEntry.Category("connection_timeouts")
	@ConfigEntry.Gui.TransitiveObject
	public ConnectionTimeouts connectionTimeouts = new ConnectionTimeouts();

	@TOMLConfigSerializer.Comment("Miscellaneous options.")
	@ConfigEntry.Category("misc")
	@ConfigEntry.Gui.TransitiveObject
	public Misc misc = new Misc();
}
