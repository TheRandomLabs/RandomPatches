package com.therandomlabs.randompatches.config;

import com.therandomlabs.randomlib.config.TRLGuiConfigFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

public class RPGuiConfigFactory extends TRLGuiConfigFactory {
	@Override
	public Class<? extends GuiConfig> mainConfigGuiClass() {
		return RPGuiConfig.class;
	}
}
