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

package com.therandomlabs.randompatches.mixin.timeouts;

import com.therandomlabs.randompatches.RandomPatches;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public final class ServerPlayNetworkHandlerKeepAliveMixin {
	@Shadow
	private long lastKeepAliveTime;

	@Redirect(method = "tick", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;disconnect" +
					"(Lnet/minecraft/text/Text;)V",
			ordinal = 2
	))
	private void disconnect(ServerPlayNetworkHandler handler, Text reason) {
		final long keepAliveTimeoutMillis =
				RandomPatches.config().connectionTimeouts.keepAliveTimeoutSeconds * 1000L;

		if (Util.getMeasuringTimeMs() - lastKeepAliveTime >= keepAliveTimeoutMillis) {
			handler.disconnect(reason);
		}
	}

	@ModifyConstant(method = "tick", constant = {
			@Constant(longValue = 15000L),
			//CraftBukkit changes it to 25000.
			@Constant(longValue = 25000L)
	})
	private long getKeepAlivePacketInterval(long interval) {
		return RandomPatches.config().connectionTimeouts.keepAlivePacketIntervalSeconds * 1000L;
	}
}
