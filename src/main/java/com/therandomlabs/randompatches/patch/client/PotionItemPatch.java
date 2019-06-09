package com.therandomlabs.randompatches.patch.client;

import com.therandomlabs.randompatches.RPConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;

public final class PotionItemPatch {
	private PotionItemPatch() {}

	public static boolean hasEffect(ItemStack stack) {
		if(stack.isEnchanted()) {
			return true;
		}

		return !RPConfig.Client.removePotionGlint &&
				!PotionUtils.getEffectsFromStack(stack).isEmpty();
	}
}
