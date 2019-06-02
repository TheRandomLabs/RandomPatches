package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.core.Patch;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
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

		for(int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if(instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				findSlotMatchingUnusedItem = (MethodInsnNode) instruction;
				break;
			}
		}

		//Call ServerRecipeBookHelper#findSlotMatchingUnusedItem
		findSlotMatchingUnusedItem.setOpcode(Opcodes.INVOKESTATIC);
		findSlotMatchingUnusedItem.owner = getName(ServerRecipeBookHelperPatch.class);
		findSlotMatchingUnusedItem.name = "findSlotMatchingUnusedItem";
		findSlotMatchingUnusedItem.desc =
				"(Lnet/minecraft/entity/player/InventoryPlayer;Lnet/minecraft/item/ItemStack;)I";

		return true;
	}

	public static int findSlotMatchingUnusedItem(InventoryPlayer inventory, ItemStack toMatch) {
		for(int i = 0; i < inventory.mainInventory.size(); i++) {
			final ItemStack stack = inventory.mainInventory.get(i);

			if(!stack.isEmpty() && stackEqualExact(toMatch, stack) && !stack.isItemDamaged() &&
					!stack.isItemEnchanted() && !stack.hasDisplayName()) {
				return i;
			}
		}

		return -1;
	}

	public static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
		//OreDictionary#WILDCARD_VALUE is Short#MAX_VALUE
		return stack1.getItem() == stack2.getItem() && (stack1.getMetadata() == Short.MAX_VALUE ||
				stack1.getMetadata() == stack2.getMetadata());
	}
}
