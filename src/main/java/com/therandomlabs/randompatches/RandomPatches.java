package com.therandomlabs.randompatches;

import com.google.common.eventbus.Subscribe;
import com.therandomlabs.randompatches.client.TileEntityEndPortalRenderer;
import com.therandomlabs.randompatches.common.CommandRPReload;
import com.therandomlabs.randompatches.config.RPConfig;
import com.therandomlabs.randompatches.config.RPStaticConfig;
import com.therandomlabs.randompatches.core.patch.MinecraftPatch;
import com.therandomlabs.randompatches.util.RPUtils;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.tileentity.TileEntityEndPortal;
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
	public static final String MOD_ID = "randompatches";
	public static final String NAME = "RandomPatches";
	public static final String VERSION = "@VERSION@";
	public static final String MINECRAFT_VERSIONS = "[1.8,1.13)";
	public static final String CERTIFICATE_FINGERPRINT = "@FINGERPRINT@";

	public static final int SORTING_INDEX = Integer.MAX_VALUE - 10000;

	public static final boolean IS_DEOBFUSCATED =
			(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

	public static final boolean IS_CLIENT = FMLLaunchHandler.side().isClient();
	public static final String MC_VERSION_STRING = (String) FMLInjectionData.data()[4];
	public static final int MC_VERSION = Integer.parseInt(MC_VERSION_STRING.split("\\.")[1]);

	public static final String DEFAULT_WINDOW_TITLE = "Minecraft " + MC_VERSION_STRING;

	public static final boolean SPONGEFORGE_INSTALLED = detect("org.spongepowered.mod.SpongeMod");
	public static final boolean ITLT_INSTALLED = detect("dk.zlepper.itlt.about.mod");
	public static final boolean REBIND_NARRATOR_INSTALLED =
			detect("quaternary.rebindnarrator.RebindNarrator");
	public static final boolean VANILLAFIX_INSTALLED = detect("org.dimdev.vanillafix.VanillaFix");
	public static final boolean VERTICAL_END_PORTALS_INSTALLED =
			detect("com.therandomlabs.verticalendportals.VerticalEndPortals");

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		if(!IS_CLIENT) {
			return;
		}

		if(MC_VERSION > 10) {
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

		if(MC_VERSION > 10) {
			MinecraftForge.EVENT_BUS.register(this);
		}

		if(RPStaticConfig.isNarratorKeybindEnabled()) {
			MinecraftPatch.ToggleNarratorKeybind.register();
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
		if(event.getModID().equals(MOD_ID)) {
			RPConfig.reload();
		}
	}

	public static void containerInit() {
		if(!RPUtils.hasFingerprint(RandomPatches.class, CERTIFICATE_FINGERPRINT)) {
			LOGGER.error("Invalid fingerprint detected!");
		}

		if(RPStaticConfig.isEndPortalTweaksEnabled() && IS_CLIENT) {
			final TileEntityEndPortalRenderer renderer = new TileEntityEndPortalRenderer();
			renderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
			TileEntityRendererDispatcher.instance.renderers.put(
					TileEntityEndPortal.class, renderer
			);
		}

		RPStaticConfig.setWindowSettings();
	}


	private static boolean detect(String className) {
		try {
			Class.forName(className);
		} catch(ClassNotFoundException ex) {
			return false;
		}

		return true;
	}
}
