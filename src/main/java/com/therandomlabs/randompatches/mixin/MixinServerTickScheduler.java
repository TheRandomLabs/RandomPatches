package com.therandomlabs.randompatches.mixin;

import java.util.HashSet;

import com.therandomlabs.randompatches.RPConfig;
import com.therandomlabs.randompatches.ScheduledTickHashSet;
import net.minecraft.server.world.ServerTickScheduler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("rawtypes")
@Mixin(ServerTickScheduler.class)
public final class MixinServerTickScheduler {
	@Redirect(method = "<init>", at = @At(
			value = "INVOKE",
			target = "com/google/common/collect/Sets.newHashSet()Ljava/util/HashSet;"
	))
	public HashSet newScheduledTickHashSet() {
		return RPConfig.Misc.fixTickNextTickListOutOfSynch ?
				new ScheduledTickHashSet() : new HashSet();
	}
}
