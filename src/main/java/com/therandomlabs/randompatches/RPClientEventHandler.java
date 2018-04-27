package com.therandomlabs.randompatches;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RPClientEventHandler {
	private static boolean registeredRpreloadclient;

	@SubscribeEvent
	public void guiOpenEvent(GuiOpenEvent event) {
		if(RPConfig.rpreloadclient && !registeredRpreloadclient) {
			registerRpreloadclient();
			registeredRpreloadclient = true;
		}
	}

	public static void registerRpreloadclient() {
		ClientCommandHandler.instance.registerCommand(new CommandRPReload(true));
	}
}
