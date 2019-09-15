package com.therandomlabs.randomlib.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;

public final class CommandConfigReload {
	@FunctionalInterface
	public interface ConfigReloader {
		void reload(ReloadPhase phase, CommandSource source);
	}

	public enum ReloadPhase {
		PRE,
		POST
	}

	private CommandConfigReload() {}

	public static void client(
			CommandDispatcher<CommandSource> dispatcher, String name, Class<?> configClass
	) {
		client(dispatcher, name, configClass, null);
	}

	public static void client(
			CommandDispatcher<CommandSource> dispatcher, String name, Class<?> configClass,
			ConfigReloader reloader
	) {
		register(dispatcher, name, name, configClass, reloader, Dist.CLIENT, null);
	}

	public static void server(
			CommandDispatcher<CommandSource> dispatcher, String name, String clientName,
			Class<?> configClass
	) {
		server(dispatcher, name, clientName, configClass, null, null);
	}

	public static void server(
			CommandDispatcher<CommandSource> dispatcher, String name, String clientName,
			Class<?> configClass, String successMessage
	) {
		server(dispatcher, name, clientName, configClass, successMessage, null);
	}

	public static void server(
			CommandDispatcher<CommandSource> dispatcher, String name, String clientName,
			Class<?> configClass, String successMessage, ConfigReloader reloader
	) {
		register(
				dispatcher, name, clientName, configClass, reloader, Dist.DEDICATED_SERVER,
				successMessage
		);
	}

	private static void register(
			CommandDispatcher<CommandSource> dispatcher, String name, String clientName,
			Class<?> configClass, ConfigReloader reloader, Dist dist, String successMessage
	) {
		dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal(name).
				requires(source -> source.hasPermissionLevel(dist == Dist.CLIENT ? 0 : 4)).
				executes(context -> execute(
						context.getSource(), name, clientName, configClass, reloader, dist,
						successMessage
				)));
	}

	private static int execute(
			CommandSource source, String name, String clientName, Class<?> configClass,
			ConfigReloader reloader, Dist dist, String successMessage
	) {
		if(reloader != null) {
			reloader.reload(ReloadPhase.PRE, source);
		}

		ConfigManager.reloadFromDisk(configClass);

		if(reloader != null) {
			reloader.reload(ReloadPhase.POST, source);
		}

		final MinecraftServer server = source.getServer();
		final boolean serverSided = server != null && server.isDedicatedServer();

		if(successMessage != null && serverSided) {
			source.sendFeedback(new StringTextComponent(successMessage), true);
		} else {
			final String actualName = serverSided ? name : clientName;
			source.sendFeedback(
					new TranslationTextComponent("commands." + actualName + ".success"), true
			);
		}

		return Command.SINGLE_SUCCESS;
	}
}
