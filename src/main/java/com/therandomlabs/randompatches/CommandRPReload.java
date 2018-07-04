package com.therandomlabs.randompatches;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

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
		return RandomPatches.localize("commands.rpreload.usage");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
			throws CommandException {
		if(server.isDedicatedServer() || RandomPatches.IS_ONE_TEN) {
			RPStaticConfig.reload();
			notifyCommandListener(sender, this,
					RandomPatches.localize("commands.rpreload.success"));
		} else {
			sender.sendMessage(new TextComponentString(
					RandomPatches.localize("commands.rpreload.useConfigGUI")));
		}
	}
}
