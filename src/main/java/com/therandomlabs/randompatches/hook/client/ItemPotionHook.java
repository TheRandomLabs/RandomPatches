package com.therandomlabs.randompatches.hook.client;

import com.therandomlabs.randompatches.config.RPConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;

public final class ItemPotionHook {
	private ItemPotionHook() {}

	public static boolean hasEffect(ItemStack stack) {
		if (stack.isItemEnchanted()) {
			return true;
		}

		return !RPConfig.Client.removePotionGlint &&
				!PotionUtils.getEffectsFromStack(stack).isEmpty();
	}
}
