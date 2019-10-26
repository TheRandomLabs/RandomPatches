package com.therandomlabs.randompatches.hook;

import com.therandomlabs.randompatches.config.RPConfig;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public final class ItemBucketHook {
	private ItemBucketHook() {}

	public static boolean isSolid(IBlockState state) {
		final Material material = state.getMaterial();

		if (material.isSolid()) {
			return true;
		}

		if (!RPConfig.Misc.portalBucketReplacementFixForNetherPortals &&
				state.getBlock() == Blocks.PORTAL) {
			return false;
		}

		return material == Material.PORTAL;
	}
}
