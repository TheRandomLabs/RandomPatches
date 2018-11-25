package com.therandomlabs.randompatches.common;

import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.config.RPConfig;
import com.therandomlabs.randompatches.config.RPStaticConfig;
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
		RPStaticConfig.doNotSetWindowSettings();

		if(server.isDedicatedServer()) {
			RPStaticConfig.reload();
			notifyCommandListener(sender, this, "RandomPatches configuration reloaded!");
		} else {
			if(RandomPatches.MC_VERSION > 10) {
				RPConfig.reloadFromDisk();
			} else {
				RPStaticConfig.reload();
			}

			sender.sendMessage(new TextComponentTranslation("commands.rpreloadclient.success"));
		}

		RPStaticConfig.doSetWindowSettings();
	}

	@Override
	public int getRequiredPermissionLevel() {
		return isClient ? 0 : 4;
	}
}
