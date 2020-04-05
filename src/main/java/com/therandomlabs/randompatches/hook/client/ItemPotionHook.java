package com.therandomlabs.randompatches.hook.client;

import java.util.List;

import com.therandomlabs.randompatches.config.RPConfig;
import net.minecraft.potion.PotionEffect;

public final class ItemPotionHook {
	private ItemPotionHook() {}

	public static boolean isEmpty(List<PotionEffect> effects) {
		return RPConfig.Client.removePotionGlint || effects.isEmpty();
	}
}
