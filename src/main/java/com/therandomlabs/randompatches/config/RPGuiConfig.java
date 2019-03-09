package com.therandomlabs.randompatches.config;

import com.therandomlabs.randomlib.config.ConfigManager;
import com.therandomlabs.randompatches.RandomPatches;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;

public class RPGuiConfig extends GuiConfig {
	public RPGuiConfig(GuiScreen parentScreen) {
		super(
				parentScreen,
				ConfigManager.getConfigElements(RPConfig.class),
				RandomPatches.MOD_ID,
				RandomPatches.MOD_ID,
				false,
				false,
				ConfigManager.getPathString(RPConfig.class)
		);
	}
}
