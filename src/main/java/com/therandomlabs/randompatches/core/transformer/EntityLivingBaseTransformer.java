package com.therandomlabs.randompatches.core.transformer;

import com.therandomlabs.randompatches.core.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public class EntityLivingBaseTransformer extends Transformer {
	@Override
	public void transform(ClassNode node) {
		final MethodNode method = findMethod(node, "updateActiveHand", "func_184608_ct");
		AbstractInsnNode jumpIfNotEqual = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() == Opcodes.IF_ACMPNE) {
				jumpIfNotEqual = instruction;
				break;
			}
		}

		final InsnNode pop = new InsnNode(Opcodes.POP2);

		method.instructions.insert(jumpIfNotEqual, pop);
		method.instructions.remove(jumpIfNotEqual);
	}
}
