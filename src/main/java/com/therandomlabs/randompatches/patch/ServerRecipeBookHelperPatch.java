package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

public final class ServerRecipeBookHelperPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "func_194325_a");

		MethodInsnNode findSlotMatchingUnusedItem = null;

		for (int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				findSlotMatchingUnusedItem = (MethodInsnNode) instruction;
				break;
			}
		}

		//Call ServerRecipeBookHelper#findSlotMatchingUnusedItem
		findSlotMatchingUnusedItem.setOpcode(Opcodes.INVOKESTATIC);
		findSlotMatchingUnusedItem.owner = hookClass;
		findSlotMatchingUnusedItem.name = "findSlotMatchingUnusedItem";
		findSlotMatchingUnusedItem.desc =
				"(Lnet/minecraft/entity/player/InventoryPlayer;Lnet/minecraft/item/ItemStack;)I";

		return true;
	}
}
