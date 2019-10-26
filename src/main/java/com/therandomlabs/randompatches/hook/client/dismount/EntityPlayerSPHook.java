package com.therandomlabs.randompatches.hook.client.dismount;

import com.therandomlabs.randomlib.TRLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public final class EntityPlayerSPHook {
	public static final class DismountKeybind {
		public static KeyBinding keybind;
		private static KeyBinding sneakKeybind;

		private DismountKeybind() {}

		public static void register() {
			keybind = new KeyBinding(
					"key.dismount",
					TRLUtils.MC_VERSION_NUMBER > 8 ? Keyboard.KEY_LSHIFT : Keyboard.KEY_Z,
					"key.categories.movement"
			);
			ClientRegistry.registerKeyBinding(keybind);
			sneakKeybind = Minecraft.getMinecraft().gameSettings.keyBindSneak;
		}

		//So the dismount and sneak key bindings don't show as conflicting in the Controls screen
		public static boolean isDismountAndSneak(KeyBinding binding1, KeyBinding binding2) {
			return (binding1 == keybind && binding2 == sneakKeybind) ||
					(binding1 == sneakKeybind && binding2 == keybind);
		}
	}

	private EntityPlayerSPHook() {}

	public static boolean shouldDismount() {
		return DismountKeybind.keybind.isKeyDown();
	}
}
