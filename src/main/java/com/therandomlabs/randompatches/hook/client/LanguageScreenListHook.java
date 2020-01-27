package com.therandomlabs.randompatches.hook.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.resource.VanillaResourceType;

public final class LanguageScreenListHook {
	private LanguageScreenListHook() {}

	public static void reloadLanguage(Minecraft mc, VanillaResourceType... types) {
		mc.getLanguageManager().onResourceManagerReload(mc.getResourceManager());
	}
}
