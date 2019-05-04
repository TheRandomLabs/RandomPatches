package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.RPConfig;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public final class ItemBucketPatch {
	private ItemBucketPatch() {}

	public static boolean isSolid(IBlockState state) {
		final Material material = state.getMaterial();

		if(material.isSolid()) {
			return true;
		}

		if(!RPConfig.Misc.portalBucketReplacementFixForNetherPortals &&
				state.getBlock() == Blocks.NETHER_PORTAL) {
			return false;
		}

		return material == Material.PORTAL;
	}
}
