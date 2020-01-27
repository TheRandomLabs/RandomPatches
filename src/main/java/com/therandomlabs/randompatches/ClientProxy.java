package com.therandomlabs.randompatches;

import com.therandomlabs.randompatches.client.RPEndPortalTileEntityRenderer;
import com.therandomlabs.randompatches.hook.client.KeyboardListenerHook;
import com.therandomlabs.randompatches.hook.client.dismount.ClientPlayerEntityHook;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.EndPortalTileEntity;

public class ClientProxy extends CommonProxy {
	@Override
	public void init() {
		super.init();

		ClientPlayerEntityHook.DismountKeybind.register();
		KeyboardListenerHook.ToggleNarratorKeybind.register();

		final RPEndPortalTileEntityRenderer renderer = new RPEndPortalTileEntityRenderer();
		renderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
		TileEntityRendererDispatcher.instance.setSpecialRenderer(
				EndPortalTileEntity.class, renderer
		);
	}
}
