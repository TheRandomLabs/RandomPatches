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

package com.therandomlabs.randompatches.mixin.client.keybindings;

import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.client.RPKeyBindingHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.ToggleableKeyBinding;
import net.minecraft.client.util.InputMappings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToggleableKeyBinding.class)
public final class ToggleableKeyBindingMixin {
	@SuppressWarnings("ConstantConditions")
	@Inject(method = "isKeyDown", at = @At("RETURN"), cancellable = true)
	private void isKeyDown(CallbackInfoReturnable<Boolean> info) {
		final KeyBinding sprint = Minecraft.getInstance().gameSettings.keyBindSprint;

		if ((Object) this != sprint || info.getReturnValue() ||
				!RandomPatches.config().client.keyBindings.secondarySprint()) {
			return;
		}

		final InputMappings.Input forwardKey =
				Minecraft.getInstance().gameSettings.keyBindForward.getKey();
		info.setReturnValue(
				!RPKeyBindingHandler.KeyBindings.SECONDARY_SPRINT.getKey().equals(forwardKey) &&
						RPKeyBindingHandler.KeyBindings.SECONDARY_SPRINT.isKeyDown()
		);
	}
}
