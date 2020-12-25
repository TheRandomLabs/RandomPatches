package com.therandomlabs.randompatches.mixin.client.contributorcapes;

import com.therandomlabs.randompatches.client.RPContributorCapeHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public final class PlayerEntityRendererContributorCapesMixin {
	@Inject(method = "render", at = @At("HEAD"))
	private void preRender(
			AbstractClientPlayerEntity player, float f, float g, MatrixStack stack,
			VertexConsumerProvider provider, int i, CallbackInfo info
	) {
		RPContributorCapeHandler.onPreRenderPlayer(player);
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void postRender(
			AbstractClientPlayerEntity player, float f, float g, MatrixStack stack,
			VertexConsumerProvider provider, int i, CallbackInfo info
	) {
		RPContributorCapeHandler.onPostRenderPlayer(player);
	}
}
