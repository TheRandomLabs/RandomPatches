package com.therandomlabs.randompatches.core.patch;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class WorldServerPatch extends Patch {
	@Override
	public void apply(ClassNode node) {
		final MethodNode method = findMethod(node, "<init>");
		MethodInsnNode createTeleporter = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() == Opcodes.INVOKESPECIAL) {
				createTeleporter = (MethodInsnNode) instruction;

				if("net/minecraft/world/Teleporter".equals(createTeleporter.owner)) {
					break;
				}

				createTeleporter = null;
			}
		}

		createTeleporter.owner = "com/therandomlabs/verticalendportals/util/VEPTeleporter";
	}
}
