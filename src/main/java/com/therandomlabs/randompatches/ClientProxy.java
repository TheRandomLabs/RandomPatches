package com.therandomlabs.randompatches;

import com.therandomlabs.randompatches.client.TileEntityRPEndPortalRenderer;
import com.therandomlabs.randompatches.patch.client.KeyboardListenerPatch;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityEndPortal;

public class ClientProxy extends CommonProxy {
	@Override
	public void init() {
		super.init();

		KeyboardListenerPatch.ToggleNarratorKeybind.register();

		final TileEntityRPEndPortalRenderer renderer = new TileEntityRPEndPortalRenderer();
		renderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
		TileEntityRendererDispatcher.instance.setSpecialRenderer(
				TileEntityEndPortal.class, renderer
		);
	}
}
