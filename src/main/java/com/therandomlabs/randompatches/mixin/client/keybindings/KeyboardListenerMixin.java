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
import net.minecraft.client.KeyboardListener;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardListener.class)
public abstract class KeyboardListenerMixin {
	@Unique
	private static final int GLFW_KEY_UNUSED = GLFW.GLFW_KEY_RIGHT_BRACKET + 1;

	@Inject(method = "onKeyEvent", at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/util/InputMappings.getInputByCode(II)" +
					"Lnet/minecraft/client/util/InputMappings$Input;"
	))
	private void onKeyEvent(
			long window, int key, int scanCode, int action, int modifiers, CallbackInfo info
	) {
		RPKeyBindingHandler.RPKeyBindings.onKeyEvent(key, action, scanCode);
	}

	@ModifyConstant(method = "onKeyEvent", constant = @Constant(intValue = GLFW.GLFW_KEY_B))
	private int getToggleNarratorKey(int key) {
		return RandomPatches.config().client.keyBindings.toggleNarrator ? GLFW_KEY_UNUSED : key;
	}

	@ModifyConstant(method = "onKeyEvent", constant = @Constant(intValue = GLFW.GLFW_KEY_ESCAPE))
	private int getPauseKey(int key) {
		return RandomPatches.config().client.keyBindings.pause ? GLFW_KEY_UNUSED : key;
	}

	@ModifyConstant(method = "onKeyEvent", constant = @Constant(intValue = GLFW.GLFW_KEY_F1))
	private int getToggleGUIKey(int key) {
		return RandomPatches.config().client.keyBindings.toggleGUI ? GLFW_KEY_UNUSED : key;
	}

	@ModifyConstant(
			method = "onKeyEvent",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "net/minecraft/client/util/InputMappings.getInputByCode(II)" +
									"Lnet/minecraft/client/util/InputMappings$Input;"
					)
			),
			constant = @Constant(intValue = GLFW.GLFW_KEY_F3, ordinal = 0)
	)
	private int getToggleDebugInfoKey(int key) {
		return RandomPatches.config().client.keyBindings.toggleDebugInfo() ? GLFW_KEY_UNUSED : key;
	}
}
