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

package com.therandomlabs.randompatches.mixin.client.keybindings;

import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.client.RPKeyBindingHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public final class ClientPlayerEntityMixin {
	@Redirect(method = "livingTick", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/settings/KeyBinding.isKeyDown()Z"
	))
	private boolean isSprintKeyDown(KeyBinding sprintKeyBinding) {
		if (sprintKeyBinding.isKeyDown()) {
			return true;
		}

		if (!RandomPatches.config().client.keyBindings.secondarySprint) {
			return false;
		}

		final InputMappings.Input forwardKey =
				Minecraft.getInstance().gameSettings.keyBindForward.getKey();
		return !RPKeyBindingHandler.KeyBindings.SECONDARY_SPRINT.getKey().equals(forwardKey) &&
				RPKeyBindingHandler.KeyBindings.SECONDARY_SPRINT.isKeyDown();
	}

	@Redirect(method = "livingTick", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/entity/player/ClientPlayerEntity.setSprinting(Z)V",
			ordinal = 0
	))
	private void enableSprintingThroughSecondarySprint(ClientPlayerEntity player, boolean flag) {
		if (!RandomPatches.config().client.keyBindings.secondarySprint ||
				RPKeyBindingHandler.KeyBindings.SECONDARY_SPRINT.isKeyDown()) {
			player.setSprinting(true);
		}
	}
}
