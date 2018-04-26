package com.therandomlabs.randompatches;

import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.VersionRange;

public class RPCoreContainer extends DummyModContainer {
	private static final ModMetadata metadata = new ModMetadata();

	public RPCoreContainer() {
		super(metadata);
		metadata.modId = RandomPatches.MODID;
		metadata.name = RandomPatches.NAME;
		metadata.description = RandomPatches.DESCRIPTION;
		metadata.version = RandomPatches.VERSION;
		metadata.url = RandomPatches.URL;
		metadata.updateJSON = RandomPatches.UPDATE_JSON;
		metadata.authorList.add(RandomPatches.AUTHOR);
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		Loader.instance().setActiveModContainer(this);
		MinecraftForge.EVENT_BUS.register(new RPEventHandler());
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
