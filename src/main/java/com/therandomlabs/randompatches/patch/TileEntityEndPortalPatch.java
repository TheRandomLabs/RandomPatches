package com.therandomlabs.randompatches.patch;

import net.minecraft.util.EnumFacing;

public final class TileEntityEndPortalPatch {
	private TileEntityEndPortalPatch() {}

	public static boolean shouldRenderFace(EnumFacing face) {
		return face == EnumFacing.UP || face == EnumFacing.DOWN;
	}
}
