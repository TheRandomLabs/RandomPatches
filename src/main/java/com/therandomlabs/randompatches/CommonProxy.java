package com.therandomlabs.randompatches;

import com.therandomlabs.utils.config.ConfigManager;
import com.therandomlabs.utils.forge.config.ForgeConfig;

public class CommonProxy {
	public void init() {
		ForgeConfig.initialize();
		ConfigManager.register(RPConfig.class);
	}
}
