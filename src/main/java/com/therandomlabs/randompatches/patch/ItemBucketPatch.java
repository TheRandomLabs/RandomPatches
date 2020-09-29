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
		//Look for the deobfuscated name first because CatServer adds a new method:
		//https://github.com/Luohuayu/CatServer/blob/9489fbb82247a08a0b4c1b62c59e3c50302f43e2/
		//patches/net/minecraft/item/ItemBucket.java.patch#L99
		InsnList instructions = findInstructions(node, "tryPlaceContainedLiquid");
		//CatServer adds three method parameters.
		int blockState = 7;

		if (instructions == null) {
			instructions = findInstructions(node, "func_180616_a");
			blockState = 4;
		}

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
		((VarInsnNode) isSolid.getPrevious()).var = blockState;

		//Call ItemBucketHook#isSolid
		isSolid.setOpcode(Opcodes.INVOKESTATIC);
		isSolid.owner = hookClass;
		isSolid.name = "isSolid";
		isSolid.desc = "(Lnet/minecraft/block/state/IBlockState;)Z";

		return true;
	}
}
