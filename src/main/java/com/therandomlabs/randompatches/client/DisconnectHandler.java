package com.therandomlabs.randompatches.client;

import com.therandomlabs.randompatches.RandomPatches;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.realms.gui.screen.RealmsBridgeScreen;
import net.minecraft.text.TranslatableText;

/**
 * Handles disconnecting from a world.
 */
public final class DisconnectHandler {
	private DisconnectHandler() {}

	/**
	 * Disconnects from the current world.
	 */
	@SuppressWarnings("ConstantConditions")
	public static void disconnect() {
		final MinecraftClient mc = MinecraftClient.getInstance();
		final boolean singleplayer = mc.isInSingleplayer();

		mc.world.disconnect();

		if (singleplayer) {
			mc.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
		} else {
			mc.disconnect();
		}

		if (RandomPatches.config().client.returnToMainMenuAfterDisconnect || singleplayer) {
			mc.openScreen(new TitleScreen());
		} else if (mc.isConnectedToRealms()) {
			new RealmsBridgeScreen().switchToRealms(new TitleScreen());
		} else {
			mc.openScreen(new MultiplayerScreen(new TitleScreen()));
		}
	}
}
