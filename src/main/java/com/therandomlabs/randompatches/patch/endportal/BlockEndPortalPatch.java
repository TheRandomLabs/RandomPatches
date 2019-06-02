package com.therandomlabs.randompatches.patch.endportal;

import com.therandomlabs.randompatches.core.Patch;
import net.minecraft.util.EnumFacing;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class BlockEndPortalPatch extends Patch {
	@SuppressWarnings("Duplicates")
	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions =
				findInstructions(node, "shouldSideBeRendered", "func_176225_a");

		instructions.clear();

		//Get side
		instructions.add(new VarInsnNode(Opcodes.ALOAD, 4));

		//Call BlockEndPortalPatch#shouldSideBeRendered
		instructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				getName(BlockEndPortalPatch.class),
				"shouldSideBeRendered",
				"(Lnet/minecraft/util/EnumFacing;)Z",
				false
		));

		//Return BlockEndPortalPatch#shouldSideBeRendered
		instructions.add(new InsnNode(Opcodes.IRETURN));

		return true;
	}

	public static boolean shouldSideBeRendered(EnumFacing side) {
		return side == EnumFacing.UP || side == EnumFacing.DOWN;
	}
}
