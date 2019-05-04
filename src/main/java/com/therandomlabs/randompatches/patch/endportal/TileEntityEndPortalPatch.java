package com.therandomlabs.randompatches.patch.endportal;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class TileEntityEndPortalPatch extends Patch {
	@SuppressWarnings("Duplicates")
	@Override
	public boolean apply(ClassNode node) {
		final MethodNode method = findMethod(node, "shouldRenderFace", "func_184313_a");

		final LabelNode labelReturnTrue = new LabelNode();

		final VarInsnNode loadFacing = new VarInsnNode(Opcodes.ALOAD, 1);

		final FieldInsnNode getUp = new FieldInsnNode(
				Opcodes.GETSTATIC,
				"net/minecraft/util/EnumFacing",
				"UP",
				"Lnet/minecraft/util/EnumFacing;"
		);

		final JumpInsnNode returnTrueIfEqual = new JumpInsnNode(
				Opcodes.IF_ACMPEQ,
				labelReturnTrue
		);

		final VarInsnNode loadFacing2 = new VarInsnNode(Opcodes.ALOAD, 1);

		final FieldInsnNode getDown = new FieldInsnNode(
				Opcodes.GETSTATIC,
				"net/minecraft/util/EnumFacing",
				"DOWN",
				"Lnet/minecraft/util/EnumFacing;"
		);

		final JumpInsnNode returnTrueIfEqual2 = new JumpInsnNode(
				Opcodes.IF_ACMPEQ,
				labelReturnTrue
		);

		final InsnNode loadZero = new InsnNode(Opcodes.ICONST_0);

		final InsnNode returnFalse = new InsnNode(Opcodes.IRETURN);

		final InsnNode loadOne = new InsnNode(Opcodes.ICONST_1);

		final InsnNode returnTrue = new InsnNode(Opcodes.IRETURN);

		method.instructions.clear();

		method.instructions.add(loadFacing);
		method.instructions.add(getUp);
		method.instructions.add(returnTrueIfEqual);
		method.instructions.add(loadFacing2);
		method.instructions.add(getDown);
		method.instructions.add(returnTrueIfEqual2);
		method.instructions.add(loadZero);
		method.instructions.add(returnFalse);
		method.instructions.add(labelReturnTrue);
		method.instructions.add(loadOne);
		method.instructions.add(returnTrue);

		return true;
	}
}
