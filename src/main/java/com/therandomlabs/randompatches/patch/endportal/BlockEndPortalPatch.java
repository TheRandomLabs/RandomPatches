package com.therandomlabs.randompatches.patch.endportal;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class BlockEndPortalPatch extends Patch {
	//So that TileEntityEndPortalPatch can easily get the name of BlockEndPortalHook
	public static final BlockEndPortalPatch INSTANCE = new BlockEndPortalPatch();

	private BlockEndPortalPatch() {}

	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions =
				findInstructions(node, "shouldSideBeRendered", "func_176225_a");

		instructions.clear();

		//Get side
		instructions.add(new VarInsnNode(Opcodes.ALOAD, 4));

		//Call BlockEndPortalHook#shouldSideBeRendered
		instructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				hookClass,
				"shouldSideBeRendered",
				"(Lnet/minecraft/util/EnumFacing;)Z",
				false
		));

		//Return BlockEndPortalHook#shouldSideBeRendered
		instructions.add(new InsnNode(Opcodes.IRETURN));

		return true;
	}
}
