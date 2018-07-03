package com.therandomlabs.randompatches.event;

import com.therandomlabs.randompatches.CommandRPReload;
import com.therandomlabs.randompatches.RPStaticConfig;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandManager;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RPEventHandler {
	@SubscribeEvent
	public void loadWorld(WorldEvent.Load event) {
		final World world = event.getWorld();

		if(world.isRemote || world.provider.getDimensionType() != DimensionType.OVERWORLD) {
			return;
		}

		final ICommandManager manager = world.getMinecraftServer().getCommandManager();
		registerCommands((CommandHandler) manager);
	}

	public static void registerCommands(CommandHandler handler) {
		if(RPStaticConfig.rpreload) {
			handler.registerCommand(new CommandRPReload());
		}
	}

	public static void initialize() {
		RPStaticConfig.reload();
	}
}
