package com.therandomlabs.randompatches.patch.endportal;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class BlockEndPortalPatch extends Patch {
	@SuppressWarnings("Duplicates")
	@Override
	public boolean apply(ClassNode node) {
		final MethodNode method = findMethod(node, "shouldSideBeRendered", "func_176225_a");

		JumpInsnNode isDown = null;
		InsnNode loadZero = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(isDown == null) {
				if(instruction.getOpcode() == Opcodes.IF_ACMPNE) {
					isDown = (JumpInsnNode) instruction;
				}

				continue;
			}

			if(instruction.getOpcode() == Opcodes.ICONST_0) {
				loadZero = (InsnNode) instruction;
				break;
			}
		}

		final LabelNode labelSuper = new LabelNode();
		final LabelNode labelReturnFalse = new LabelNode();

		isDown.setOpcode(Opcodes.IF_ACMPEQ);
		isDown.label = labelSuper;

		final VarInsnNode loadFacing = new VarInsnNode(
				Opcodes.ALOAD,
				4
		);

		final FieldInsnNode getUp = new FieldInsnNode(
				Opcodes.GETSTATIC,
				"net/minecraft/util/EnumFacing",
				"UP",
				"Lnet/minecraft/util/EnumFacing;"
		);

		final JumpInsnNode jumpIfNotEqual = new JumpInsnNode(
				Opcodes.IF_ACMPNE,
				labelReturnFalse
		);

		method.instructions.insert(isDown, getUp);
		method.instructions.insert(getUp, loadFacing);
		method.instructions.insert(loadFacing, jumpIfNotEqual);
		method.instructions.insert(jumpIfNotEqual, labelSuper);
		method.instructions.insertBefore(loadZero, labelReturnFalse);

		return true;
	}
}
