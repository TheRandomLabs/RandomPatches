package com.therandomlabs.randompatches;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public final class RandomPatches {
	public static final String MODID = "randompatches";
	public static final String NAME = "RandomPatches";
	public static final String VERSION = "@VERSION@";
	public static final String MINECRAFT_VERSIONS = "[1.10,1.13)";

	public static final boolean IS_DEOBFUSCATED =
			(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

	public static final String MC_VERSION = (String) FMLInjectionData.data()[4];
	public static final boolean IS_ONE_TEN = MC_VERSION.startsWith("1.10");
	public static final boolean IS_ONE_ELEVEN = MC_VERSION.startsWith("1.11");
	public static final boolean IS_ONE_TWELVE = MC_VERSION.startsWith("1.12");

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static KeyBinding toggleNarrator;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		if(!event.getSide().equals(Side.CLIENT)) {
			return;
		}

		if(!IS_ONE_TEN) {
			RPConfig.reload();
		}

		if(RPStaticConfig.rpreloadclient) {
			ClientCommandHandler.instance.registerCommand(new CommandRPReload(Side.CLIENT));
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		if(!event.getSide().equals(Side.CLIENT)) {
			return;
		}

		MinecraftForge.EVENT_BUS.register(this);

		if(!RPStaticConfig.narratorKeybind || !RandomPatches.IS_ONE_TWELVE ||
				Loader.isModLoaded("rebind_narrator")) {
			return;
		}

		toggleNarrator = new KeyBinding("key.narrator", new IKeyConflictContext() {
			@Override
			public boolean isActive() {
				return !(Minecraft.getMinecraft().currentScreen instanceof GuiControls);
			}

			@Override
			public boolean conflicts(IKeyConflictContext other) {
				return true;
			}
		}, KeyModifier.CONTROL, Keyboard.KEY_B, "key.categories.misc");

		ClientRegistry.registerKeyBinding(toggleNarrator);
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
