package com.therandomlabs.randompatches.patch;

import net.minecraft.util.Direction;

public final class EndPortalTileEntityPatch {
	private EndPortalTileEntityPatch() {}

	public static boolean shouldRenderFace(Direction face) {
		return face == Direction.UP || face == Direction.DOWN;
	}
}
