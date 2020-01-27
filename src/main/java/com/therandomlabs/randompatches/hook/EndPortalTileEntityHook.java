package com.therandomlabs.randompatches.hook;

import net.minecraft.util.Direction;

public final class EndPortalTileEntityHook {
	private EndPortalTileEntityHook() {}

	public static boolean shouldRenderFace(Direction face) {
		return face == Direction.UP || face == Direction.DOWN;
	}
}
