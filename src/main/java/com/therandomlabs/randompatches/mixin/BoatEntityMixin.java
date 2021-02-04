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
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BoatEntity.class)
public final class BoatEntityMixin {
	@Shadow
	private BoatEntity.Status status;

	@Shadow
	private float outOfControlTicks;

	@Inject(method = "tick", at = @At("TAIL"))
	private void tick(CallbackInfo info) {
		if (status == BoatEntity.Status.UNDER_FLOWING_WATER) {
			final Vector3d motion = ((Entity) (Object) this).getMotion();
			((Entity) (Object) this).setMotion(
					motion.x,
					motion.y + 0.0007 + RandomPatches.config().misc.boatBuoyancyUnderFlowingWater,
					motion.z
			);
		}
	}

	@ModifyConstant(
			method = {"tick", "processInitialInteract"}, constant = @Constant(floatValue = 60.0F)
	)
	private float getUnderwaterBoatPassengerEjectionDelay(float delay) {
		final int newDelay = RandomPatches.config().misc.underwaterBoatPassengerEjectionDelayTicks;
		return newDelay == -1 ? Float.MAX_VALUE : newDelay;
	}

	@Redirect(method = "fall", at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/entity/vehicle/BoatEntity;" +
					"location:Lnet/minecraft/entity/vehicle/BoatEntity$Location;"
	))
	private BoatEntity.Location getLocation(BoatEntity boat) {
		return RandomPatches.config().misc.bugFixes.fixBoatFallDamage ?
				BoatEntity.Location.ON_LAND : location;
	}
}
