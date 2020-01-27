package com.therandomlabs.randompatches.hook.client.dismount;

import com.therandomlabs.utils.forge.ForgeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public final class ClientPlayerEntityHook {
	public static final class DismountKeybind {
		public static KeyBinding keybind;
		private static KeyBinding sneakKeybind;

		private DismountKeybind() {}

		public static void register() {
			keybind = new KeyBinding(
					"key.dismount",
					ForgeUtils.IS_DEOBFUSCATED ? GLFW.GLFW_KEY_Z : GLFW.GLFW_KEY_LEFT_SHIFT,
					"key.categories.movement"
			);

			ClientRegistry.registerKeyBinding(keybind);

			sneakKeybind = Minecraft.getInstance().gameSettings.keyBindSneak;
		}

		//So the dismount and sneak key bindings don't show as conflicting in the Controls screen
		public static boolean isDismountAndSneak(KeyBinding binding1, KeyBinding binding2) {
			return (binding1 == keybind && binding2 == sneakKeybind) ||
					(binding1 == sneakKeybind && binding2 == keybind);
		}
	}

	private ClientPlayerEntityHook() {}

	public static boolean shouldDismount() {
		return DismountKeybind.keybind.isKeyDown();
	}
}
