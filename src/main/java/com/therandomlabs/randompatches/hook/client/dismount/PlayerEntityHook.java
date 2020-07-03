package com.therandomlabs.randompatches.hook.client.dismount;

import net.minecraft.entity.player.PlayerEntity;

public final class PlayerEntityHook {
	public static boolean wantsToStopRiding(PlayerEntity entity) {
		return !entity.world.isRemote && entity.isSneaking();
	}
}
