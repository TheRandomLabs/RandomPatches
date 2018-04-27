package com.therandomlabs.randompatches;

import net.minecraft.command.CommandHandler;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.world.DimensionType;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public class RPEventHandler {
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

	public static void registerCommands(CommandHandler handler) {
		if(RPConfig.rpreload) {
			handler.registerCommand(new CommandRPReload(false));
		}
	}

	public static boolean shouldRegisterClient() {
		try {
			Class.forName("net.minecraft.client.gui.GuiScreen");
		} catch(ClassNotFoundException ex) {
			return false;
		}
		return true;
	}
}
