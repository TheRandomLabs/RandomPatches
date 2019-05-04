package com.therandomlabs.randompatches.common;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;

public class RPReloadCommand {
	private RPReloadCommand() {}

	public static void register(CommandDispatcher<CommandSource> dispatcher, Dist dist) {
		dispatcher.register(Commands.literal(dist.isClient() ? "rpreloadclient" : "rpreload").
				requires(source -> source.hasPermissionLevel(dist.isClient() ? 0 : 4)).
				executes(context -> execute(context.getSource())));
	}

	public static int execute(CommandSource source) {
		final MinecraftServer server = source.getServer();
		final boolean isServer = server != null && server.isDedicatedServer();

		//RPConfig.Window.setWindowSettings = false;
		//ConfigManager.reloadFromDisk(RPConfig.class);
		//RPConfig.Window.setWindowSettings = true;

		if(isServer) {
			source.sendFeedback(
					new TextComponentString("RandomPatches configuration reloaded!"),
					true
			);
		} else {
			//noinspection NoTranslation
			source.sendFeedback(
					new TextComponentTranslation("commands.rpreloadclient.success"),
					true
			);
		}

		return Command.SINGLE_SUCCESS;
	}
}
