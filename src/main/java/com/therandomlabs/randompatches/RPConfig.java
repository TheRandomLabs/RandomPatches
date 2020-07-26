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

import com.therandomlabs.utils.config.Config;

@SuppressWarnings("NullAway")
@Config(id = RandomPatches.MOD_ID, comment = "RandomPatches configuration")
public final class RPConfig {
	public static final class Misc {
		@Config.RequiresRestart
		@Config.Property("Enables the /rpreload command.")
		public static boolean rpreload = true;

		private Misc() {}
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
		@Config.Property("The login timeout in ticks.")
		public static int loginTimeout = 900;

		@Config.RangeInt(min = 1)
		@Config.Property({
				"The read timeout in seconds.",
				"This is the time it takes for a player to be disconnected after not " +
						"responding to a KeepAlive packet.",
				"This value is automatically rounded up to a product of 15."
		})
		public static int readTimeout = 90;

		public static long readTimeoutMillis;

		private Timeouts() {}

		public static void onReload() {
			if (readTimeout < keepAlivePacketInterval) {
				readTimeout = keepAlivePacketInterval;
			} else if (readTimeout % keepAlivePacketInterval != 0) {
				readTimeout = keepAlivePacketInterval * (readTimeout / keepAlivePacketInterval + 1);
			}

			readTimeoutMillis = readTimeout * 1000L;
		}
	}

	@Config.Category("Options that don't fit into any other categories.")
	public static final Misc misc = null;

	@Config.Category("Options related to the movement speed limits.")
	public static final SpeedLimits speedLimits = null;

	@Config.Category("Options related to the disconnect timeouts.")
	public static final Timeouts timeouts = null;

	private RPConfig() {}
}
