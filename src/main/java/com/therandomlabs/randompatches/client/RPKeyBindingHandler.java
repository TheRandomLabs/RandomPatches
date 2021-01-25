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

import java.util.List;

import com.therandomlabs.randompatches.RPConfig;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.mixin.client.keybindings.GameOptionsMixin;
import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.impl.AmecsAPI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.NarratorOptionsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;

/**
 * Contains key binding-related code for RandomPatches.
 */
@Environment(EnvType.CLIENT)
public final class RPKeyBindingHandler {
	/**
	 * Contains key bindings added by RandomPatches.
	 * <p>
	 * A separate class in necessary to prevent certain classes from being loaded too early.
	 */
	public static final class KeyBindings {
		/**
		 * The secondary sprint key binding.
		 */
		public static final KeyBinding SECONDARY_SPRINT = new KeyBinding(
				"key.secondarySprint", GLFW.GLFW_KEY_W, "key.categories.movement"
		);

		/**
		 * The dismount key binding.
		 */
		public static final KeyBinding DISMOUNT = new KeyBinding(
				"key.dismount", GLFW.GLFW_KEY_LEFT_SHIFT, "key.categories.gameplay"
		);

		/**
		 * The narrator toggle key binding.
		 */
		public static final KeyBinding TOGGLE_NARRATOR;

		/**
		 * The pause key binding.
		 */
		public static final KeyBinding PAUSE = new KeyBinding(
				"key.pause", GLFW.GLFW_KEY_ESCAPE, "key.categories.misc"
		);

		/**
		 * The GUI toggle key binding.
		 */
		public static final KeyBinding TOGGLE_GUI = new KeyBinding(
				"key.gui", GLFW.GLFW_KEY_F1, "key.categories.misc"
		);

		/**
		 * The debug key binding.
		 */
		public static final KeyBinding TOGGLE_DEBUG_INFO = new KeyBinding(
				"key.debugInfo", GLFW.GLFW_KEY_F3, "key.categories.misc"
		);

		private static final MinecraftClient mc = MinecraftClient.getInstance();

		static {
			if (FabricLoader.getInstance().isModLoaded(AmecsAPI.MOD_ID)) {
				TOGGLE_NARRATOR = AmecsToggleNarratorKeyBinding.TOGGLE_NARRATOR;
			} else {
				//We have to use "key.randompatches.narrator" instead of "key.narrator" because
				//Amecs insists on putting in the namespace.
				TOGGLE_NARRATOR = new KeyBinding(
						"key.randompatches.narrator", InputUtil.UNKNOWN_KEY.getCode(),
						"key.categories.misc"
				);
			}
		}

		private KeyBindings() {}

		/**
		 * Handles key events. This should only be called by
		 * {@link com.therandomlabs.randompatches.mixin.client.keybindings.KeyboardMixin} and
		 * {@link com.therandomlabs.randompatches.mixin.client.keybindings.MouseMixin}.
		 *
		 * @param key the key.
		 * @param action the action.
		 * @param scanCode the scan code. If this is a mouse event, this should be
		 * {@link Integer#MIN_VALUE}.
		 */
		public static void onKeyEvent(int key, int action, int scanCode) {
			final RPConfig.KeyBindings config = RandomPatches.config().client.keyBindings;

			if (config.toggleNarrator && action != GLFW.GLFW_RELEASE &&
					isNarratorKeyBindingContextActive() &&
					matches(TOGGLE_NARRATOR, key, scanCode)) {
				Option.NARRATOR.cycle(mc.options, 1);

				if (mc.currentScreen instanceof NarratorOptionsScreen) {
					((NarratorOptionsScreen) mc.currentScreen).updateNarratorButtonText();
				}
			}

			if (config.pause && action != GLFW.GLFW_RELEASE && matches(PAUSE, key, scanCode)) {
				if (mc.currentScreen == null) {
					mc.openPauseMenu(InputUtil.isKeyPressed(
							mc.getWindow().getHandle(), GLFW.GLFW_KEY_F3
					));
				} else if (mc.currentScreen instanceof GameMenuScreen) {
					mc.currentScreen.onClose();
				}
			}

			if (mc.currentScreen != null && !mc.currentScreen.passEvents) {
				return;
			}

			if (config.toggleGUI && action != GLFW.GLFW_RELEASE &&
					matches(TOGGLE_GUI, key, scanCode)) {
				mc.options.hudHidden = !mc.options.hudHidden;
			}

			if (config.toggleDebugInfo && action == GLFW.GLFW_RELEASE &&
					matches(TOGGLE_DEBUG_INFO, key, scanCode)) {
				final InputUtil.Key toggleDebugInfoKey =
						((BoundKeyAccessor) TOGGLE_DEBUG_INFO).getBoundKey();
				final SwitchF3StateAccessor accessor = (SwitchF3StateAccessor) mc.keyboard;

				if (toggleDebugInfoKey.getCategory() == InputUtil.Type.KEYSYM &&
						toggleDebugInfoKey.getCode() == GLFW.GLFW_KEY_F3 &&
						accessor.getSwitchF3State()) {
					accessor.setSwitchF3State(false);
				} else {
					mc.options.debugEnabled = !mc.options.debugEnabled;
					mc.options.debugProfilerEnabled =
							mc.options.debugEnabled && Screen.hasShiftDown();
					mc.options.debugTpsEnabled = mc.options.debugEnabled && Screen.hasAltDown();
				}
			}
		}

		private static boolean matches(KeyBinding keyBinding, int key, int scanCode) {
			return scanCode == Integer.MIN_VALUE ?
					keyBinding.matchesMouse(key) : keyBinding.matchesKey(key, scanCode);
		}

		private static boolean isNarratorKeyBindingContextActive() {
			final Screen screen = mc.currentScreen;
			return screen == null || !(screen.getFocused() instanceof TextFieldWidget) ||
					!((TextFieldWidget) screen.getFocused()).isActive();
		}

		private static void register() {
			final RPConfig.KeyBindings config = RandomPatches.config().client.keyBindings;
			final List<String> mixinBlacklist = RandomPatches.config().misc.mixinBlacklist;

			register(SECONDARY_SPRINT, config.secondarySprint());
			register(DISMOUNT, config.dismount());

			if (!mixinBlacklist.contains("Keyboard")) {
				register(TOGGLE_NARRATOR, config.toggleNarrator);
				register(PAUSE, config.pause);
				register(TOGGLE_GUI, config.toggleGUI);
				register(TOGGLE_DEBUG_INFO, config.toggleDebugInfo);
			}
		}

		private static void register(KeyBinding keyBinding, boolean enabled) {
			if (enabled) {
				if (!ArrayUtils.contains(mc.options.keysAll, keyBinding)) {
					((GameOptionsMixin) mc.options).setKeysAll(
							ArrayUtils.add(mc.options.keysAll, keyBinding)
					);
				}
			} else {
				final int index = ArrayUtils.indexOf(mc.options.keysAll, keyBinding);

				if (index != ArrayUtils.INDEX_NOT_FOUND) {
					((GameOptionsMixin) mc.options).setKeysAll(
							ArrayUtils.remove(mc.options.keysAll, index)
					);
				}
			}
		}
	}

	private static final class AmecsToggleNarratorKeyBinding {
		//We put this in a separate class to circumvent a NoClassDefFoundError if Amecs is not
		//installed.
		private static final KeyBinding TOGGLE_NARRATOR = new AmecsKeyBinding(
				new Identifier(RandomPatches.MOD_ID, "narrator"), InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_B, "key.categories.misc", new KeyModifiers().setControl(true)
		);
	}

	private static boolean enabled;

	private RPKeyBindingHandler() {}

	/**
	 * Enables this class's functionality if it has not already been enabled.
	 */
	public static void enable() {
		if (!enabled && !RandomPatches.config().misc.mixinBlacklist.contains("GameOptions")) {
			enabled = true;
			onConfigReload();
		}
	}

	/**
	 * Called by {@link com.therandomlabs.randompatches.RPConfig.KeyBindings} when the RandomPatches
	 * configuration is reloaded.
	 */
	public static void onConfigReload() {
		if (enabled) {
			KeyBindings.register();
		}
	}
}
