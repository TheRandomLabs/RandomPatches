package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.RPConfig;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.util.math.Vec3d;

public final class BoatEntityPatch {
	public static final double VANILLA_UNDERWATER_BUOYANCY = -0.0007;

	private BoatEntityPatch() {}

	public static void tick(BoatEntity boat, BoatEntity.Status status) {
		if(status == BoatEntity.Status.UNDER_FLOWING_WATER) {
			final Vec3d motion = boat.getMotion();
			boat.setMotion(
					motion.x,
					motion.y + RPConfig.Boats.underwaterBoatBuoyancy - VANILLA_UNDERWATER_BUOYANCY,
					motion.z
			);
		}

		if(RPConfig.Boats.preventUnderwaterBoatPassengerEjection) {
			boat.outOfControlTicks = 0.0F;
		}
	}
}
