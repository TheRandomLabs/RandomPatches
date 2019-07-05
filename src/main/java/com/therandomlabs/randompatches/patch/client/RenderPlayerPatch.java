package com.therandomlabs.randompatches.patch.client;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

//Thanks Fuzs_!
public final class RenderPlayerPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "applyRotations", "func_77043_a");

		for(int i = instructions.size() - 1; i >= 0; i--) {
			final AbstractInsnNode instruction = instructions.get(i);

			if(instruction.getOpcode() == Opcodes.INVOKESTATIC) {
				final MethodInsnNode method = (MethodInsnNode) instruction;

				if("acos".equals(method.name)) {
					method.owner = getName(RenderPlayerPatch.class);
					return true;
				}
			}
		}

		return false;
	}

	//In RenderPlayer#applyRotations, Math#acos is sometimes called with a value larger than 1.0,
	//making the rotation angle NaN and making the player model disappear
	//This issue is noticeable when flying with elytra in a straight line in third-person mode
	public static double acos(double a) {
		return Math.acos(Math.min(a, 1.0));
	}
}
