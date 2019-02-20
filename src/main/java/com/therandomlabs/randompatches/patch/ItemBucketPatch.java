package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.core.Patch;
import net.minecraft.block.material.Material;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class ItemBucketPatch extends Patch {
	public static final String IS_SOLID = getName("isSolid", "func_76220_a");

	@Override
	public boolean apply(ClassNode node) {
		final MethodNode method = findMethod(node, "tryPlaceContainedLiquid", "func_180616_a");
		MethodInsnNode isSolid = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction instanceof MethodInsnNode) {
				isSolid = (MethodInsnNode) instruction;

				if(IS_SOLID.equals(isSolid.name)) {
					break;
				}

				isSolid = null;
			}
		}

		isSolid.setOpcode(Opcodes.INVOKESTATIC);
		isSolid.owner = getName(ItemBucketPatch.class);
		isSolid.name = "isSolid";
		isSolid.desc = "(Lnet/minecraft/block/material/Material;)Z";

		return true;
	}

	public static boolean isSolid(Material material) {
		return material.isSolid() || material == Material.PORTAL;
	}
}
