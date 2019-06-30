package com.therandomlabs.randompatches.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import com.therandomlabs.randompatches.RPConfig;
import com.therandomlabs.randompatches.RandomPatches;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class WindowIconHandler {
	private static boolean setBefore;

	public static void setWindowIcon(long handle) {
		RPConfig.Window.onReload(false);

		final boolean osX = Util.getOSType() == Util.EnumOS.OSX;

		InputStream stream16 = null;
		InputStream stream32 = null;
		InputStream stream256 = null;

		try(MemoryStack memoryStack = MemoryStack.stackPush()) {
			final Minecraft mc = Minecraft.getInstance();

			if(RPConfig.Window.icon16String.isEmpty()) {
				if(osX && !setBefore) {
					return;
				}

				final VanillaPack vanillaPack = mc.getPackFinder().getVanillaPack();

				stream16 = vanillaPack.getResourceStream(
						ResourcePackType.CLIENT_RESOURCES,
						new ResourceLocation("icons/icon_16x16.png")
				);

				stream32 = vanillaPack.getResourceStream(
						ResourcePackType.CLIENT_RESOURCES,
						new ResourceLocation("icons/icon_32x32.png")
				);

				if(osX) {
					stream256 = vanillaPack.getResourceStream(
							ResourcePackType.CLIENT_RESOURCES,
							new ResourceLocation("icons/icon_256x256.png")
					);
				}
			} else {
				stream16 = new FileInputStream(RPConfig.Window.icon16String);
				stream32 = new FileInputStream(RPConfig.Window.icon32String);

				if(osX) {
					stream256 = new FileInputStream(RPConfig.Window.icon256String);
				}
			}

			if(stream16 != null) {
				final IntBuffer x = memoryStack.mallocInt(1);
				final IntBuffer y = memoryStack.mallocInt(1);
				final IntBuffer channels = memoryStack.mallocInt(1);

				final GLFWImage.Buffer imageBuffer = GLFWImage.mallocStack(2, memoryStack);

				final ByteBuffer image16Bytes = readImageToBuffer(stream16, x, y, channels);

				if(image16Bytes == null) {
					throw new IllegalStateException(
							"Could not load icon: " + STBImage.stbi_failure_reason()
					);
				}

				imageBuffer.position(0);
				imageBuffer.width(x.get(0));
				imageBuffer.height(y.get(0));
				imageBuffer.pixels(image16Bytes);

				final ByteBuffer image32Bytes = readImageToBuffer(stream32, x, y, channels);

				if(image32Bytes == null) {
					throw new IllegalStateException(
							"Could not load icon: " + STBImage.stbi_failure_reason()
					);
				}

				imageBuffer.position(1);
				imageBuffer.width(x.get(0));
				imageBuffer.height(y.get(0));
				imageBuffer.pixels(image32Bytes);

				ByteBuffer image256Bytes = null;

				if(osX) {
					image256Bytes = readImageToBuffer(stream256, x, y, channels);

					if(image256Bytes == null) {
						throw new IllegalStateException(
								"Could not load icon: " + STBImage.stbi_failure_reason()
						);
					}

					imageBuffer.position(2);
					imageBuffer.width(x.get(0));
					imageBuffer.height(y.get(0));
					imageBuffer.pixels(image256Bytes);
					imageBuffer.position(0);
				}

				imageBuffer.position(0);
				GLFW.glfwSetWindowIcon(handle, imageBuffer);

				STBImage.stbi_image_free(image16Bytes);
				STBImage.stbi_image_free(image32Bytes);

				if(osX) {
					STBImage.stbi_image_free(image256Bytes);
				}
			}
		} catch(IOException ex) {
			if(RandomPatches.IS_DEOBFUSCATED &&
					ex instanceof FileNotFoundException &&
					RPConfig.Window.DEFAULT_ICON.equals(RPConfig.Window.icon16) &&
					RPConfig.Window.DEFAULT_ICON.equals(RPConfig.Window.icon32)) {
				return;
			}

			RandomPatches.LOGGER.error("Failed to set icon", ex);
		} finally {
			IOUtils.closeQuietly(stream16);
			IOUtils.closeQuietly(stream32);
			IOUtils.closeQuietly(stream256);
		}
	}

	private static ByteBuffer readImageToBuffer(
			InputStream stream, IntBuffer x, IntBuffer y, IntBuffer channels
	) throws IOException {
		ByteBuffer buffer1 = null;
		final ByteBuffer buffer2;

		try {
			buffer1 = TextureUtil.readToNativeBuffer(stream);
			buffer1.rewind();
			buffer2 = STBImage.stbi_load_from_memory(buffer1, x, y, channels, 0);
		} finally {
			if(buffer1 != null) {
				MemoryUtil.memFree(buffer1);
			}
		}

		return buffer2;
	}
}
