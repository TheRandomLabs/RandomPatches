package com.therandomlabs.randompatches.util;

import java.lang.reflect.InvocationTargetException;
import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class RPTeleporter extends Teleporter {
	private static Class<? extends Teleporter> teleporterClass;
	private final Teleporter customTeleporter;

	public RPTeleporter(WorldServer world) {
		super(world);

		if(teleporterClass == null) {
			customTeleporter = null;
		} else {
			Teleporter teleporter = null;

			try {
				teleporter =
						teleporterClass.getConstructor(WorldServer.class).newInstance(world);
			} catch(IllegalAccessException | InstantiationException | NoSuchMethodException |
					InvocationTargetException ex) {
				RPUtils.crashReport("Failed to instantiate: " + teleporterClass.getName(), ex);
			}

			this.customTeleporter = teleporter;
		}
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

	@Override
	public boolean isVanilla() {
		return customTeleporter == null ? true : customTeleporter.isVanilla();
	}

	public static void setTeleporter(Class<? extends Teleporter> teleporterClass) {
		RPTeleporter.teleporterClass = teleporterClass;
	}
}
