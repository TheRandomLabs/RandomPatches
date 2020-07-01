package com.therandomlabs.randompatches.hook;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class BlockObserverHook {
	private BlockObserverHook() {}

	public static void onBlockAdded(
			BlockObserver block, World world, BlockPos pos, IBlockState state
	) {
		if (world.isRemote || !state.getValue(BlockObserver.POWERED) ||
				world.isUpdateScheduled(pos, block)) {
			return;
		}

		final IBlockState unpowered = state.withProperty(BlockObserver.POWERED, false);
		world.setBlockState(pos, unpowered, 18);

		final EnumFacing facing = unpowered.getValue(BlockDirectional.FACING);
		final BlockPos opposite = pos.offset(facing.getOpposite());

		world.neighborChanged(opposite, block, pos);
		world.notifyNeighborsOfStateExcept(opposite, block, facing);
	}
}
