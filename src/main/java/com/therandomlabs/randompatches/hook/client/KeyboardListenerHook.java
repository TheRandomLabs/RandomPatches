package com.therandomlabs.randompatches.hook.client;

import net.minecraft.client.AbstractOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.ChatOptionsScreen;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public final class KeyboardListenerHook {
	public static final class ToggleNarratorKeybind {
		private static final Minecraft mc = Minecraft.getInstance();
		private static KeyBinding keybind;

		private ToggleNarratorKeybind() {}

		public static void register() {
			keybind = new KeyBinding(
					"key.narrator", KeyConflictContext.INSTANCE, KeyModifier.CONTROL,
					InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.categories.misc"
			);

			ClientRegistry.registerKeyBinding(keybind);
		}
	}

	private static class KeyConflictContext implements IKeyConflictContext {
		public static final KeyConflictContext INSTANCE = new KeyConflictContext();

		@Override
		public boolean isActive() {
			final Screen screen = ToggleNarratorKeybind.mc.currentScreen;

			if (screen == null) {
				return true;
			}

			if (screen instanceof ControlsScreen) {
				return false;
			}

			final IGuiEventListener focused = screen.func_241217_q_();

			return !(focused instanceof TextFieldWidget) ||
					!((TextFieldWidget) focused).canWrite();
		}

		@Override
		public boolean conflicts(IKeyConflictContext other) {
			return true;
		}
	}

	private KeyboardListenerHook() {}

	public static void handleKeypress(int key, int scanCode) {
		final KeyBinding keybind = ToggleNarratorKeybind.keybind;

		if (keybind == null || !keybind.matchesKey(key, scanCode) ||
				!KeyConflictContext.INSTANCE.isActive() ||
				!keybind.getKeyModifier().isActive(KeyConflictContext.INSTANCE)) {
			return;
		}

		final Minecraft mc = ToggleNarratorKeybind.mc;

		AbstractOption.NARRATOR.setValueIndex(mc.gameSettings, 1);

		if (mc.currentScreen instanceof ChatOptionsScreen) {
			((ChatOptionsScreen) mc.currentScreen).updateNarratorButton();
		} else if (mc.currentScreen instanceof AccessibilityScreen) {
			((AccessibilityScreen) mc.currentScreen).func_212985_a();
		}
	}
}
