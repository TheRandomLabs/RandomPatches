package com.therandomlabs.randompatches.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.therandomlabs.randompatches.command.RPConfigReloadCommand;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public final class CommandManagerMixin {
	@Shadow
	@Final
	private CommandDispatcher<ServerCommandSource> dispatcher;

	@Inject(method = "<init>", at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/brigadier/CommandDispatcher;findAmbiguities" +
					"(Lcom/mojang/brigadier/AmbiguityConsumer;)V"
	))
	private void registerCommands(
			CommandManager.RegistrationEnvironment environment, CallbackInfo info
	) {
		RPConfigReloadCommand.register(dispatcher);
	}
}
