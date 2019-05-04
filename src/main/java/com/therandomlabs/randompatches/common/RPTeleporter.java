package com.therandomlabs.randompatches.common;

import java.lang.reflect.InvocationTargetException;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public final class RPTeleporter extends Teleporter {
	private static Class<? extends Teleporter> teleporterClass;
	private final Teleporter customTeleporter;

	public RPTeleporter(WorldServer world) {
		super(world);

		if(teleporterClass == null) {
			customTeleporter = null;
			return;
		}

		try {
			customTeleporter =
					teleporterClass.getConstructor(WorldServer.class).newInstance(world);
		} catch(IllegalAccessException | InstantiationException | NoSuchMethodException |
				InvocationTargetException ex) {
			throw new ReportedException(
					new CrashReport("Failed to instantiate: " + teleporterClass.getName(), ex)
			);
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
	public void tick(long worldTime) {
		if(customTeleporter == null) {
			super.tick(worldTime);
		} else {
			customTeleporter.tick(worldTime);
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
		return customTeleporter == null || customTeleporter.isVanilla();
	}

	public static void setTeleporter(Class<? extends Teleporter> clazz) {
		teleporterClass = clazz;
	}
}
