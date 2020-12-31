package com.therandomlabs.randompatches.mixin.timeouts;

import com.therandomlabs.randompatches.RandomPatches;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = {
		"net/minecraft/network/ClientConnection$1",
		"net/minecraft/server/ServerNetworkIo$1"
})
public final class ChannelInitializerMixin {
	@ModifyArg(method = "initChannel(Lio/netty/channel/Channel;)V", at = @At(
			value = "INVOKE",
			target = "io/netty/handler/timeout/ReadTimeoutHandler.<init>(I)V"
	))
	private int getReadTimeout(int timeout) {
		return RandomPatches.config().connectionTimeouts.readTimeoutSeconds;
	}
}
