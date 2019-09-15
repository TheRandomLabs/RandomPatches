package com.therandomlabs.randompatches;

import com.therandomlabs.randomlib.config.ConfigManager;

public class CommonProxy {
	public void init() {
		ConfigManager.register(RPConfig.class);
	}
}
