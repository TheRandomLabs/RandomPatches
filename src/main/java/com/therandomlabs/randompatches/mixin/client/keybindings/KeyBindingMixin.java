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

import java.util.Map;

import com.therandomlabs.randompatches.RPConfig;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.client.BoundKeyAccessor;
import com.therandomlabs.randompatches.client.RPKeyBindingHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public final class KeyBindingMixin implements BoundKeyAccessor {
	@Shadow
	@Final
	private static Map<String, KeyBinding> keysById;

	@Shadow
	private InputUtil.Key boundKey;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputUtil.Key getBoundKey() {
		return boundKey;
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "isPressed", at = @At("RETURN"), cancellable = true)
	private void isPressed(CallbackInfoReturnable<Boolean> info) {
		final KeyBinding sprint = MinecraftClient.getInstance().options.keySprint;

		if ((Object) this != sprint || info.getReturnValue() ||
				!RandomPatches.config().client.keyBindings.secondarySprint()) {
			return;
		}

		final InputUtil.Key forwardKey =
				((BoundKeyAccessor) MinecraftClient.getInstance().options.keyForward).getBoundKey();
		final InputUtil.Key secondarySprintKey =
				((BoundKeyAccessor) RPKeyBindingHandler.KeyBindings.SECONDARY_SPRINT).getBoundKey();
		info.setReturnValue(
				!secondarySprintKey.equals(forwardKey) &&
						RPKeyBindingHandler.KeyBindings.SECONDARY_SPRINT.isPressed()
		);
	}

	@SuppressWarnings({"ConstantConditions", "PMD.CompareObjectsWithEquals"})
	@Inject(method = "equals", at = @At("HEAD"), cancellable = true)
	private void conflicts(KeyBinding keyBinding, CallbackInfoReturnable<Boolean> info) {
		final RPConfig.KeyBindings config = RandomPatches.config().client.keyBindings;

		if (config.secondarySprint()) {
			final KeyBinding forward = MinecraftClient.getInstance().options.keyForward;
			final KeyBinding secondarySprint = RPKeyBindingHandler.KeyBindings.SECONDARY_SPRINT;

			if (((Object) this == forward && keyBinding == secondarySprint) ||
					((Object) this == secondarySprint && keyBinding == forward)) {
				info.setReturnValue(false);
			}
		}

		if (config.dismount()) {
			final KeyBinding sneak = MinecraftClient.getInstance().options.keySneak;
			final KeyBinding dismount = RPKeyBindingHandler.KeyBindings.DISMOUNT;

			if (((Object) this == sneak && keyBinding == dismount) ||
					((Object) this == dismount && keyBinding == sneak)) {
				info.setReturnValue(false);
			}
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
	private static void setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo info) {
		// Amecs implements the same fix. Performing it twice breaks sticky key bindings
		if (FabricLoader.getInstance().isModLoaded("amecsapi")) {
			return;
		}

		//In vanilla, setPressed is only called on the most recently registered KeyBinding.
		keysById.values().stream().filter(keyBinding -> key.equals(
				((KeyBindingMixin) (Object) keyBinding).getBoundKey())
		).forEach(keyBinding -> keyBinding.setPressed(pressed));
		info.cancel();
	}
}
