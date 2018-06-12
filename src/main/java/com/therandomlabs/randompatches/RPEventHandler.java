package com.therandomlabs.randompatches;

import net.minecraft.command.CommandHandler;
import net.minecraft.world.DimensionType;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RPEventHandler {
	@SubscribeEvent
	public void loadWorld(WorldEvent.Load event) {
		if(!event.getWorld().isRemote &&
				event.getWorld().provider.getDimensionType() == DimensionType.OVERWORLD) {
			final CommandHandler handler =
					((CommandHandler) event.getWorld().getMinecraftServer().getCommandManager());
			registerCommands(handler);
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
