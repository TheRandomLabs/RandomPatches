package com.therandomlabs.randompatches.hook;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class TileEntityPistonHook {
	private TileEntityPistonHook() {}

	public static void updatePistonExtension(World world, BlockPos pos) {
		final IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(
				pos.offset(state.getValue(BlockDirectional.FACING).getOpposite()), state, state, 0
		);
	}

	public static void onPistonMoveBlock(TileEntityPiston piston) {
		final IBlockState state = piston.getPistonState();
		final Block block = state.getBlock();

		if (block instanceof BlockObserver && !state.getValue(BlockObserver.POWERED)) {
			final World world = piston.getWorld();
			final BlockPos pos = piston.getPos();

			if (!world.isUpdateScheduled(pos, block)) {
				world.scheduleUpdate(pos, block, 2);
			}
		}
	}
}
