package com.therandomlabs.randompatches.mixin.client.contributorcapes;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractClientPlayerEntity.class)
public interface AbstractClientPlayerEntityMixin {
	@Invoker
	PlayerListEntry invokeGetPlayerListEntry();

	@Accessor
	void setCachedScoreboardEntry(PlayerListEntry entry);
}
