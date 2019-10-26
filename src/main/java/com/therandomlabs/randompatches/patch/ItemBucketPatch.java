package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class ItemBucketPatch extends Patch {
	public static final String IS_SOLID = getName("isSolid", "func_76220_a");

	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions =
				findInstructions(node, "tryPlaceContainedLiquid", "func_180616_a");
		MethodInsnNode isSolid = null;

		for (int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				isSolid = (MethodInsnNode) instruction;

				if (IS_SOLID.equals(isSolid.name)) {
					break;
				}

				isSolid = null;
			}
		}

		//Get IBlockState
		((VarInsnNode) isSolid.getPrevious()).var = 4;

		//Call ItemBucketHook#isSolid
		isSolid.setOpcode(Opcodes.INVOKESTATIC);
		isSolid.owner = hookClass;
		isSolid.name = "isSolid";
		isSolid.desc = "(Lnet/minecraft/block/state/IBlockState;)Z";

		return true;
	}
}
