package com.therandomlabs.randompatches.patch.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.resource.VanillaResourceType;

public final class LanguageScreenListPatch {
	private LanguageScreenListPatch() {}

	public static void reloadLanguage(Minecraft mc, VanillaResourceType... types) {
		mc.getLanguageManager().onResourceManagerReload(mc.getResourceManager());
	}
}
