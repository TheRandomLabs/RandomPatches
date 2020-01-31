package com.therandomlabs.randompatches.hook.client;

import com.therandomlabs.randompatches.RPConfig;
import com.therandomlabs.utils.forge.ForgeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.resources.I18n;

public final class MinecraftHook {
	private MinecraftHook() {}

	public static String getTitle(Minecraft mc) {
		final ClientPlayNetHandler handler = mc.getConnection();
		final String activity;

		if (handler != null && handler.getNetworkManager().isChannelOpen()) {
			if (mc.getIntegratedServer() != null && !mc.getIntegratedServer().getPublic()) {
				activity = "title.singleplayer";
			} else if (mc.isConnectedToRealms()) {
				activity = "title.multiplayer.realms";
			} else if (mc.getIntegratedServer() == null &&
					(mc.getCurrentServerData() == null || !mc.getCurrentServerData().isOnLAN())) {
				activity = "title.multiplayer.other";
			} else {
				activity = "title.multiplayer.lan";
			}
		} else {
			activity = null;
		}

		if (activity == null) {
			return String.format(RPConfig.Window.title, ForgeUtils.MC_VERSION);
		}

		return String.format(
				RPConfig.Window.titleWithActivity, ForgeUtils.MC_VERSION, I18n.format(activity)
		);
	}
}
