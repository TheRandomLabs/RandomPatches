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

package com.therandomlabs.randompatches.mixin;

import com.therandomlabs.randompatches.RandomPatches;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnimalEntity.class)
public final class AnimalEntityMixin {
	@Redirect(method = "tickMovement", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/World;addParticle" +
					"(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"
	))
	private void addParticle(
			World world, ParticleEffect effect, double x, double y, double z,
			double xOffset, double yOffset, double zOffset
	) {
		if (!world.isClient && RandomPatches.config().misc.bugFixes.fixAnimalBreedingHearts) {
			//addParticle is not implemented in ServerWorld.
			((ServerWorld) world).spawnParticles(
					effect, x, y, z, 1, xOffset, yOffset, zOffset, 0.0
			);
		} else {
			world.addParticle(effect, x, y, z, xOffset, yOffset, zOffset);
		}
	}
}
