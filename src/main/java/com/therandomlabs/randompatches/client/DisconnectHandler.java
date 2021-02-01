/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2021 TheRandomLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
