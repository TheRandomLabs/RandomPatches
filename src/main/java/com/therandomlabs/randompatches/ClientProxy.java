package com.therandomlabs.randompatches;

import com.therandomlabs.randompatches.hook.client.KeyboardListenerHook;
import com.therandomlabs.randompatches.hook.client.dismount.ClientPlayerEntityHook;

public class ClientProxy extends CommonProxy {
	@Override
	public void init() {
		super.init();
		ClientPlayerEntityHook.DismountKeybind.register();
		KeyboardListenerHook.ToggleNarratorKeybind.register();
	}
}
