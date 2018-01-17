package com.therandomlabs.randompatches;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public class RPEventHandler {
	@SubscribeEvent
	public void loadWorld(WorldEvent.Load event) {
		System.out.println("Patched read timeout: " + FMLNetworkHandler.READ_TIMEOUT);
		System.out.println("Patched login timeout: " + FMLNetworkHandler.LOGIN_TIMEOUT);
	}
}
