package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;

public final class NetHandlerLoginServerPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "update", "func_73660_a");
		LdcInsnNode loginTimeout = null;

		for (int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if (instruction.getOpcode() == Opcodes.LDC) {
				loginTimeout = (LdcInsnNode) instruction;

				if (((Integer) 600).equals(loginTimeout.cst)) {
					break;
				}

				loginTimeout = null;
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
