/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 TheRandomLabs
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

package com.therandomlabs.randompatches.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;

import com.therandomlabs.randompatches.RPConfig;
import com.therandomlabs.randompatches.RandomPatches;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * Contains Minecraft window-related code for RandomPatches.
 */
public final class RPWindowHandler {
	private static final class TitleLookup extends StrLookup<String> {
		private static final TitleLookup INSTANCE = new TitleLookup();

		@Override
		public String lookup(String key) {
			if (key.equals("mcversion")) {
				return SharedConstants.getGameVersion().getName();
			}

			if (key.equals("activity")) {
				return activity;
			}

			if (key.equals("username")) {
				return MinecraftClient.getInstance().getSession().getUsername();
			}

			if (key.equals("modsloaded")) {
				return Integer.toString(FabricLoader.getInstance().getAllMods().size());
			}

			if (key.startsWith("modversion:")) {
				final String modID = key.substring("modversion:".length());
				final Optional<ModContainer> container =
						FabricLoader.getInstance().getModContainer(modID);

				return container.map(
						modContainer -> modContainer.getMetadata().getVersion().getFriendlyString()
				).orElse(null);
			}

			return null;
		}
	}

	private static final StrSubstitutor titleSubstitutor = new StrSubstitutor(TitleLookup.INSTANCE);

	private static String activity;
	private static boolean enabled;

	private RPWindowHandler() {}

	/**
	 * Enables this class's functionality if it has not already been enabled.
	 */
	public static void enable() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			enabled = true;
		}
	}

	/**
	 * Called by {@link RPConfig.Window} when the RandomPatches configuration is reloaded.
	 */
	public static void onConfigReload() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && enabled) {
			MinecraftClient.getInstance().execute(RPWindowHandler::applySettings);
		}
	}

	/**
	 * Returns the Minecraft window title according to the relevant RandomPatches configuration
	 * options.
	 *
	 * @return the Minecraft window title according to the relevant RandomPatches configuration
	 * options.
	 */
	public static String getWindowTitle() {
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT || !enabled) {
			return RPConfig.Window.DEFAULT_TITLE;
		}

		final RPConfig.Window config = RandomPatches.config().client.window;

		final MinecraftClient mc = MinecraftClient.getInstance();
		final ClientPlayNetworkHandler handler = mc.getNetworkHandler();

		if (handler == null || !handler.getConnection().isOpen()) {
			activity = null;
			return titleSubstitutor.replace(config.simpleTitle);
		}

		final String activityKey;

		if (mc.getServer() != null && !mc.getServer().isRemote()) {
			activityKey = "title.singleplayer";
		} else if (mc.isConnectedToRealms()) {
			activityKey = "title.multiplayer.realms";
		} else if (mc.getServer() == null &&
				(mc.getCurrentServerEntry() == null || !mc.getCurrentServerEntry().isLocal())) {
			activityKey = "title.multiplayer.other";
		} else {
			activityKey = "title.multiplayer.lan";
		}

		activity = I18n.translate(activityKey);
		return titleSubstitutor.replace(config.titleWithActivity);
	}

	/**
	 * Applies the RandomPatches configuration options relating to the Minecraft window icon.
	 *
	 * @param vanillaIcon16 the 16x16 vanilla icon.
	 * @param vanillaIcon32 the 32x32 vanilla icon.
	 */
	public static void updateWindowIcon(
			@Nullable InputStream vanillaIcon16, @Nullable InputStream vanillaIcon32
	) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && enabled) {
			updateWindowIcon(
					vanillaIcon16, vanillaIcon32,
					MinecraftClient.getInstance().getWindow().getHandle()
			);
		}
	}

	/**
	 * Applies the RandomPatches configuration options relating to the Minecraft window icon.
	 *
	 * @param vanillaIcon16 the 16x16 vanilla icon.
	 * @param vanillaIcon32 the 32x32 vanilla icon.
	 * @param window the window handle.
	 */
	@SuppressWarnings("PMD.CloseResource")
	public static void updateWindowIcon(
			@Nullable InputStream vanillaIcon16, @Nullable InputStream vanillaIcon32, long window
	) {
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT || !enabled) {
			return;
		}

		final RPConfig.Window config = RandomPatches.config().client.window;

		final MinecraftClient mc = MinecraftClient.getInstance();

		InputStream stream16 = null;
		InputStream stream32 = null;
		InputStream stream256 = null;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			if (config.icon16.isEmpty()) {
				final DefaultResourcePack vanillaPack = mc.getResourcePackDownloader().getPack();

				stream16 = vanillaIcon16 == null ? vanillaPack.open(
						ResourceType.CLIENT_RESOURCES,
						new Identifier("icons/icon_16x16.png")
				) : vanillaIcon16;

				stream32 = vanillaIcon32 == null ? vanillaPack.open(
						ResourceType.CLIENT_RESOURCES,
						new Identifier("icons/icon_32x32.png")
				) : vanillaIcon32;

				if (MinecraftClient.IS_SYSTEM_MAC) {
					stream256 = vanillaPack.open(
							ResourceType.CLIENT_RESOURCES,
							new Identifier("icons/icon_256x256.png")
					);
				}
			} else {
				stream16 = new FileInputStream(config.icon16);
				stream32 = new FileInputStream(config.icon32);

				if (MinecraftClient.IS_SYSTEM_MAC) {
					stream256 = new FileInputStream(config.icon256);
				}
			}

			final IntBuffer x = stack.mallocInt(1);
			final IntBuffer y = stack.mallocInt(1);
			final IntBuffer channels = stack.mallocInt(1);

			final GLFWImage.Buffer imageBuffer = GLFWImage.mallocStack(2, stack);

			final ByteBuffer image16 = readImageToBuffer(stream16, x, y, channels, 16);

			if (image16 == null) {
				throw new IllegalStateException(
						"Failed to load icon: " + STBImage.stbi_failure_reason()
				);
			}

			final boolean image16Resized = x.get(0) != 16 || y.get(0) != 16;

			imageBuffer.position(0);
			imageBuffer.width(16);
			imageBuffer.height(16);
			imageBuffer.pixels(image16);

			final ByteBuffer image32 = readImageToBuffer(stream32, x, y, channels, 32);

			if (image32 == null) {
				throw new IllegalStateException(
						"Failed to load icon: " + STBImage.stbi_failure_reason()
				);
			}

			final boolean image32Resized = x.get(0) != 32 || y.get(0) != 32;

			imageBuffer.position(1);
			imageBuffer.width(32);
			imageBuffer.height(32);
			imageBuffer.pixels(image32);

			ByteBuffer image256 = null;
			final boolean image256Resized;

			if (stream256 == null) {
				image256Resized = false;
			} else {
				image256 = readImageToBuffer(stream256, x, y, channels, 256);

				if (image256 == null) {
					throw new IllegalStateException(
							"Failed to load icon: " + STBImage.stbi_failure_reason()
					);
				}

				image256Resized = x.get(0) != 256 || y.get(0) != 256;

				imageBuffer.position(2);
				imageBuffer.width(256);
				imageBuffer.height(256);
				imageBuffer.pixels(image256);
				imageBuffer.position(0);
			}

			imageBuffer.position(0);
			GLFW.glfwSetWindowIcon(window, imageBuffer);

			//If the image has to be resized, readImageToBuffer allocates a new buffer using
			//MemoryUtil.memAlloc and calls STBImage#stbi_image_free on the original buffer.
			if (image16Resized) {
				MemoryUtil.memFree(image16);
			} else {
				STBImage.stbi_image_free(image16);
			}

			if (image32Resized) {
				MemoryUtil.memFree(image32);
			} else {
				STBImage.stbi_image_free(image32);
			}

			if (MinecraftClient.IS_SYSTEM_MAC) {
				if (image256Resized) {
					MemoryUtil.memFree(image256);
				} else {
					//noinspection ConstantConditions
					STBImage.stbi_image_free(image256);
				}
			}
		} catch (IOException ex) {
			RandomPatches.logger.error("Failed to load icon", ex);
		} finally {
			IOUtils.closeQuietly(stream16);
			IOUtils.closeQuietly(stream32);

			if (MinecraftClient.IS_SYSTEM_MAC) {
				IOUtils.closeQuietly(stream256);
			}
		}
	}

	private static void applySettings() {
		MinecraftClient.getInstance().updateWindowTitle();
		updateWindowIcon(null, null);
	}

	private static ByteBuffer readImageToBuffer(
			InputStream stream, IntBuffer x, IntBuffer y, IntBuffer channels, int size
	) throws IOException {
		ByteBuffer resource = null;

		try {
			resource = TextureUtil.readAllToByteBuffer(stream);
			resource.rewind();

			final ByteBuffer image = STBImage.stbi_load_from_memory(resource, x, y, channels, 4);

			if (image == null) {
				return null;
			}

			final int width = x.get(0);
			final int height = y.get(0);

			if (width == size && height == size) {
				return image;
			}

			final ByteBuffer resized = MemoryUtil.memAlloc(size * size * 4);

			STBImageResize.stbir_resize_uint8(
					image, width, height, 0, resized, size, size, 0, 4
			);

			STBImage.stbi_image_free(image);

			return resized;
		} finally {
			if (resource != null) {
				MemoryUtil.memFree(resource);
			}
		}
	}
}
