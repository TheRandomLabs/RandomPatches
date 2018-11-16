package com.therandomlabs.randompatches.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.therandomlabs.randompatches.RPEventHandler;
import com.therandomlabs.randompatches.RPUtils;
import com.therandomlabs.randompatches.RandomPatches;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.versioning.VersionRange;

public class RPCoreContainer extends DummyModContainer {
	public RPCoreContainer() {
		super(RPUtils.loadMetadata(
				RPCore.getModFile(),
				RandomPatches.MOD_ID,
				RandomPatches.NAME,
				RandomPatches.VERSION
		));

		RPEventHandler.containerInit();
	}

	protected RPCoreContainer(ModMetadata metadata) {
		super(metadata);
	}

	@Override
	public File getSource() {
		return RPCore.getModFile();
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		if(RandomPatches.MC_VERSION > 9) {
			Loader.instance().setActiveModContainer(this);
			bus.register(new RPEventHandler());
		}

		return true;
	}

	@Override
	public VersionRange acceptableMinecraftVersionRange() {
		return RPUtils.createVersionRange(RandomPatches.MINECRAFT_VERSIONS);
	}

	@Override
	public Class<?> getCustomResourcePackClass() {
		return RPUtils.getResourcePackClass(this);
	}

	@Override
	public List<String> getOwnedPackages() {
		return ImmutableList.of("com.therandomlabs.randompatches");
	}

	@Override
	public URL getUpdateUrl() {
		try {
			return new URL(getMetadata().updateJSON);
		} catch(MalformedURLException ignored) {}

		return null;
	}
}
