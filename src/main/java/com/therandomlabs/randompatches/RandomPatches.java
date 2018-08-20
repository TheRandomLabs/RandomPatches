package com.therandomlabs.randompatches;

import com.google.common.eventbus.Subscribe;
import com.therandomlabs.randompatches.core.transformer.MinecraftTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RandomPatches {
	public static final String MODID = "randompatches";
	public static final String NAME = "RandomPatches";
	public static final String VERSION = "@VERSION@";
	public static final String MINECRAFT_VERSIONS = "[1.8,1.13)";

	public static final boolean IS_DEOBFUSCATED =
			(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

	public static final boolean IS_CLIENT = FMLLaunchHandler.side().isClient();

	public static final String MC_VERSION = (String) FMLInjectionData.data()[4];
	public static final boolean IS_ONE_EIGHT = MC_VERSION.startsWith("1.8");
	public static final boolean IS_ONE_NINE = MC_VERSION.startsWith("1.9");
	public static final boolean IS_ONE_TEN = MC_VERSION.startsWith("1.10");
	public static final boolean IS_ONE_TWELVE = MC_VERSION.startsWith("1.12");

	public static final String DEFAULT_WINDOW_TITLE = "Minecraft " + MC_VERSION;

	public static final boolean ITLT_INSTALLED;
	public static final boolean REBIND_NARRATOR_INSTALLED;

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	static {
		boolean flag = false;

		try {
			Class.forName("dk.zlepper.itlt.about.mod");
			flag = true;
		} catch(ClassNotFoundException ignored) {}

		ITLT_INSTALLED = flag;
		flag = false;

		try {
			Class.forName("quaternary.rebindnarrator.RebindNarrator");
			flag = true;
		} catch(ClassNotFoundException ignored) {}

		REBIND_NARRATOR_INSTALLED = flag;
	}

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		if(!IS_CLIENT) {
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
		if(!IS_CLIENT) {
			return;
		}

		MinecraftForge.EVENT_BUS.register(this);

		if(RPStaticConfig.narratorKeybind && IS_ONE_TWELVE && !REBIND_NARRATOR_INSTALLED) {
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
		if(event.getModID().equals(MODID)) {
			RPConfig.reload();
		}
	}
}
