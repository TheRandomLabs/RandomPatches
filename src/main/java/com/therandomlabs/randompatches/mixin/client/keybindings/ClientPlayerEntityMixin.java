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

package com.therandomlabs.randompatches.mixin.client.keybindings;

import com.mojang.authlib.GameProfile;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.client.RPKeyBindingHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {
	//CHECKSTYLE IGNORE MissingJavadocMethod FOR NEXT 1 LINES
	protected ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean shouldDismount() {
		//We let the server handle the dismount logic instead of the client if the dismount
		//key binding is enabled.
		return !RandomPatches.config().client.keyBindings.dismount() && super.shouldDismount();
	}

	@Shadow
	public abstract void onRecipeDisplayed(Recipe<?> recipe);

	@Redirect(method = "tickMovement", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, ordinal = 0, target = "Lnet/minecraft/client/network/ClientPlayerEntity;onGround:Z"))
	private boolean isOnGround(ClientPlayerEntity player) {
		//Minecraft only allows double-tap sprinting when the player is either on the ground
		//or swimming. We combat this by redirecting the on ground check.
		return this.onGround ||
				(RandomPatches.config().client.keyBindings.doubleTapSprintingWhileFlying &&
						player.abilities.flying);
	}

	@Redirect(method = "tickMovement", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/network/ClientPlayerEntity;setSprinting(Z)V",
			ordinal = 0
	))
	private void enableSprintingThroughSecondarySprint(ClientPlayerEntity player, boolean flag) {
		if (!RandomPatches.config().client.keyBindings.secondarySprint() ||
				RPKeyBindingHandler.KeyBindings.SECONDARY_SPRINT.isPressed()) {
			player.setSprinting(true);
		}
	}
}
