package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.RPConfig;
import net.minecraft.entity.item.EntityBoat;

public final class EntityBoatPatch {
	public static final double VANILLA_UNDERWATER_BUOYANCY = -0.0007;

	private EntityBoatPatch() {}

	public static void onUpdate(EntityBoat boat, EntityBoat.Status status) {
		if(status == EntityBoat.Status.UNDER_FLOWING_WATER) {
			boat.motionY += RPConfig.Boats.underwaterBoatBuoyancy - VANILLA_UNDERWATER_BUOYANCY;
		}
	}
}
