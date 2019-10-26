package com.therandomlabs.randompatches.hook;

import java.util.Objects;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;

public final class PlayerInteractionManagerHook {
	private PlayerInteractionManagerHook() {}

	public static void spawnParticle(
			WorldServer world, int particleID, boolean ignoreRange, double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed, int... parameters
	) {
		final EnumParticleTypes particleType =
				Objects.requireNonNull(EnumParticleTypes.getParticleFromId(particleID));

		//In SPacketParticles, there are always as many ints written to the PacketBuffer as there
		//are arguments defined for that particle type, however, there is no check in vanilla to
		//ensure there are as many parameters as required, leading to an
		//ArrayIndexOutOfBoundsException.
		//This exception would also occur in vanilla when calling EntityLivingBase#updateItemUse
		//on the server when there are no subtypes for an item if not for MC-10369, which this
		//patch fixes.
		if (parameters.length == particleType.getArgumentCount()) {
			//numberOfParticles must be 0 so that the speed parameters are actually used and not
			//randomized in NetHandlerPlayClient#handleParticles.
			//The speed parameters are actually RGB values for anything potion related.
			world.spawnParticle(
					particleType,
					x, y, z,
					0,
					xSpeed, ySpeed, zSpeed,
					1.0,
					parameters
			);
		}
	}
}
