package com.therandomlabs.randompatches;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
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

	public static final boolean SPONGEFORGE_INSTALLED = detect("org.spongepowered.mod.SpongeMod");
	public static final boolean ITLT_INSTALLED = detect("dk.zlepper.itlt.about.mod");
	public static final boolean REBIND_NARRATOR_INSTALLED =
			detect("quaternary.rebindnarrator.RebindNarrator");

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	private static boolean detect(String className) {
		try {
			Class.forName(className);
		} catch(ClassNotFoundException ex) {
			return false;
		}

		return true;
	}
}
