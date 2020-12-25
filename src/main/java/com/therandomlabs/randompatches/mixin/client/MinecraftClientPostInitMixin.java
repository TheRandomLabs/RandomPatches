package com.therandomlabs.randompatches.mixin.client;

import com.therandomlabs.randompatches.RandomPatches;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public final class MinecraftClientPostInitMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(RunArgs args, CallbackInfo info) {
		RandomPatches.postClientInit();
	}
}
