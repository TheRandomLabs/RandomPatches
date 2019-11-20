package com.therandomlabs.randompatches.hook;

import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.BlockPos;

public final class PlayerInteractionManagerHook {
	private PlayerInteractionManagerHook() {}

	public static void sendBlockChangePacket(PlayerInteractionManager manager, BlockPos pos) {
		manager.player.connection.sendPacket(new SPacketBlockChange(manager.world, pos));
	}
}
