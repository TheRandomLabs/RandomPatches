package com.therandomlabs.randompatches.hook.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.ScreenChatOptions;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public final class MinecraftHook {
	public static final class ToggleNarratorKeybind {
		public static final Minecraft mc = Minecraft.getMinecraft();
		public static KeyBinding keybind;

		private ToggleNarratorKeybind() {}

		public static void register() {
			keybind = new KeyBinding("key.narrator", new IKeyConflictContext() {
				@Override
				public boolean isActive() {
					return !(mc.currentScreen instanceof GuiControls);
				}

				@Override
				public boolean conflicts(IKeyConflictContext other) {
					return true;
				}
			}, KeyModifier.CONTROL, Keyboard.KEY_B, "key.categories.misc");

			ClientRegistry.registerKeyBinding(keybind);
		}
	}

	private MinecraftHook() {}

	public static void handleKeypress() {
		if (ToggleNarratorKeybind.keybind == null) {
			return;
		}

		final int eventKey = Keyboard.getEventKey();
		final int key = eventKey == 0 ? Keyboard.getEventCharacter() + 256 : eventKey;

		if (!ToggleNarratorKeybind.keybind.isActiveAndMatches(key)) {
			return;
		}

		final Minecraft mc = ToggleNarratorKeybind.mc;

		mc.gameSettings.setOptionValue(GameSettings.Options.NARRATOR, 1);

		if (mc.currentScreen instanceof ScreenChatOptions) {
			((ScreenChatOptions) mc.currentScreen).updateNarratorButton();
		}
	}
}
