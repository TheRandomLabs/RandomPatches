package com.therandomlabs.randompatches.patch.endportal;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;

public final class BlockModelShapesPatch extends Patch {
	public static final String AIR = getName("AIR", "field_150350_a");
	public static final String END_PORTAL = getName("END_PORTAL", "field_150384_bq");
	public static final String END_GATEWAY = getName("END_GATEWAY", "field_185775_db");

	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "registerAllBlocks", "func_178119_d");

		for(int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if(instruction.getOpcode() == Opcodes.GETSTATIC) {
				final FieldInsnNode field = (FieldInsnNode) instruction;

				if(END_PORTAL.equals(field.name)) {
					field.name = AIR;
					continue;
				}

				if(END_GATEWAY.equals(field.name)) {
					field.name = AIR;
					return true;
				}
			}
		}

		return false;
	}
}
