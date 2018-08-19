package com.therandomlabs.randompatches.core.transformer;

import com.therandomlabs.randompatches.core.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class LoginServerTransformer extends Transformer {
	@Override
	public void transform(ClassNode node) {
		final MethodNode method = findMethod(node, "update", "func_73660_a");
		LdcInsnNode loginTimeout = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() == Opcodes.LDC) {
				loginTimeout = (LdcInsnNode) instruction;

				if(new Integer(600).equals(loginTimeout.cst)) {
					break;
				}

				loginTimeout = null;
			}
		}

		final FieldInsnNode getLoginTimeout = new FieldInsnNode(
				Opcodes.GETSTATIC,
				"com/therandomlabs/randompatches/RPStaticConfig",
				"loginTimeout",
				"I"
		);

		method.instructions.insert(loginTimeout, getLoginTimeout);
		method.instructions.remove(loginTimeout);
	}
}
