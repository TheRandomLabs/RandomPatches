package com.therandomlabs.randompatches.patch.endportal;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class BlockModelShapesPatch extends Patch {
	public static final String AIR = getName("AIR", "field_150350_a");
	public static final String END_PORTAL = getName("END_PORTAL", "field_150384_bq");

	@Override
	public void apply(ClassNode node) {
		final MethodNode method = findMethod(node, "registerAllBlocks", "func_178119_d");

		FieldInsnNode getEndPortal = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() == Opcodes.GETSTATIC) {
				getEndPortal = (FieldInsnNode) instruction;

				if(END_PORTAL.equals(getEndPortal.name)) {
					break;
				}

				getEndPortal = null;
			}
		}

		getEndPortal.name = AIR;
	}
}
