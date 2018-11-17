package com.therandomlabs.randompatches;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
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

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	private static boolean detect(String className) {
		try {
			Class.forName(className);
		} catch(ClassNotFoundException ex) {
			return false;
		}

		return true;
	}
}
