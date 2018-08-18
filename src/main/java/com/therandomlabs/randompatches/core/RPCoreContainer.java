package com.therandomlabs.randompatches.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.therandomlabs.randompatches.RPStaticConfig;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.WindowIconHandler;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.VersionRange;
import org.lwjgl.opengl.Display;

public class RPCoreContainer extends DummyModContainer {
	public RPCoreContainer() {
		super(loadMetadata(RPCore.getModFile(), RandomPatches.MODID, RandomPatches.NAME,
				RandomPatches.VERSION));

		if(!RandomPatches.ITLT_INSTALLED) {
			WindowIconHandler.setWindowIcon();
			Display.setTitle(RPStaticConfig.title);
		}
	}

	@Override
	public File getSource() {
		return RPCore.getModFile();
	}

	@Override
	public Class<?> getCustomResourcePackClass() {
		return getResourcePackClass(this);
	}

	@Override
	public URL getUpdateUrl() {
		try {
			return new URL(getMetadata().updateJSON);
		} catch(MalformedURLException ignored) {}

		return null;
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		if(!RandomPatches.IS_ONE_EIGHT && !RandomPatches.IS_ONE_NINE) {
			Loader.instance().setActiveModContainer(this);
			bus.register(new RandomPatches());
		}

		return true;
	}

	@Override
	public VersionRange acceptableMinecraftVersionRange() {
		try {
			return VersionRange.createFromVersionSpec(RandomPatches.MINECRAFT_VERSIONS);
		} catch(InvalidVersionSpecificationException ex) {
			ex.printStackTrace();
		}

		return null;
	}

	@Override
	public List<String> getOwnedPackages() {
		return ImmutableList.of("com.therandomlabs.randompatches");
	}

	public static ModMetadata loadMetadata(File source, String modid, String name, String version) {
		InputStream stream = null;

		if(source != null) {
			try {
				if(source.isDirectory()) {
					stream = new FileInputStream(new File(source, "mcmod.info"));
				} else {
					final JarFile jar = new JarFile(source);
					stream = jar.getInputStream(jar.getJarEntry("mcmod.info"));
				}
			} catch(IOException ex) {
				RandomPatches.LOGGER.error("Failed to load mcmod.info", ex);
			}
		}

		final Map<String, Object> fallback = new HashMap<>();

		fallback.put("name", name);
		fallback.put("version", version);

		return MetadataCollection.from(stream, modid).getMetadataForId(modid, fallback);
	}

	public static Class<?> getResourcePackClass(ModContainer container) {
		final File source = container.getSource();

		if(source == null) {
			return null;
		}

		final String className;

		if(source.isDirectory()) {
			className = "net.minecraftforge.fml.client.FMLFolderResourcePack";
		} else {
			className = "net.minecraftforge.fml.client.FMLFileResourcePack";
		}

		try {
			return Class.forName(className, true, RPCoreContainer.class.getClassLoader());
		} catch(ClassNotFoundException ignored) {}

		return null;
	}
}
