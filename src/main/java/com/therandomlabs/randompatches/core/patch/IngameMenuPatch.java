package com.therandomlabs.randompatches.core.patch;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class IngameMenuPatch extends Patch {
	@SuppressWarnings("Duplicates")
	@Override
	public void apply(ClassNode node) {
		final MethodNode method = findMethod(node, "actionPerformed", "func_146284_a");
		AbstractInsnNode storeIsIntegratedServerRunning = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() == Opcodes.ISTORE) {
				storeIsIntegratedServerRunning = instruction;
				break;
			}
		}

		final LabelNode label = new LabelNode();
		final FieldInsnNode getEnabled = new FieldInsnNode(
				Opcodes.GETSTATIC,
				"com/therandomlabs/randompatches/config/RPStaticConfig",
				"forceTitleScreenOnDisconnect",
				"Z"
		);
		final JumpInsnNode jumpIfNotEnabled = new JumpInsnNode(Opcodes.IFEQ, label);
		final InsnNode loadTrue = new InsnNode(Opcodes.ICONST_1);
		final VarInsnNode storeTrue = new VarInsnNode(Opcodes.ISTORE, 2);

		method.instructions.insert(storeIsIntegratedServerRunning, getEnabled);
		method.instructions.insert(getEnabled, jumpIfNotEnabled);
		method.instructions.insert(jumpIfNotEnabled, loadTrue);
		method.instructions.insert(loadTrue, storeTrue);
		method.instructions.insert(storeTrue, label);
	}
}
