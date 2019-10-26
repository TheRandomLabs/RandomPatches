package com.therandomlabs.randompatches.patch.endportal;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class TileEntityEndPortalPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "shouldRenderFace", "func_184313_a");

		instructions.clear();

		//Get face
		instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));

		//Call BlockEndPortalHook#shouldSideBeRendered
		instructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				BlockEndPortalPatch.INSTANCE.hookClass,
				"shouldSideBeRendered",
				"(Lnet/minecraft/util/EnumFacing;)Z",
				false
		));

		//Return BlockEndPortalHook#shouldSideBeRendered
		instructions.add(new InsnNode(Opcodes.IRETURN));

		return true;
	}
}
