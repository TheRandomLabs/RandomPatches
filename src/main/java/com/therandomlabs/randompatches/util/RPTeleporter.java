package com.therandomlabs.randompatches.util;

import com.therandomlabs.verticalendportals.util.VEPTeleporter;
import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Loader;

public class RPTeleporter extends Teleporter {
	public static final boolean VERTICAL_END_PORTALS_LOADED =
			Loader.isModLoaded("verticalendportals");

	private final Teleporter customTeleporter;

	public RPTeleporter(WorldServer world) {
		super(world);
		customTeleporter = VERTICAL_END_PORTALS_LOADED ? new VEPTeleporter(world) : null;
	}

	@Override
	public void placeInPortal(Entity entity, float yaw) {
		if(customTeleporter == null) {
			super.placeInPortal(entity, yaw);
		} else {
			customTeleporter.placeInPortal(entity, yaw);
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, float yaw) {
		if(customTeleporter == null) {
			return super.placeInExistingPortal(entity, yaw);
		}

		return customTeleporter.placeInExistingPortal(entity, yaw);
	}

	@Override
	public boolean makePortal(Entity entity) {
		if(customTeleporter == null) {
			return super.makePortal(entity);
		}

		return customTeleporter.makePortal(entity);
	}

	@Override
	public void removeStalePortalLocations(long worldTime) {
		if(customTeleporter == null) {
			super.removeStalePortalLocations(worldTime);
		} else {
			customTeleporter.removeStalePortalLocations(worldTime);
		}
	}

	@Override
	public void placeEntity(World world, Entity entity, float yaw) {
		if(customTeleporter == null) {
			super.placeEntity(world, entity, yaw);
		} else {
			customTeleporter.placeEntity(world, entity, yaw);
		}
	}
}
