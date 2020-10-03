package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.core.Patch;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class ItemBucketPatch extends Patch {
	public static final String IS_SOLID = getName("isSolid", "func_76220_a");

	@Override
	public boolean apply(ClassNode node) {
		//Look for the deobfuscated name first because CatServer adds a new method:
		//https://github.com/Luohuayu/CatServer/blob/9489fbb82247a08a0b4c1b62c59e3c50302f43e2/
		//patches/net/minecraft/item/ItemBucket.java.patch#L99
		MethodNode method = findMethod(node, "tryPlaceContainedLiquid");

		if (method == null) {
			method = findMethod(node, "func_180616_a");
		}

		final InsnList instructions = method.instructions;

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
		((VarInsnNode) isSolid.getPrevious()).var = StringUtils.countMatches(method.desc, ';') + 1;

		//Call ItemBucketHook#isSolid
		isSolid.setOpcode(Opcodes.INVOKESTATIC);
		isSolid.owner = hookClass;
		isSolid.name = "isSolid";
		isSolid.desc = "(Lnet/minecraft/block/state/IBlockState;)Z";

		return true;
	}
}
