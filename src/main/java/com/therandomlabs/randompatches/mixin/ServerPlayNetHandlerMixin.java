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
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetHandler.class)
public final class ServerPlayNetHandlerMixin {
	@Shadow
	private long keepAliveTime;

	@Redirect(method = "tick", at = @At(
			value = "INVOKE",
			target = "net/minecraft/network/play/ServerPlayNetHandler.disconnect" +
					"(Lnet/minecraft/util/text/ITextComponent;)V",
			ordinal = 2
	))
	public void disconnect(ServerPlayNetHandler handler, ITextComponent reason) {
		final long readTimeoutMillis =
				RandomPatches.config().connectionTimeouts.readTimeoutSeconds * 1000L;

		if (Util.milliTime() - keepAliveTime >= readTimeoutMillis) {
			handler.disconnect(reason);
		}
	}

	@ModifyConstant(method = "tick", constant = @Constant(longValue = 15000L))
	public long getKeepAlivePacketInterval(long interval) {
		return RandomPatches.config().connectionTimeouts.keepAlivePacketIntervalSeconds * 1000L;
	}

	@ModifyConstant(method = "processPlayer", constant = @Constant(floatValue = 100.0F))
	public float getMaxPlayerSpeed(float speed) {
		return RandomPatches.config().playerSpeedLimits.maxSpeed;
	}

	@ModifyConstant(method = "processPlayer", constant = @Constant(floatValue = 300.0F))
	public float getMaxPlayerElytraSpeed(float speed) {
		return RandomPatches.config().playerSpeedLimits.maxElytraSpeed;
	}

	@ModifyConstant(method = "processVehicleMove", constant = @Constant(doubleValue = 100.0))
	public double getMaxPlayerVehicleSpeed(double speed) {
		return RandomPatches.config().playerSpeedLimits.maxVehicleSpeed;
	}
}
