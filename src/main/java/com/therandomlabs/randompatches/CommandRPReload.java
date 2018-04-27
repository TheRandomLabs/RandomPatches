package com.therandomlabs.randompatches;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandRPReload extends CommandBase {
	private final boolean client;

	public CommandRPReload(boolean client) {
		this.client = client;
	}

	@Override
	public String getName() {
		return client ? "rpreloadclient" : "rpreload";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return client ? 0 : 4;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/" + getName();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
			throws CommandException {
		RPConfig.reload();
		if(client) {
			sender.sendMessage(
					new TextComponentString("Successfully reloaded RandomPatches config!"));
		} else {
			notifyCommandListener(sender, this, "Successfully reloaded RandomPatches config!");
		}
	}
}
