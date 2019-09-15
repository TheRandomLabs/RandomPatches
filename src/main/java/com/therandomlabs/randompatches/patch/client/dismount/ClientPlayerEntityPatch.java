package com.therandomlabs.randompatches.patch.client.dismount;

import com.therandomlabs.randomlib.TRLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public final class ClientPlayerEntityPatch {
	public static final class DismountKeybind {
		public static KeyBinding keybind;
		private static KeyBinding sneakKeybind;

		private DismountKeybind() {}

		public static void register() {
			keybind = new KeyBinding(
					"key.dismount", KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM,
					TRLUtils.IS_DEOBFUSCATED ? GLFW.GLFW_KEY_Z : GLFW.GLFW_KEY_LEFT_SHIFT,
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

	private ClientPlayerEntityPatch() {}

	public static boolean shouldDismount() {
		return DismountKeybind.keybind.isKeyDown();
	}
}
