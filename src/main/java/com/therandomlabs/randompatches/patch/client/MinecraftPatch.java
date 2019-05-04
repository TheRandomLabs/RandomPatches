package com.therandomlabs.randompatches.patch.client;

import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.client.WindowIconHandler;
import com.therandomlabs.randompatches.config.RPConfig;
import com.therandomlabs.randompatches.core.Patch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.ScreenChatOptions;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class MinecraftPatch extends Patch {
	public static final class ToggleNarratorKeybind {
		public static KeyBinding keybind;

		private ToggleNarratorKeybind() {}

		public static void register() {
			keybind = new KeyBinding("key.narrator", new IKeyConflictContext() {
				@Override
				public boolean isActive() {
					return !(Minecraft.getMinecraft().currentScreen instanceof GuiControls);
				}

				@Override
				public boolean conflicts(IKeyConflictContext other) {
					return true;
				}
			}, KeyModifier.CONTROL, Keyboard.KEY_B, "key.categories.misc");

			ClientRegistry.registerKeyBinding(keybind);
		}
	}

	public static final int KEY_UNUSED = 0x54;

	@Override
	public boolean apply(ClassNode node) {
		if(!RandomPatches.ITLT_INSTALLED) {
			if(!RandomPatches.DEFAULT_WINDOW_TITLE.equals(RPConfig.Window.title)) {
				patchCreateDisplay(findMethod(node, "createDisplay", "func_175609_am"));
			}

			if(!RPConfig.Window.icon16.isEmpty()) {
				patchSetWindowIcon(findMethod(node, "setWindowIcon", "func_175594_ao"));
			}
		}

		if(RPConfig.Client.isNarratorKeybindEnabled()) {
			patchDispatchKeypresses(findMethod(node, "dispatchKeypresses", "func_152348_aa"));
		}

		return true;
	}

	public static void handleKeypress() {
		if(ToggleNarratorKeybind.keybind == null) {
			return;
		}

		final int eventKey = Keyboard.getEventKey();
		final int key = eventKey == 0 ? Keyboard.getEventCharacter() + 256 : eventKey;

		if(!ToggleNarratorKeybind.keybind.isActiveAndMatches(key)) {
			return;
		}

		final Minecraft mc = Minecraft.getMinecraft();

		mc.gameSettings.setOptionValue(GameSettings.Options.NARRATOR, 1);

		if(mc.currentScreen instanceof ScreenChatOptions) {
			((ScreenChatOptions) mc.currentScreen).updateNarratorButton();
		}
	}

	private static void patchCreateDisplay(MethodNode method) {
		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() == Opcodes.INVOKESTATIC) {
				final MethodInsnNode setTitle = (MethodInsnNode) instruction;

				if("setTitle".equals(setTitle.name)) {
					((LdcInsnNode) setTitle.getPrevious()).cst = RPConfig.Window.title;
					return;
				}
			}
		}
	}

	private static void patchSetWindowIcon(MethodNode method) {
		final MethodInsnNode setWindowIcon = new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				getName(WindowIconHandler.class),
				"setWindowIcon",
				"()V",
				false
		);

		method.instructions.insertBefore(method.instructions.getFirst(), setWindowIcon);
		method.instructions.insert(setWindowIcon, new InsnNode(Opcodes.RETURN));
	}

	private static void patchDispatchKeypresses(MethodNode method) {
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
				getName(MinecraftPatch.class),
				"handleKeypress",
				"()V",
				false
		);

		method.instructions.insertBefore(isB.getPrevious(), callHandleKeypress);
		isB.operand = KEY_UNUSED;
	}
}
