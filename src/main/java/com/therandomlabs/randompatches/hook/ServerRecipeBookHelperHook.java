package com.therandomlabs.randompatches.hook;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public final class ServerRecipeBookHelperHook {
	private ServerRecipeBookHelperHook() {}

	public static int findSlotMatchingUnusedItem(InventoryPlayer inventory, ItemStack toMatch) {
		for (int i = 0; i < inventory.mainInventory.size(); i++) {
			final ItemStack stack = inventory.mainInventory.get(i);

			if (!stack.isEmpty() && stackEqualExact(toMatch, stack) && !stack.isItemDamaged() &&
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
