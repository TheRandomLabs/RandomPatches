package com.therandomlabs.randompatches;

import com.therandomlabs.randompatches.client.RPTileEntityEndPortalRenderer;
import com.therandomlabs.randompatches.patch.client.KeyboardListenerPatch;
import com.therandomlabs.randompatches.patch.client.dismount.EntityPlayerSPPatch;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityEndPortal;

public class ClientProxy extends CommonProxy {
	@Override
	public void init() {
		super.init();

		EntityPlayerSPPatch.DismountKeybind.register();
		KeyboardListenerPatch.ToggleNarratorKeybind.register();

		final RPTileEntityEndPortalRenderer renderer = new RPTileEntityEndPortalRenderer();
		renderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
		TileEntityRendererDispatcher.instance.setSpecialRenderer(
				TileEntityEndPortal.class, renderer
		);
	}
}
