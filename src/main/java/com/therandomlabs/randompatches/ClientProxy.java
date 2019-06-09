package com.therandomlabs.randompatches;

import com.therandomlabs.randompatches.client.RPEndPortalTileEntityRenderer;
import com.therandomlabs.randompatches.patch.client.KeyboardListenerPatch;
import com.therandomlabs.randompatches.patch.client.dismount.ClientPlayerEntityPatch;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.EndPortalTileEntity;

public class ClientProxy extends CommonProxy {
	@Override
	public void init() {
		super.init();

		ClientPlayerEntityPatch.DismountKeybind.register();
		KeyboardListenerPatch.ToggleNarratorKeybind.register();

		final RPEndPortalTileEntityRenderer renderer = new RPEndPortalTileEntityRenderer();
		renderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
		TileEntityRendererDispatcher.instance.setSpecialRenderer(
				EndPortalTileEntity.class, renderer
		);
	}
}
