package com.therandomlabs.randompatches;

import java.net.MalformedURLException;
import java.net.URL;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RandomPatches {
	public static final String MODID = "randompatches";
	public static final String NAME = "RandomPatches";
	public static final String VERSION = "@VERSION@";
	public static final String AUTHOR = "TheRandomLabs";
	public static final String DESCRIPTION = "A bunch of miscellaneous patches for Minecraft.";
	public static final String LOGO_FILE = "assets/" + MODID + "/logo.png";
	public static final String PROJECT_URL =
			"https://minecraft.curseforge.com/projects/randompatches";
	public static final String UPDATE_JSON =
			"https://raw.githubusercontent.com/TheRandomLabs/RandomPatches/misc/versions.json";
	public static final URL UPDATE_URL;
	public static final String MINECRAFT_VERSIONS = "[1.10,1.13)";

	public static final boolean IS_DEOBFUSCATED =
			(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

	public static final String MC_VERSION = (String) FMLInjectionData.data()[4];
	public static final boolean IS_ONE_TEN = MC_VERSION.startsWith("1.10");
	public static final boolean IS_ONE_ELEVEN = MC_VERSION.startsWith("1.11");
	public static final boolean IS_ONE_TWELVE = MC_VERSION.startsWith("1.12");
	public static final boolean IS_ONE_TWELVE_TWO = MC_VERSION.equals("1.12.2");

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	static {
		URL url = null;

		try {
			url = new URL(UPDATE_JSON);
		} catch(MalformedURLException ignored) {}

		UPDATE_URL = url;
	}

	private RandomPatches() {}
}
