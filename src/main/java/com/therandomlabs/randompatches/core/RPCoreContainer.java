package com.therandomlabs.randompatches.core;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.therandomlabs.randomlib.TRLUtils;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.config.RPGuiConfigFactory;
import com.therandomlabs.randompatches.util.RPUtils;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.versioning.VersionRange;

public class RPCoreContainer extends DummyModContainer {
	private static final Field MOD_CONTROLLER = TRLUtils.findField(Loader.class, "modController");
	private static final Field ACTIVE_CONTAINER =
			TRLUtils.findField(LoadController.class, "activeContainer");

	public RPCoreContainer() {
		super(RPUtils.loadMetadata(
				RPCore.getModFile(),
				RandomPatches.MOD_ID,
				RandomPatches.NAME,
				RandomPatches.VERSION
		));

		RandomPatches.containerInit();
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
		if(TRLUtils.MC_VERSION_NUMBER > 9) {
			Loader.instance().setActiveModContainer(this);
		} else {
			try {
				ACTIVE_CONTAINER.set(MOD_CONTROLLER.get(Loader.instance()), this);
			} catch(IllegalAccessException ex) {
				TRLUtils.crashReport("Failed to set active mod controller", ex);
			}
		}

		bus.register(new RandomPatches());
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
	public String getGuiClassName() {
		return RPGuiConfigFactory.class.getName();
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
