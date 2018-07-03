package com.therandomlabs.randompatches;

import java.net.MalformedURLException;
import java.net.URL;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RandomPatches {
	public static final String MODID = "randompatches";
	public static final String NAME = "RandomPatches";
	public static final String VERSION = "@VERSION@";
	public static final String AUTHOR = "TheRandomLabs";
	public static final String DESCRIPTION = "A bunch of miscellaneous patches for Minecraft.";
	public static final String LOGO_FILE =
			new ResourceLocation(MODID, "textures/logo.png").getResourcePath();
	public static final String PROJECT_URL =
			"https://minecraft.curseforge.com/projects/randompatches";
	public static final String UPDATE_JSON =
			"https://raw.githubusercontent.com/TheRandomLabs/RandomPatches/misc/versions.json";
	public static final URL UPDATE_URL;
	public static final String MINECRAFT_VERSIONS = "[1.10,1.13)";

	public static final boolean IS_DEOBFUSCATED =
			(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

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
