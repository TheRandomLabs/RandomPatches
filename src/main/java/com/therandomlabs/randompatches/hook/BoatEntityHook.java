package com.therandomlabs.randompatches.hook;

import com.therandomlabs.randompatches.RPConfig;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.util.math.vector.Vector3d;

public final class BoatEntityHook {
	public static final double VANILLA_UNDERWATER_BUOYANCY = -0.0007;

	private BoatEntityHook() {}

	public static void tick(BoatEntity boat, BoatEntity.Status status) {
		if (status == BoatEntity.Status.UNDER_FLOWING_WATER) {
			final Vector3d motion = boat.getMotion();
			boat.setMotion(
					motion.x,
					motion.y + RPConfig.Boats.underwaterBoatBuoyancy - VANILLA_UNDERWATER_BUOYANCY,
					motion.z
			);
		}

		if (RPConfig.Boats.preventUnderwaterBoatPassengerEjection) {
			boat.outOfControlTicks = 0.0F;
		}
	}
}
