package com.therandomlabs.randompatches.mixin;

import com.therandomlabs.randompatches.RPConfig;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ReadTimeoutHandler.class)
public class MixinReadTimeoutHandler {
	@ModifyArg(method = "<init>(I)V", at = @At(
			value = "INVOKE",
			target = "io/netty/handler/timeout/ReadTimeoutHandler.<init>" +
					"(JLjava/util/concurrent/TimeUnit;)V"
	))
	public int getTimeout(int timeout) {
		return timeout == 30 ? RPConfig.Timeouts.readTimeout : timeout;
	}
}
