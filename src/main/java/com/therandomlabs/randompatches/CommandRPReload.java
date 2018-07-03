package com.therandomlabs.randompatches;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandRPReload extends CommandBase {
	@Override
	public String getName() {
		return "rpreload";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/" + getName();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
			throws CommandException {
		if(server.isDedicatedServer()) {
			RPStaticConfig.reload();
			notifyCommandListener(sender, this, "RandomPatches configuration reloaded.");
		} else {
			//TODO
		}
	}
}
