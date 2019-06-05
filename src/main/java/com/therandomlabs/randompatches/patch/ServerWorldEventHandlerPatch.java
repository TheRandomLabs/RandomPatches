package com.therandomlabs.randompatches.patch;

import net.minecraft.particles.IParticleData;
import net.minecraft.world.WorldServer;

public final class ServerWorldEventHandlerPatch {
	private ServerWorldEventHandlerPatch() {}

	public static void spawnParticle(WorldServer world, IParticleData particleData,
			boolean alwaysRender, double x, double y, double z, double xSpeed, double ySpeed,
			double zSpeed) {
		//particleCount must be 0 so that the speed parameters are actually used and not
		//randomized in NetHandlerPlayClient#handleParticles.
		//The speed parameters are actually RGB values for anything potion related.
		world.spawnParticle(
				particleData,
				x, y, z,
				0,
				xSpeed, ySpeed, zSpeed,
				1.0
		);
	}
}
