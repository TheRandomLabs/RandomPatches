package com.therandomlabs.randompatches.patch;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public final class ServerRecipePlacerPatch {
	private ServerRecipePlacerPatch() {}

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
