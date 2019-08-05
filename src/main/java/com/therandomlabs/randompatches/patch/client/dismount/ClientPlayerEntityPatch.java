package com.therandomlabs.randompatches.patch.client.dismount;

import com.therandomlabs.randomlib.TRLUtils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public final class ClientPlayerEntityPatch {
	public static final class DismountKeybind {
		public static KeyBinding keybind;

		private DismountKeybind() {}

		public static void register() {
			keybind = new KeyBinding(
					"key.dismount",
					TRLUtils.IS_DEOBFUSCATED ? GLFW.GLFW_KEY_Z : GLFW.GLFW_KEY_LEFT_SHIFT,
					"key.categories.movement"
			);

			ClientRegistry.registerKeyBinding(keybind);
		}
	}

	private ClientPlayerEntityPatch() {}

	public static boolean shouldDismount() {
		return DismountKeybind.keybind.isKeyDown();
	}
}
