package com.therandomlabs.randompatches.hook;

import com.therandomlabs.randompatches.config.RPConfig;
import net.minecraft.entity.item.EntityBoat;

public final class EntityBoatHook {
	public static final double VANILLA_UNDERWATER_BUOYANCY = -0.0007;

	private EntityBoatHook() {}

	public static void onUpdate(EntityBoat boat, EntityBoat.Status status) {
		if (status == EntityBoat.Status.UNDER_FLOWING_WATER) {
			boat.motionY += RPConfig.Boats.underwaterBoatBuoyancy - VANILLA_UNDERWATER_BUOYANCY;
		}

		if (RPConfig.Boats.preventUnderwaterBoatPassengerEjection) {
			boat.outOfControlTicks = 0.0F;
		}
	}
}
