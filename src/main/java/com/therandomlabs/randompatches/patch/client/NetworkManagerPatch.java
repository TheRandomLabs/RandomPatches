package com.therandomlabs.randompatches.patch.client;

import com.therandomlabs.randompatches.core.Patch;
import com.therandomlabs.randompatches.patch.NetHandlerPlayServerPatch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;

public final class NetworkManagerPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "initChannel");
		AbstractInsnNode readTimeout = null;

		for (int i = 0; i < instructions.size(); i++) {
			readTimeout = instructions.get(i);

			if (readTimeout.getOpcode() == Opcodes.BIPUSH) {
				break;
			}

			readTimeout = null;
		}

		instructions.insert(readTimeout, new FieldInsnNode(
				Opcodes.GETSTATIC,
				NetHandlerPlayServerPatch.TIMEOUTS_CONFIG,
				"readTimeout",
				"I"
		));
		instructions.remove(readTimeout);

		return true;
	}
}
