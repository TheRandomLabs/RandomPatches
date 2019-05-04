package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.Patch;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class ServerRecipePlacerPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final MethodNode method = findMethod(node, "consumeIngredient", "func_194325_a");

		MethodInsnNode findSlotMatchingUnusedItem = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				findSlotMatchingUnusedItem = (MethodInsnNode) instruction;
				break;
			}
		}

		findSlotMatchingUnusedItem.setOpcode(Opcodes.INVOKESTATIC);
		findSlotMatchingUnusedItem.owner = getName(ServerRecipePlacerPatch.class);
		findSlotMatchingUnusedItem.name = "findSlotMatchingUnusedItem";
		findSlotMatchingUnusedItem.desc =
				"(Lnet/minecraft/entity/player/InventoryPlayer;Lnet/minecraft/item/ItemStack;)I";

		return true;
	}

	public static int findSlotMatchingUnusedItem(InventoryPlayer inventory, ItemStack toMatch) {
		for(int i = 0; i < inventory.mainInventory.size(); i++) {
			final ItemStack stack = inventory.mainInventory.get(i);

			if(!stack.isEmpty() && stack.getItem() == toMatch.getItem() && !stack.isDamaged() &&
					!stack.isEnchanted() && !stack.hasDisplayName()) {
				return i;
			}
		}

		return -1;
	}
}
