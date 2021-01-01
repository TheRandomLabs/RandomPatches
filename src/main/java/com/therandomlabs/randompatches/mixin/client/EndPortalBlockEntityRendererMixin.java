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

package com.therandomlabs.randompatches.mixin.client;

import java.util.Random;

import com.therandomlabs.randompatches.RandomPatches;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndPortalBlockEntityRenderer.class)
public abstract class EndPortalBlockEntityRendererMixin {
	@Shadow
	@Final
	private static Random RANDOM;

	//CHECKSTYLE IGNORE MethodName FOR NEXT 3 LINES
	@SuppressWarnings("PMD.MethodNamingConventions")
	@Shadow
	protected abstract void method_23085(
			EndPortalBlockEntity blockEntity, Matrix4f model, VertexConsumer vertexConsumer,
			float x1, float x2, float y1, float y2, float z1, float z2, float z3, float z4,
			float r, float g, float b, Direction face
	);

	@Inject(method = "method_23084", at = @At("HEAD"), cancellable = true)
	private void render(
			EndPortalBlockEntity blockEntity, float y, float colorMultiplier, Matrix4f model,
			VertexConsumer vertexConsumer, CallbackInfo info
	) {
		if (!RandomPatches.config().client.bugFixes.fixEndPortalsOnlyRenderingFromAbove) {
			return;
		}

		info.cancel();

		final float r = (RANDOM.nextFloat() * 0.5F + 0.1F) * colorMultiplier;
		final float g = (RANDOM.nextFloat() * 0.5F + 0.4F) * colorMultiplier;
		final float b = (RANDOM.nextFloat() * 0.5F + 0.5F) * colorMultiplier;

		this.method_23085(
				blockEntity, model, vertexConsumer, 0.0F, 1.0F, 0.0F, y, 1.0F, 1.0F, 1.0F, 1.0F,
				r, g, b, Direction.SOUTH
		);
		this.method_23085(
				blockEntity, model, vertexConsumer, 0.0F, 1.0F, y, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
				r, g, b, Direction.NORTH
		);
		this.method_23085(
				blockEntity, model, vertexConsumer, 1.0F, 1.0F, y, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F,
				r, g, b, Direction.EAST
		);
		this.method_23085(
				blockEntity, model, vertexConsumer, 0.0F, 0.0F, 0.0F, y, 0.0F, 1.0F, 1.0F, 0.0F,
				r, g, b, Direction.WEST
		);
		this.method_23085(
				blockEntity, model, vertexConsumer, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F,
				r, g, b, Direction.DOWN
		);
		this.method_23085(
				blockEntity, model, vertexConsumer, 0.0F, 1.0F, y, y, 1.0F, 1.0F, 0.0F, 0.0F,
				r, g, b, Direction.UP
		);
	}

	@SuppressWarnings("UnresolvedMixinReference")
	@Redirect(method = "method_23085", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/entity/EndPortalBlockEntity;" +
					"shouldDrawSide(Lnet/minecraft/util/math/Direction;)Z"
	))
	private boolean shouldDrawSide(EndPortalBlockEntity blockEntity, Direction side) {
		return RandomPatches.config().client.bugFixes.fixEndPortalsOnlyRenderingFromAbove ||
				side == Direction.UP;
	}
}
