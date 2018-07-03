package com.therandomlabs.randompatches.asm;

import java.io.File;
import java.net.URL;
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.therandomlabs.randompatches.RPConfig;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.event.RPClientEventHandler;
import com.therandomlabs.randompatches.event.RPEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.VersionRange;

public class RPCoreContainer extends DummyModContainer {
	private static final ModMetadata METADATA = new ModMetadata();

	static {
		METADATA.modId = RandomPatches.MODID;
		METADATA.name = RandomPatches.NAME;
		METADATA.version = RandomPatches.VERSION;
		METADATA.authorList.add(RandomPatches.AUTHOR);
		METADATA.description = RandomPatches.DESCRIPTION;
		METADATA.url = RandomPatches.PROJECT_URL;
		METADATA.logoFile = RandomPatches.LOGO_FILE;
	}

	public RPCoreContainer() {
		super(METADATA);
	}

	@Override
	public File getSource() {
		return RPCore.getModFile();
	}

	@Override
	public Class<?> getCustomResourcePackClass() {
		final File source = getSource();

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
			return Class.forName(className, true, getClass().getClassLoader());
		} catch(ClassNotFoundException ignored) {}

		return null;
	}

	@Override
	public URL getUpdateUrl() {
		return RandomPatches.UPDATE_URL;
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		Loader.instance().setActiveModContainer(this);

		MinecraftForge.EVENT_BUS.register(new RPEventHandler());

		if(FMLCommonHandler.instance().getSide().isClient()) {
			RPConfig.reload();
			MinecraftForge.EVENT_BUS.register(new RPClientEventHandler());
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
}
