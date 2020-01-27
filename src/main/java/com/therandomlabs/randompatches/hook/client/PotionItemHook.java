package com.therandomlabs.randompatches.hook.client;

import com.therandomlabs.randompatches.RPConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;

public final class PotionItemHook {
	private PotionItemHook() {}

	public static boolean hasEffect(ItemStack stack) {
		if (stack.isEnchanted()) {
			return true;
		}

		return !RPConfig.Client.removePotionGlint &&
				!PotionUtils.getEffectsFromStack(stack).isEmpty();
	}
}
