package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;

public final class NetHandlerLoginServerPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "update", "func_73660_a");
		AbstractInsnNode loginTimeout = null;

		for (int i = instructions.size() - 1; i >= 0; i--) {
			final AbstractInsnNode instruction = instructions.get(i);

			if (instruction.getType() == AbstractInsnNode.INT_INSN) {
				loginTimeout = instruction;
				break;
			}
		}

		//Get RPConfig.Timeouts#loginTimeout
		instructions.insert(loginTimeout, new FieldInsnNode(
				Opcodes.GETSTATIC,
				NetHandlerPlayServerPatch.TIMEOUTS_CONFIG,
				"loginTimeout",
				"I"
		));

		instructions.remove(loginTimeout);

		return true;
	}
}
