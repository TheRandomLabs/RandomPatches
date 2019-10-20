package com.therandomlabs.randompatches.patch.client;

import com.therandomlabs.randompatches.config.RPConfig;
import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class GuiIngameMenuPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "actionPerformed", "func_146284_a");
		AbstractInsnNode storeIsIntegratedServerRunning = null;

		for (int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if (instruction.getOpcode() == Opcodes.ISTORE) {
				storeIsIntegratedServerRunning = instruction;
				break;
			}
		}

		final InsnList newInstructions = new InsnList();

		final LabelNode label = new LabelNode();

		//Get RPConfig.Client#forceTitleScreenOnDisconnect
		newInstructions.add(new FieldInsnNode(
				Opcodes.GETSTATIC,
				getName(RPConfig.Client.class),
				"forceTitleScreenOnDisconnect",
				"Z"
		));

		//Jump if not enabled
		newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, label));

		//Load true
		newInstructions.add(new InsnNode(Opcodes.ICONST_1));

		//Store true to flag (isIntegratedServerRunning)
		newInstructions.add(new VarInsnNode(Opcodes.ISTORE, 2));

		newInstructions.add(label);

		instructions.insert(storeIsIntegratedServerRunning, newInstructions);

		return true;
	}
}
