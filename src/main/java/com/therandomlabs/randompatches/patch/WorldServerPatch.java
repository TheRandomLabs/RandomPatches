package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

//https://github.com/SleepyTrousers/EnderCore/issues/105#issuecomment-475957390 and
//https://github.com/SleepyTrousers/EnderCore/issues/105#issuecomment-504779582 and
//https://github.com/SleepyTrousers/EnderCore/issues/105#issuecomment-506215102
//Thanks, malte0811!
public final class WorldServerPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "<init>");

		for (int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if (instruction.getOpcode() == Opcodes.INVOKESTATIC) {
				final MethodInsnNode method = (MethodInsnNode) instruction;

				if ("newHashSet".equals(method.name)) {
					method.owner = getHookInnerClass("NextTickListEntryHashSet");
					return true;
				}
			}
		}

		return false;
	}
}
