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
import com.therandomlabs.randompatches.client.SwitchF3StateAccessor;
import net.minecraft.client.Keyboard;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public final class KeyboardMixin implements SwitchF3StateAccessor {
	@Shadow
	private boolean switchF3State;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getSwitchF3State() {
		return switchF3State;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSwitchF3State(boolean state) {
		switchF3State = state;
	}

	@Inject(method = "onKey", at = @At(
			value = "INVOKE",
			shift = At.Shift.BY,
			by = 2,
			target = "Lnet/minecraft/client/gui/screen/options/NarratorOptionsScreen;" +
					"updateNarratorButtonText()V"
	))
	private void onKeyEvent(
			long window, int key, int scanCode, int action, int modifiers, CallbackInfo info
	) {
		RPKeyBindingHandler.KeyBindings.onKeyEvent(key, action, scanCode);
	}

	@ModifyConstant(method = "onKey", constant = @Constant(intValue = GLFW.GLFW_KEY_B))
	private int getToggleNarratorKey(int key) {
		return RandomPatches.config().client.keyBindings.toggleNarrator ? -1 : key;
	}

	@ModifyConstant(method = "onKey", constant = @Constant(intValue = GLFW.GLFW_KEY_ESCAPE))
	private int getPauseKey(int key) {
		return RandomPatches.config().client.keyBindings.pause ? -1 : key;
	}

	@ModifyConstant(method = "onKey", constant = @Constant(intValue = GLFW.GLFW_KEY_F1))
	private int getToggleGUIKey(int key) {
		return RandomPatches.config().client.keyBindings.toggleGUI ? -1 : key;
	}

	@ModifyConstant(
			method = "onKey",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/client/util/InputUtil;fromKeyCode(II)" +
									"Lnet/minecraft/client/util/InputUtil$Key;"
					)
			),
			constant = @Constant(intValue = GLFW.GLFW_KEY_F3, ordinal = 0)
	)
	private int getToggleDebugInfoKey(int key) {
		return RandomPatches.config().client.keyBindings.toggleDebugInfo ? -1 : key;
	}
}
