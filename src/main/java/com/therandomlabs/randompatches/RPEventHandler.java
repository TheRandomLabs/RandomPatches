package com.therandomlabs.randompatches;

import com.google.common.eventbus.Subscribe;
import com.therandomlabs.randompatches.core.transformer.MinecraftTransformer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.Display;

public final class RPEventHandler {
	static boolean setWindowSettings = true;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		if(!RandomPatches.IS_CLIENT) {
			return;
		}

		if(RPStaticConfig.CONFIG_GUI_ENABLED) {
			RPConfig.reload();
		}

		if(RPStaticConfig.rpreloadclient) {
			ClientCommandHandler.instance.registerCommand(new CommandRPReload(Side.CLIENT));
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		if(!RandomPatches.IS_CLIENT) {
			return;
		}

		if(RPStaticConfig.CONFIG_GUI_ENABLED) {
			MinecraftForge.EVENT_BUS.register(this);
		}

		if(RPStaticConfig.isNarratorKeybindEnabled()) {
			MinecraftTransformer.registerKeybind();
		}
	}

	@Subscribe
	public void serverStarting(FMLServerStartingEvent event) {
		if(RPStaticConfig.rpreload) {
			event.registerServerCommand(new CommandRPReload(Side.SERVER));
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(event.getModID().equals(RandomPatches.MODID)) {
			RPConfig.reload();
		}
	}

	public static void containerInit() {
		if(!RPUtils.hasFingerprint(RPEventHandler.class, RandomPatches.CERTIFICATE_FINGERPRINT)) {
			RandomPatches.LOGGER.error("Invalid fingerprint detected!");
		}

		if(setWindowSettings && RandomPatches.IS_CLIENT && !RandomPatches.ITLT_INSTALLED) {
			if(!RPStaticConfig.icon16.isEmpty()) {
				//If icon16 is empty, WindowIconHandler loads the Minecraft class too early
				WindowIconHandler.setWindowIcon();
			}

			Display.setTitle(RPStaticConfig.title);
		}
	}
}
