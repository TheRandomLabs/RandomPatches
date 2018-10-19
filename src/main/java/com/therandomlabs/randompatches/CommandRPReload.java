package com.therandomlabs.randompatches;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;

public class CommandRPReload extends CommandBase {
	private final boolean isClient;

	public CommandRPReload(Side side) {
		isClient = side.isClient();
	}

	@Override
	public String getName() {
		return isClient ? "rpreloadclient" : "rpreload";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return isClient ? 0 : 4;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return isClient ? "commands.rpreloadclient.usage" : "/rpreload";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
			throws CommandException {
		RPEventHandler.setWindowSettings = false;

		if(server.isDedicatedServer()) {
			RPStaticConfig.reload();
			notifyCommandListener(sender, this, "RandomPatches configuration reloaded!");
		} else {
			if(RPStaticConfig.CONFIG_GUI_ENABLED) {
				RPConfig.reloadFromDisk();
			} else {
				RPStaticConfig.reload();
			}

			sender.sendMessage(new TextComponentTranslation("commands.rpreloadclient.success"));
		}

		RPEventHandler.setWindowSettings = true;
	}
}
