package com.therandomlabs.randompatches.patch.client;

import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.client.WindowIconHandler;
import com.therandomlabs.randompatches.config.RPConfig;
import com.therandomlabs.randompatches.core.Patch;
import org.lwjgl.input.Keyboard;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

public final class MinecraftPatch extends Patch {
	public static final int KEY_UNUSED = 0x54;

	@Override
	public boolean apply(ClassNode node) {
		if (RPConfig.Client.isNarratorKeybindEnabled()) {
			patchDispatchKeypresses(findInstructions(node, "dispatchKeypresses", "func_152348_aa"));
		}

		if (!RandomPatches.DEFAULT_WINDOW_TITLE.equals(RPConfig.Window.title)) {
			patchCreateDisplay(findInstructions(node, "createDisplay", "func_175609_am"));
		}

		if (!RPConfig.Window.icon16String.isEmpty()) {
			patchSetWindowIcon(findInstructions(node, "setWindowIcon", "func_175594_ao"));
		}

		return true;
	}

	@Override
	public boolean computeFrames() {
		return !RandomPatches.REPLAY_MOD_INSTALLED;
	}

	private void patchDispatchKeypresses(InsnList instructions) {
		IntInsnNode isB = null;

		for (int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if (instruction.getOpcode() == Opcodes.BIPUSH) {
				isB = (IntInsnNode) instruction;

				if (isB.operand == Keyboard.KEY_B) {
					break;
				}

				isB = null;
			}
		}

		//Call MinecraftHook#handleKeypress
		instructions.insertBefore(isB.getPrevious(), new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				hookClass,
				"handleKeypress",
				"()V",
				false
		));

		isB.operand = KEY_UNUSED;
	}

	private static void patchCreateDisplay(InsnList instructions) {
		for (int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if (instruction.getOpcode() == Opcodes.INVOKESTATIC) {
				final MethodInsnNode setTitle = (MethodInsnNode) instruction;

				if ("setTitle".equals(setTitle.name)) {
					((LdcInsnNode) setTitle.getPrevious()).cst = RPConfig.Window.title;
					return;
				}
			}
		}
	}

	private static void patchSetWindowIcon(InsnList instructions) {
		final InsnList newInstructions = new InsnList();

		//Call WindowIconHandler#setWindowIcon
		newInstructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				getName(WindowIconHandler.class),
				"setWindowIcon",
				"()V",
				false
		));

		//Return
		newInstructions.add(new InsnNode(Opcodes.RETURN));

		instructions.insertBefore(instructions.getFirst(), newInstructions);
	}
}
