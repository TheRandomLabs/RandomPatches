/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 TheRandomLabs
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

package com.therandomlabs.randompatches.mixin.client;

import com.therandomlabs.randompatches.RandomPatches;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.DirtMessageScreen;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(IngameMenuScreen.class)
public final class IngameMenuScreenMixin {
	@SuppressWarnings("ConstantConditions")
	@ModifyArg(
			method = "addButtons",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "net/minecraft/client/Minecraft.isSingleplayer()Z"
					)
			),
			at = @At(
					value = "INVOKE",
					target = "net/minecraft/client/gui/widget/button/Button.<init>" +
							"(IIIILnet/minecraft/util/text/ITextComponent;" +
							"Lnet/minecraft/client/gui/widget/button/Button$IPressable;)V",
					ordinal = 0 //We set the ordinal just in case. :P
			)
	)
	private Button.IPressable adjustOnPress(Button.IPressable onPress) {
		return button -> {
			final Minecraft mc = Minecraft.getInstance();

			button.active = false;
			mc.world.sendQuittingDisconnectingPacket();

			if (mc.isIntegratedServerRunning()) {
				mc.func_213231_b(new DirtMessageScreen(
						new TranslationTextComponent("menu.savingLevel")
				));
			} else {
				mc.func_213254_o();
			}

			if (RandomPatches.config().client.returnToMainMenuAfterDisconnect ||
					mc.isIntegratedServerRunning()) {
				mc.displayGuiScreen(new MainMenuScreen());
			} else if (mc.isConnectedToRealms()) {
				new RealmsBridgeScreen().switchToRealms(new MainMenuScreen());
			} else {
				mc.displayGuiScreen(new MultiplayerScreen(new MainMenuScreen()));
			}
		};
	}
}
