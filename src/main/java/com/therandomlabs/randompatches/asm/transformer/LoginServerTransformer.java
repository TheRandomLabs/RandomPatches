package com.therandomlabs.randompatches.asm.transformer;

import com.therandomlabs.randompatches.asm.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class LoginServerTransformer extends Transformer {
	@Override
	public boolean transform(ClassNode node) {
		final MethodNode method = findMethod(node, "()V", PlayServerTransformer.UPDATE);

		AbstractInsnNode toPatch = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);
			if(instruction.getType() == AbstractInsnNode.LDC_INSN) {
				final LdcInsnNode ldc = (LdcInsnNode) instruction;
				if(new Integer(600).equals(ldc.cst)) {
					toPatch = ldc;
					break;
				}
			}
		}

		if(toPatch == null) {
			return false;
		}

		final FieldInsnNode getLoginTimeout = new FieldInsnNode(
				Opcodes.GETSTATIC,
				"com/therandomlabs/randompatches/RPStaticConfig",
				"loginTimeout",
				"I"
		);

		method.instructions.insert(toPatch, getLoginTimeout);
		method.instructions.remove(toPatch);

		return true;
	}
}
