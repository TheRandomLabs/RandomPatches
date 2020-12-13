/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 TheRandomLabs
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
import net.minecraft.network.play.ServerPlayNetHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerPlayNetHandler.class)
public final class ServerPlayNetHandlerPlayerSpeedLimitsMixin {
	@ModifyConstant(method = "processPlayer", constant = @Constant(floatValue = 100.0F))
	private float getDefaultMaxPlayerSpeed(float speed) {
		return RandomPatches.config().playerSpeedLimits.defaultMaxSpeed;
	}

	@ModifyConstant(method = "processPlayer", constant = @Constant(floatValue = 300.0F))
	private float getMaxPlayerElytraSpeed(float speed) {
		return RandomPatches.config().playerSpeedLimits.maxElytraSpeed;
	}

	@ModifyConstant(method = "processVehicleMove", constant = @Constant(doubleValue = 100.0))
	private double getMaxPlayerVehicleSpeed(double speed) {
		return RandomPatches.config().playerSpeedLimits.maxVehicleSpeed;
	}
}
