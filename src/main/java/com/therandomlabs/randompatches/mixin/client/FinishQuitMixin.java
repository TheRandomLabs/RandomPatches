package com.therandomlabs.randompatches.mixin.client;

import com.minenash.seamless_loading_screen.FinishQuit;
import com.therandomlabs.randompatches.client.DisconnectHandler;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FinishQuit.class)
public final class FinishQuitMixin {
	@Redirect(method = "render", at = @At(
			value = "INVOKE",
			target = "Lcom/minenash/seamless_loading_screen/FinishQuit;" +
					"quit(Lnet/minecraft/client/MinecraftClient;)V"
	))
	private void quit(FinishQuit finishQuit, MinecraftClient mc) {
		DisconnectHandler.disconnect();
	}
}
