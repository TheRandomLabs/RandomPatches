/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 TheRandomLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.therandomlabs.randompatches;

import com.mojang.brigadier.CommandDispatcher;
import com.therandomlabs.utils.config.ConfigManager;
import com.therandomlabs.utils.fabric.config.ConfigReloadCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The main RandomPatches class.
 */
@SuppressWarnings("NullAway")
public final class RandomPatches implements ModInitializer, CommandRegistrationCallback {
	/**
	 * The RandomPatches mod ID.
	 */
	public static final String MOD_ID = "randompatches";

	/**
	 * The RandomPatches logger.
	 */
	public static final Logger logger = LogManager.getLogger(MOD_ID);

	private static final ConfigReloadCommand configReloadCommand = new ConfigReloadCommand(
			"rpreload", "rpreloadclient", RPConfig.class
	).serverSuccessMessage("RandomPatches configuration reloaded!");

	@Override
	public void onInitialize() {
		//FabricConfig.initialize();
		ConfigManager.register(RPConfig.class);

		CommandRegistrationCallback.EVENT.register(this);
	}

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
		if (RPConfig.Misc.rpreload) {
			configReloadCommand.registerServer((CommandDispatcher) dispatcher);
		}
	}
}
