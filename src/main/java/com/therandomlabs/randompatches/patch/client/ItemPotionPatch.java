package com.therandomlabs.randompatches.patch.client;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

public final class ItemPotionPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		InsnList instructions = findInstructions(node, "hasEffect", "func_77962_s");

		if (instructions == null) {
			instructions = findInstructions(node, "hasEffect", "func_77636_d");
		}

		for (int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if (instruction.getOpcode() == Opcodes.INVOKEINTERFACE) {
				final MethodInsnNode isEmpty = (MethodInsnNode) instruction;

				isEmpty.setOpcode(Opcodes.INVOKESTATIC);
				isEmpty.owner = hookClass;
				isEmpty.desc = "(Ljava/util/List;)Z";
				isEmpty.itf = false;

				return true;
			}
		}

		return false;
	}
}
