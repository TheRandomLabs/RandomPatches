package com.therandomlabs.randompatches.client;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.config.RPConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.Display;

public class WindowIconHandler {
	public static void setWindowIcon() {
		final Util.EnumOS os = Util.getOSType();

		if(os != Util.EnumOS.OSX) {
			InputStream stream16 = null;
			InputStream stream32 = null;

			try {
				if(RPConfig.Window.icon16String.isEmpty()) {
					final Minecraft mc = Minecraft.getMinecraft();

					stream16 = mc.defaultResourcePack.getInputStreamAssets(
							new ResourceLocation("icons/icon_16x16.png")
					);

					stream32 = mc.defaultResourcePack.getInputStreamAssets(
							new ResourceLocation("icons/icon_32x32.png")
					);
				} else {
					stream16 = new FileInputStream(RPConfig.Window.icon16String);
					stream32 = new FileInputStream(RPConfig.Window.icon32String);
				}

				if(stream16 != null && stream32 != null) {
					Display.setIcon(new ByteBuffer[] {
							readImageToBuffer(stream16, 16),
							readImageToBuffer(stream32, 32)
					});
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

	private static ByteBuffer readImageToBuffer(InputStream stream, int dimensions)
			throws IOException {
		BufferedImage image = ImageIO.read(stream);

		if(image.getWidth() != dimensions || image.getHeight() != dimensions) {
			final GraphicsEnvironment environment =
					GraphicsEnvironment.getLocalGraphicsEnvironment();

			final GraphicsDevice device = environment.getDefaultScreenDevice();

			final GraphicsConfiguration gc = device.getDefaultConfiguration();

			final BufferedImage resized = gc.createCompatibleImage(
					dimensions,
					dimensions,
					image.getTransparency()
			);

			final Graphics2D graphics = resized.createGraphics();

			graphics.setRenderingHint(
					RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR
			);

			graphics.setRenderingHint(
					RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY
			);

			graphics.setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON
			);

			graphics.drawImage(image, 0, 0, dimensions, dimensions, null);

			graphics.dispose();

			image = resized;
		}

		final int[] aint = image.getRGB(0, 0, dimensions, dimensions, null, 0, dimensions);
		final ByteBuffer buffer = ByteBuffer.allocate(aint.length * 4);

		for(int i : aint) {
			buffer.putInt(i << 8 | i >> 24 & 255);
		}

		buffer.flip();
		return buffer;
	}
}
