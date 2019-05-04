package com.therandomlabs.randompatches.patch.client;

import net.minecraft.client.Minecraft;

public final class GuiLanguageListPatch {
	private GuiLanguageListPatch() {}

	public static void reloadLanguage() {
		final Minecraft mc = Minecraft.getInstance();
		mc.getLanguageManager().onResourceManagerReload(mc.getResourceManager());
	}
}
