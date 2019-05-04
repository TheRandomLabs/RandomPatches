package com.therandomlabs.randompatches.patch.client;

import com.therandomlabs.randompatches.Patch;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.ScreenChatOptions;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class KeyboardListenerPatch extends Patch {
	public static final class ToggleNarratorKeybind {
		public static KeyBinding keybind;

		public static void register() {
			keybind = new KeyBinding("key.narrator", new IKeyConflictContext() {
				@Override
				public boolean isActive() {
					return !(Minecraft.getInstance().currentScreen instanceof GuiControls);
				}

				@Override
				public boolean conflicts(IKeyConflictContext other) {
					return true;
				}
			}, KeyModifier.CONTROL, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_B,
					"key.categories.misc");

			ClientRegistry.registerKeyBinding(keybind);
		}
	}

	public static final int KEY_UNUSED = 0x54;

	@Override
	public boolean apply(ClassNode node) {
		final MethodNode method = findMethod(node, "onKeyEvent", "func_197961_a");

		IntInsnNode isB = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() == Opcodes.BIPUSH) {
				isB = (IntInsnNode) instruction;

				if(isB.operand == GLFW.GLFW_KEY_B) {
					break;
				}

				isB = null;
			}
		}

		final MethodInsnNode callHandleKeypress = new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				getName(KeyboardListenerPatch.class),
				"handleKeypress",
				"()V",
				false
		);

		method.instructions.insertBefore(isB.getPrevious(), callHandleKeypress);
		isB.operand = KEY_UNUSED;

		return true;
	}

	public static void handleKeypress(int key) {
		if(ToggleNarratorKeybind.keybind == null) {
			return;
		}

		if(!ToggleNarratorKeybind.keybind.isActiveAndMatches(
				InputMappings.Type.KEYSYM.getOrMakeInput(key)
		)) {
			return;
		}

		final Minecraft mc = Minecraft.getInstance();

		mc.gameSettings.setOptionValue(GameSettings.Options.NARRATOR, 1);

		if(mc.currentScreen instanceof ScreenChatOptions) {
			((ScreenChatOptions) mc.currentScreen).updateNarratorButton();
		}
	}
}
