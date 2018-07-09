package com.therandomlabs.randompatches;

import java.net.MalformedURLException;
import java.net.URL;
import com.google.common.eventbus.Subscribe;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RandomPatches {
	public static final String MODID = "randompatches";
	public static final String NAME = "RandomPatches";
	public static final String VERSION = "@VERSION@";
	public static final String LOGO_FILE = "assets/" + MODID + "/logo.png";
	public static final String UPDATE_JSON =
			"https://raw.githubusercontent.com/TheRandomLabs/RandomPatches/misc/versions.json";
	public static final URL UPDATE_URL;
	public static final String MINECRAFT_VERSIONS = "[1.10,1.13)";

	public static final boolean IS_DEOBFUSCATED =
			(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

	public static final String MC_VERSION = (String) FMLInjectionData.data()[4];
	public static final boolean IS_ONE_TEN = MC_VERSION.startsWith("1.10");
	public static final boolean IS_ONE_ELEVEN = MC_VERSION.startsWith("1.11");

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	static {
		URL url = null;

		try {
			url = new URL(UPDATE_JSON);
		} catch(MalformedURLException ignored) {}

		UPDATE_URL = url;
	}

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		if(RPStaticConfig.rpreloadclient && event.getSide().equals(Side.CLIENT)) {
			if(!IS_ONE_TEN) {
				RPConfig.reload();
			}

			ClientCommandHandler.instance.registerCommand(new CommandRPReload(Side.CLIENT));
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		if(event.getSide().equals(Side.CLIENT)) {
			MinecraftForge.EVENT_BUS.register(this);
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
		if(event.getModID().equals(MODID)) {
			RPConfig.reload();
		}
	}
}
