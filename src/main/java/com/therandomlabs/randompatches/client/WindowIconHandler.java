package com.therandomlabs.randompatches.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.therandomlabs.randompatches.RPConfig;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.utils.fabric.FabricUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class WindowIconHandler {
	private static boolean setBefore;

	public static void setWindowIcon() {
		//This gets called before the RandomPatches constructor is called
		setWindowIcon(MinecraftClient.getInstance().getWindow().getHandle());
	}

	public static void setWindowIcon(long handle) {
		RPConfig.Window.onReload(false);

		final boolean osX = Util.getOperatingSystem() == Util.OperatingSystem.OSX;

		InputStream stream16 = null;
		InputStream stream32 = null;
		InputStream stream256 = null;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			final MinecraftClient mc = MinecraftClient.getInstance();

			if (RPConfig.Window.icon16String.isEmpty()) {
				if (osX && !setBefore) {
					return;
				}

				final DefaultResourcePack vanillaPack = mc.getResourcePackDownloader().getPack();

				stream16 = vanillaPack.open(
						ResourceType.CLIENT_RESOURCES,
						new Identifier("icons/icon_16x16.png")
				);

				stream32 = vanillaPack.open(
						ResourceType.CLIENT_RESOURCES,
						new Identifier("icons/icon_32x32.png")
				);

				if (osX) {
					stream256 = vanillaPack.open(
							ResourceType.CLIENT_RESOURCES,
							new Identifier("icons/icon_256x256.png")
					);
				}
			} else {
				stream16 = new FileInputStream(RPConfig.Window.icon16String);
				stream32 = new FileInputStream(RPConfig.Window.icon32String);

				if (osX) {
					stream256 = new FileInputStream(RPConfig.Window.icon256String);
				}
			}

			final IntBuffer x = stack.mallocInt(1);
			final IntBuffer y = stack.mallocInt(1);
			final IntBuffer channels = stack.mallocInt(1);

			final GLFWImage.Buffer imageBuffer = GLFWImage.mallocStack(2, stack);

			final ByteBuffer image16 = readImageToBuffer(stream16, x, y, channels, 16);

			if (image16 == null) {
				throw new IllegalStateException(
						"Could not load icon: " + STBImage.stbi_failure_reason()
				);
			}

			boolean image16Resized = x.get(0) != 16 || y.get(0) != 16;

			imageBuffer.position(0);
			imageBuffer.width(16);
			imageBuffer.height(16);
			imageBuffer.pixels(image16);

			final ByteBuffer image32 = readImageToBuffer(stream32, x, y, channels, 32);

			if (image32 == null) {
				throw new IllegalStateException(
						"Could not load icon: " + STBImage.stbi_failure_reason()
				);
			}

			boolean image32Resized = x.get(0) != 32 || y.get(0) != 32;

			imageBuffer.position(1);
			imageBuffer.width(32);
			imageBuffer.height(32);
			imageBuffer.pixels(image32);

			ByteBuffer image256 = null;
			boolean image256Resized = false;

			if (stream256 != null) {
				image256 = readImageToBuffer(stream256, x, y, channels, 256);

				if (image256 == null) {
					throw new IllegalStateException(
							"Could not load icon: " + STBImage.stbi_failure_reason()
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
			GLFW.glfwSetWindowIcon(handle, imageBuffer);

			//If it was resized, then a buffer would have been allocated with MemoryUtil.memAlloc
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

			if (osX) {
				if (image256Resized) {
					MemoryUtil.memFree(image256);
				} else {
					STBImage.stbi_image_free(image256);
				}
			}
		} catch (IOException ex) {
			if (FabricUtils.IS_DEVELOPMENT_ENVIRONMENT &&
					ex instanceof FileNotFoundException &&
					RPConfig.Window.DEFAULT_ICON.equals(RPConfig.Window.icon16) &&
					RPConfig.Window.DEFAULT_ICON.equals(RPConfig.Window.icon32)) {
				return;
			}

			RandomPatches.logger.error("Failed to set icon", ex);
		} finally {
			IOUtils.closeQuietly(stream16);
			IOUtils.closeQuietly(stream32);

			if (osX) {
				IOUtils.closeQuietly(stream256);
			}
		}
	}

	private static ByteBuffer readImageToBuffer(
			InputStream stream, IntBuffer x, IntBuffer y, IntBuffer channels, int size
	) throws IOException {
		ByteBuffer resource = null;

		try {
			resource = TextureUtil.readAllToByteBuffer(stream);
			resource.rewind();

			final ByteBuffer image = STBImage.stbi_load_from_memory(resource, x, y, channels, 4);

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
