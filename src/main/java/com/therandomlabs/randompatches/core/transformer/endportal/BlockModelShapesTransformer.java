package com.therandomlabs.randompatches.core.transformer.endportal;

import com.therandomlabs.randompatches.core.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class BlockModelShapesTransformer extends Transformer {
	public static final String AIR = getName("AIR", "field_150350_a");
	public static final String END_PORTAL = getName("END_PORTAL", "field_150384_bq");

	@Override
	public void transform(ClassNode node) {
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
