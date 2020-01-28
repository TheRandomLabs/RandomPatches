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

import com.therandomlabs.randompatches.RPConfig;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetworkHandler.class)
public final class MixinServerLoginNetworkHandler {
	@Shadow
	private int loginTicks;

	@Inject(method = "tick", at = @At("TAIL"))
	public void tick(CallbackInfo info) {
		//If the login timeout is below 600 ticks, we have to check again at the end of
		//the tick method.
		if (loginTicks >= RPConfig.Timeouts.loginTimeout) {
			((ServerLoginNetworkHandler) (Object) this).disconnect(
					new TranslatableText("multiplayer.disconnect.slow_login")
			);
		}
	}

	@Redirect(method = "tick", at = @At(
			value = "INVOKE",
			target = "net/minecraft/server/network/ServerLoginNetworkHandler.disconnect" +
					"(Lnet/minecraft/text/Text;)V"
	))
	public void disconnect(ServerLoginNetworkHandler handler, Text reason) {
		if (loginTicks >= RPConfig.Timeouts.loginTimeout) {
			handler.disconnect(reason);
		}
	}
}
