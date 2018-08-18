package com.therandomlabs.randompatches.core.transformer;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import com.therandomlabs.randompatches.RPStaticConfig;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.core.Transformer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.ScreenChatOptions;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class MinecraftTransformer extends Transformer {
	public static final String SET_WINDOW_ICON = getName("setWindowIcon", "func_175594_ao");
	public static final int KEY_UNUSED = 0x54;

	public static KeyBinding toggleNarrator;

	@Override
	public void transform(ClassNode node) {
		if(!RPStaticConfig.icon16.isEmpty()) {
			transformInit(findMethod(node, "init", "func_71384_a"));
		}

		if(!RandomPatches.DEFAULT_WINDOW_TITLE.equals(RPStaticConfig.title)) {
			transformCreateDisplay(findMethod(node, "createDisplay", "func_175609_am"));
		}

		if(RPStaticConfig.narratorKeybind && RandomPatches.IS_ONE_TWELVE &&
				!RandomPatches.REBIND_NARRATOR_INSTALLED) {
			transformDispatchKeypresses(findMethod(node, "dispatchKeypresses", "func_152348_aa"));
		}
	}

	private static void transformInit(MethodNode method) {
		MethodInsnNode setWindowIcon = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				setWindowIcon = (MethodInsnNode) instruction;

				if(SET_WINDOW_ICON.equals(setWindowIcon.name)) {
					break;
				}

				setWindowIcon = null;
			}
		}

		setWindowIcon.setOpcode(Opcodes.INVOKESTATIC);
		setWindowIcon.owner =
				"com/therandomlabs/randompatches/core/transformer/MinecraftTransformer";
		setWindowIcon.name = "setWindowIcon";

		method.instructions.remove(setWindowIcon.getPrevious());
	}

	public static void setWindowIcon() {
		final Minecraft mc = Minecraft.getMinecraft();
		final Util.EnumOS os = Util.getOSType();

		if(os != Util.EnumOS.OSX) {
			InputStream stream16 = null;
			InputStream stream32 = null;

			try {
				if(RPStaticConfig.icon16.isEmpty()) {
					stream16 = mc.defaultResourcePack.getInputStreamAssets(
							new ResourceLocation("icons/icon_16x16.png")
					);

					stream32 = mc.defaultResourcePack.getInputStreamAssets(
							new ResourceLocation("icons/icon_32x32.png")
					);
				} else {
					stream16 = new FileInputStream(RPStaticConfig.icon16);
					stream32 = new FileInputStream(RPStaticConfig.icon32);
				}

				if(stream16 != null && stream32 != null) {
					Display.setIcon(new ByteBuffer[] {
							readImageToBuffer(stream16, 16),
							readImageToBuffer(stream32, 32)
					});
				}
			} catch(IOException ex) {
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

			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			graphics.drawImage(image, 0, 0, dimensions, dimensions, null);

			graphics.dispose();

			image = resized;
		}

		final int[] aint =
				image.getRGB(0, 0, dimensions, dimensions, null, 0, dimensions);
		final ByteBuffer buffer = ByteBuffer.allocate(aint.length * 4);

		for(int i : aint) {
			buffer.putInt(i << 8 | i >> 24 & 255);
		}

		buffer.flip();
		return buffer;
	}

	private static void transformCreateDisplay(MethodNode method) {
		LdcInsnNode ldc = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() == Opcodes.INVOKESTATIC) {
				final MethodInsnNode setTitle = (MethodInsnNode) instruction;

				if("setTitle".equals(setTitle.name)) {
					ldc = (LdcInsnNode) setTitle.getPrevious();
				}
			}
		}

		ldc.cst = RPStaticConfig.title;
	}

	private static void transformDispatchKeypresses(MethodNode method) {
		IntInsnNode isB = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() == Opcodes.BIPUSH) {
				isB = (IntInsnNode) instruction;

				if(isB.operand == Keyboard.KEY_B) {
					break;
				}

				isB = null;
			}
		}

		final MethodInsnNode callHandleKeypress = new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				"com/therandomlabs/randompatches/core/transformer/MinecraftTransformer",
				"handleKeypress",
				"()V",
				false
		);

		method.instructions.insertBefore(isB.getPrevious(), callHandleKeypress);
		isB.operand = KEY_UNUSED;
	}

	public static void handleKeypress() {
		final int eventKey = Keyboard.getEventKey();
		final int key = eventKey == 0 ? Keyboard.getEventCharacter() + 256 : eventKey;

		if(!toggleNarrator.isActiveAndMatches(key)) {
			return;
		}

		final Minecraft mc = Minecraft.getMinecraft();

		mc.gameSettings.setOptionValue(GameSettings.Options.NARRATOR, 1);

		if(mc.currentScreen instanceof ScreenChatOptions) {
			((ScreenChatOptions) mc.currentScreen).updateNarratorButton();
		}
	}

	public static void registerKeybind() {
		toggleNarrator = new KeyBinding("key.narrator", new IKeyConflictContext() {
			@Override
			public boolean isActive() {
				return !(Minecraft.getMinecraft().currentScreen instanceof GuiControls);
			}

			@Override
			public boolean conflicts(IKeyConflictContext other) {
				return true;
			}
		}, KeyModifier.CONTROL, Keyboard.KEY_B, "key.categories.misc");

		ClientRegistry.registerKeyBinding(toggleNarrator);
	}
}
