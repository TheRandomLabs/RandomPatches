package com.therandomlabs.randompatches.core.patch;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

public final class MinecartPatch extends Patch {
	@Override
	public void apply(ClassNode node) {
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
