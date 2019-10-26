package com.therandomlabs.randompatches.hook.client;

import net.minecraft.client.Minecraft;

public final class GuiLanguageListHook {
	private GuiLanguageListHook() {}

	public static void reloadLanguage() {
		final Minecraft mc = Minecraft.getMinecraft();
		mc.getLanguageManager().onResourceManagerReload(mc.getResourceManager());
	}
}
