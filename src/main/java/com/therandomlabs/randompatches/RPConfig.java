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

import java.nio.file.Path;
import java.nio.file.Paths;

import com.therandomlabs.randompatches.client.WindowIconHandler;
import com.therandomlabs.utils.config.Config;
import com.therandomlabs.utils.fabric.FabricUtils;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("NullAway")
@Config(id = RandomPatches.MOD_ID, comment = "RandomPatches configuration")
public final class RPConfig {
	public static final class Client {
		@Config.Category("Options related to the Minecraft window.")
		public static final Window window = null;
	}

	public static final class Misc {
		@Config.RequiresRestart
		@Config.Property(
				"Whether to patch WorldServer to prevent a \"TickNextTick list out of synch\" " +
						"IllegalStateException."
		)
		public static boolean fixTickNextTickListOutOfSynch = true;

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

		private SpeedLimits() {}
	}

	public static final class Timeouts {
		@Config.RangeInt(min = 1)
		@Config.Property("The interval at which the server sends the KeepAlive packet.")
		public static int keepAlivePacketInterval = 15;

		@Config.RangeInt(min = 1)
		@Config.Property("The login timeout in ticks.")
		public static int loginTimeout = 1800;

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

	public static final class Window {
		public static final Path DEFAULT_ICON = Paths.get(
				FabricUtils.IS_DEVELOPMENT_ENVIRONMENT ? "../src/main/resources/logo.png" : ""
		);

		@Config.Property({
				"The path to the 16x16 Minecraft window icon.",
				"Leave this and the 32x32 icon blank to use the default icon."
		})
		public static Path icon16 = DEFAULT_ICON;

		@Config.Property({
				"The path to the 32x32 Minecraft window icon.",
				"Leave this and the 16x16 icon blank to use the default icon."
		})
		public static Path icon32 = DEFAULT_ICON;

		@Config.Property({
				"The path to the 256x256 window icon which is used on Mac OS X.",
				"Leave this, the 16x16 icon and the 32x32 icon blank to use the default icon."
		})
		public static Path icon256 = DEFAULT_ICON;

		@Config.Property({
				"The Minecraft window title.",
				"The Minecraft version is provided as an argument."
		})
		public static String title =
				FabricUtils.IS_DEVELOPMENT_ENVIRONMENT ? "RandomPatches" : "Minecraft* %s";

		@Config.Property({
				"The Minecraft window title.",
				"The Minecraft version and current activity are provided as arguments.",
				"For example: \"RandomPatches - %2$s\""
		})
		public static String titleWithActivity = FabricUtils.IS_DEVELOPMENT_ENVIRONMENT ?
				"RandomPatches - %2$s" : "Minecraft* %s - %s";

		public static String icon16String;
		public static String icon32String;
		public static String icon256String;

		public static boolean setWindowSettings = true;

		public static void onReload() {
			onReload(true);
		}

		public static void onReload(boolean applySettings) {
			icon16String = icon16.toString();
			icon32String = icon32.toString();
			icon256String = icon256.toString();

			if (icon16String.isEmpty()) {
				if (!icon256String.isEmpty()) {
					icon16 = icon256;
					icon16String = icon256String;
				} else if (!icon32String.isEmpty()) {
					icon16 = icon32;
					icon16String = icon32String;
				}
			}

			if (icon32String.isEmpty()) {
				if (!icon256String.isEmpty()) {
					icon32 = icon256;
					icon32String = icon256String;
				} else if (!icon16String.isEmpty()) {
					icon32 = icon16;
					icon32String = icon16String;
				}
			}

			if (icon256String.isEmpty()) {
				if (!icon32String.isEmpty()) {
					icon256 = icon32;
					icon256String = icon32String;
				} else if (!icon16String.isEmpty()) {
					icon256 = icon16;
					icon256String = icon16String;
				}
			}

			if (FabricUtils.IS_CLIENT && setWindowSettings && applySettings) {
				MinecraftClient.getInstance().execute(RPConfig.Window::setWindowSettings);
			}
		}

		private static void setWindowSettings() {
			final net.minecraft.client.util.Window mainWindow =
					MinecraftClient.getInstance().getWindow();

			if (mainWindow == null) {
				return;
			}

			final long handle = mainWindow.getHandle();

			if (!icon16String.isEmpty()) {
				WindowIconHandler.setWindowIcon(handle);
			}

			GLFW.glfwSetWindowTitle(handle, title);
		}
	}

	@Config.Category("Options related to client-sided features.")
	public static final Client client = null;

	@Config.Category("Options that don't fit into any other categories.")
	public static final Misc misc = null;

	@Config.Category("Options related to the movement speed limits.")
	public static final SpeedLimits speedLimits = null;

	@Config.Category("Options related to the disconnect timeouts.")
	public static final Timeouts timeouts = null;

	private RPConfig() {}
}
