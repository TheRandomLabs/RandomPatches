package com.therandomlabs.randompatches;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

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
		return "/rpreload";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
			throws CommandException {
		if(server.isDedicatedServer() || RandomPatches.IS_ONE_TEN) {
			RPStaticConfig.reload();
			notifyCommandListener(sender, this, "RandomPatches configuration reloaded!");
		} else {
			sender.sendMessage(new TextComponentString(TextFormatting.RED +
					"Use the provided GUI to modify the client configuration."));
		}
	}
}
