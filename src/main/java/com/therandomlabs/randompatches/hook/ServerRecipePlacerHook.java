package com.therandomlabs.randompatches.hook;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public final class ServerRecipePlacerHook {
	private ServerRecipePlacerHook() {}

	public static int findSlotMatchingUnusedItem(PlayerInventory inventory, ItemStack toMatch) {
		for (int i = 0; i < inventory.mainInventory.size(); i++) {
			final ItemStack stack = inventory.mainInventory.get(i);

			if (!stack.isEmpty() && stack.getItem() == toMatch.getItem() && !stack.isDamaged() &&
					!stack.isEnchanted() && !stack.hasDisplayName()) {
				return i;
			}
		}

		return -1;
	}
}