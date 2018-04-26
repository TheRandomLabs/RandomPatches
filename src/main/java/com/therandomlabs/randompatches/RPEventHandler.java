package com.therandomlabs.randompatches;

import net.minecraft.command.CommandHandler;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.world.DimensionType;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public class RPEventHandler {
	private static boolean registeredRpreloadclient;

	@SubscribeEvent
	public void guiOpenEvent(GuiOpenEvent event) {
		if(RPConfig.rpreloadclient && !registeredRpreloadclient) {
			registerRpreloadclient();
			registeredRpreloadclient = true;
		}
	}

	@SubscribeEvent
	public void loadWorld(WorldEvent.Load event) {
		if(!event.getWorld().isRemote &&
				event.getWorld().provider.getDimensionType() == DimensionType.OVERWORLD) {
			final CommandHandler handler =
					((CommandHandler) event.getWorld().getMinecraftServer().getCommandManager());
			registerCommands(handler);
		}

		if((boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
			System.out.println("Patched read timeout: " + FMLNetworkHandler.READ_TIMEOUT);
			System.out.println("Patched login timeout: " + FMLNetworkHandler.LOGIN_TIMEOUT);
		}
	}

	public static void registerRpreloadclient() {
		ClientCommandHandler.instance.registerCommand(new CommandRpreload(true));
	}

	public static void registerCommands(CommandHandler handler) {
		if(RPConfig.rpreload) {
			handler.registerCommand(new CommandRpreload(false));
		}
	}
}
