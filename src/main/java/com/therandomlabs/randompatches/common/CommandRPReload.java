package com.therandomlabs.randompatches.common;

import com.therandomlabs.randomlib.config.ConfigManager;
import com.therandomlabs.randompatches.config.RPConfig;
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
	public String getUsage(ICommandSender sender) {
		return isClient ? "commands.rpreloadclient.usage" : "/rpreload";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
			throws CommandException {
		RPConfig.Client.Window.setWindowSettings = false;
		ConfigManager.reloadFromDisk(RPConfig.class);

		if(server != null && server.isDedicatedServer()) {
			notifyCommandListener(sender, this, "RandomPatches configuration reloaded!");
		} else {
			sender.sendMessage(new TextComponentTranslation("commands.rpreloadclient.success"));
		}

		RPConfig.Client.Window.setWindowSettings = true;
	}

	@Override
	public int getRequiredPermissionLevel() {
		return isClient ? 0 : 4;
	}
}
