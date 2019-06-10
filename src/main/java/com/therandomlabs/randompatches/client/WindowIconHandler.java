package com.therandomlabs.randompatches.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import com.mojang.blaze3d.platform.TextureUtil;
import com.therandomlabs.randompatches.RPConfig;
import com.therandomlabs.randompatches.RandomPatches;
import net.minecraft.client.Minecraft;
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
	public static void setWindowIcon() {
		setWindowIcon(Minecraft.getInstance().mainWindow.getHandle());
	}

	public static void setWindowIcon(long handle) {
		RPConfig.Window.onReload(false);

		final Util.OS os = Util.getOSType();

		if(os != Util.OS.OSX) {
			InputStream stream16 = null;
			InputStream stream32 = null;

			try(MemoryStack memoryStack = MemoryStack.stackPush()) {
				final Minecraft mc = Minecraft.getInstance();

				if(RPConfig.Window.icon16String.isEmpty()) {
					final VanillaPack vanillaPack = mc.getPackFinder().getVanillaPack();

					stream16 = vanillaPack.getResourceStream(
							ResourcePackType.CLIENT_RESOURCES,
							new ResourceLocation("icons/icon_16x16.png")
					);

					stream32 = vanillaPack.getResourceStream(
							ResourcePackType.CLIENT_RESOURCES,
							new ResourceLocation("icons/icon_32x32.png")
					);
				} else {
					stream16 = new FileInputStream(RPConfig.Window.icon16String);
					stream32 = new FileInputStream(RPConfig.Window.icon32String);
				}

				if(stream16 != null && stream32 != null) {
					final IntBuffer i = memoryStack.mallocInt(1);
					final IntBuffer j = memoryStack.mallocInt(1);
					final IntBuffer k = memoryStack.mallocInt(1);

					final GLFWImage.Buffer iamgeBuffer = GLFWImage.mallocStack(2, memoryStack);

					final ByteBuffer buffer1 = readImageToBuffer(stream16, i, j, k);

					if(buffer1 == null) {
						throw new IllegalStateException(
								"Could not load icon: " + STBImage.stbi_failure_reason()
						);
					}

					iamgeBuffer.position(0);
					iamgeBuffer.width(i.get(0));
					iamgeBuffer.height(j.get(0));
					iamgeBuffer.pixels(buffer1);

					final ByteBuffer buffer2 = readImageToBuffer(stream32, i, j, k);

					if(buffer2 == null) {
						throw new IllegalStateException(
								"Could not load icon: " + STBImage.stbi_failure_reason()
						);
					}

					iamgeBuffer.position(1);
					iamgeBuffer.width(i.get(0));
					iamgeBuffer.height(j.get(0));
					iamgeBuffer.pixels(buffer2);
					iamgeBuffer.position(0);

					GLFW.glfwSetWindowIcon(handle, iamgeBuffer);

					STBImage.stbi_image_free(buffer1);
					STBImage.stbi_image_free(buffer2);
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
			}
		}
	}

	private static ByteBuffer readImageToBuffer(InputStream stream, IntBuffer i, IntBuffer j,
			IntBuffer k) throws IOException {
		ByteBuffer buffer1 = null;
		final ByteBuffer buffer2;

		try {
			buffer1 = TextureUtil.readResource(stream);
			buffer1.rewind();
			buffer2 = STBImage.stbi_load_from_memory(buffer1, i, j, k, 0);
		} finally {
			if(buffer1 != null) {
				MemoryUtil.memFree(buffer1);
			}
		}

		return buffer2;
	}
}
