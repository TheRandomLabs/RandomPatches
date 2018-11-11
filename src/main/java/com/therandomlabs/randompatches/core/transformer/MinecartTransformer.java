package com.therandomlabs.randompatches.core.transformer;

import com.therandomlabs.randompatches.core.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

public final class MinecartTransformer extends Transformer {
	@Override
	public void transform(ClassNode node) {
		final MethodNode method = findMethod(node, "moveAlongTrack", "func_180460_a");
		TypeInsnNode instanceOfPlayer = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() == Opcodes.INSTANCEOF) {
				instanceOfPlayer = (TypeInsnNode) instruction;
				break;
			}
		}

		instanceOfPlayer.desc = "net/minecraft/entity/player/EntityPlayer";
	}
}
