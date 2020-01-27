package com.therandomlabs.randompatches.hook;

import com.therandomlabs.randompatches.RPConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;

public final class BucketItemHook {
	private BucketItemHook() {}

	public static boolean isSolid(BlockState state) {
		final Material material = state.getMaterial();

		if (material.isSolid()) {
			return true;
		}

		if (!RPConfig.Misc.portalBucketReplacementFixForNetherPortals &&
				state.getBlock() == Blocks.NETHER_PORTAL) {
			return false;
		}

		return material == Material.PORTAL;
	}
}
