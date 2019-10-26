package com.therandomlabs.randompatches.hook.endportal;

import net.minecraft.util.EnumFacing;

public final class BlockEndPortalHook {
	private BlockEndPortalHook() {}

	public static boolean shouldSideBeRendered(EnumFacing side) {
		return side == EnumFacing.UP || side == EnumFacing.DOWN;
	}
}
