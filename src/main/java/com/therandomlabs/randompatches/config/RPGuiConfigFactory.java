package com.therandomlabs.randompatches.config;

import com.therandomlabs.randomlib.config.TRLGuiConfigFactory;
import net.minecraft.client.gui.GuiScreen;

public class RPGuiConfigFactory extends TRLGuiConfigFactory {
	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new RPGuiConfig(parentScreen);
	}
}
