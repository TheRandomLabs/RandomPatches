package com.therandomlabs.randompatches.hook;

import java.util.function.Predicate;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

//Taken from 1.16.1.
//Sometimes I wonder if this was worth the effort of backporting.
public final class EntityLivingBaseHook {
	private EntityLivingBaseHook() {}

	public static void dismountEntity(EntityLivingBase rider, Entity riding) {
		if (!riding.isDead &&
				rider.world.getBlockState(riding.getPosition()).getMaterial() != Material.PORTAL) {
			final Vec3d pos = getDismountPosition(rider, riding);
			rider.setPositionAndUpdate(pos.x, pos.y, pos.z);
		} else {
			rider.setPositionAndUpdate(riding.posX, riding.posY + riding.height, riding.posZ);
		}
	}

	public static Vec3d getDismountPosition(Entity riding) {
		return new Vec3d(riding.posX, riding.getEntityBoundingBox().maxY, riding.posZ);
	}

	public static Vec3d getDismountPosition(EntityLivingBase rider, Entity riding) {
		if (riding instanceof EntityBoat) {
			return getDismountPosition(rider, (EntityBoat) riding);
		}

		if (riding instanceof EntityMinecart) {
			return getDismountPosition(rider, (EntityMinecart) riding);
		}

		if (riding instanceof EntityPig) {
			return getDismountPosition(rider, (EntityPig) riding);
		}

		return getDismountPosition(riding);
	}

	public static Vec3d getDismountPosition(EntityLivingBase rider, EntityBoat boat) {
		final Vec3d xzDisplacement = getXZDisplacement(
				boat.width * MathHelper.SQRT_2, rider.width, boat.rotationYaw
		);

		final double x = boat.posX + xzDisplacement.x;
		final double z = boat.posZ + xzDisplacement.z;

		BlockPos pos = new BlockPos(x, boat.getEntityBoundingBox().maxY, z);
		double yDisplacement = getYDisplacement(boat.world, pos);

		if (isValidYDisplacement(yDisplacement)) {
			final Vec3d dismountPos = new Vec3d(x, pos.getY() + yDisplacement, z);

			if (!rider.world.checkBlockCollision(
					rider.getEntityBoundingBox().offset(dismountPos)
			)) {
				return dismountPos;
			}
		}

		pos = pos.down();
		yDisplacement = getYDisplacement(boat.world, pos);

		if (isValidYDisplacement(yDisplacement)) {
			final Vec3d dismountPos = new Vec3d(x, pos.getY() + yDisplacement, z);

			if (!rider.world.checkBlockCollision(
					rider.getEntityBoundingBox().offset(dismountPos)
			)) {
				return dismountPos;
			}
		}

		return getDismountPosition(boat);
	}

	@SuppressWarnings("Duplicates")
	public static Vec3d getDismountPosition(EntityLivingBase rider, EntityMinecart minecart) {
		final EnumFacing facing = minecart.getAdjustedHorizontalFacing();

		if (facing.getAxis() == EnumFacing.Axis.Y) {
			return getDismountPosition(minecart);
		}

		final int[][] offsets = getOffsets(facing);
		final BlockPos pos = minecart.getPosition();
		final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		for (int[] offset : offsets) {
			for (int i = -1; i < 2; i++) {
				mutable.setPos(pos.getX() + offset[0], pos.getY() + i, pos.getZ() + offset[1]);

				final double yDisplacement = getYDisplacement(minecart.world, mutable, state -> {
					if (state.getBlock().isLadder(state, minecart.world, pos, rider)) {
						return true;
					}

					return state.getBlock() instanceof BlockTrapDoor &&
							state.getValue(BlockTrapDoor.OPEN);
				});

				if (isValidYDisplacement(yDisplacement)) {
					final Vec3d dismountPos = new Vec3d(
							mutable.getX() + 0.5,
							mutable.getY() + yDisplacement,
							mutable.getZ() + 0.5
					);

					if (!rider.world.checkBlockCollision(
							rider.getEntityBoundingBox().offset(dismountPos)
					)) {
						return dismountPos;
					}
				}
			}
		}

		return getDismountPosition(minecart);
	}

	@SuppressWarnings("Duplicates")
	public static Vec3d getDismountPosition(EntityLivingBase rider, EntityPig pig) {
		final EnumFacing facing = pig.getAdjustedHorizontalFacing();

		if (facing.getAxis() == EnumFacing.Axis.Y) {
			return getDismountPosition(pig);
		}

		final int[][] offsets = getOffsets(facing);
		final BlockPos pos = pig.getPosition();
		final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		for (int[] offset : offsets) {
			mutable.setPos(pos.getX() + offset[0], pos.getY(), pos.getZ() + offset[1]);

			final double yDisplacement = getYDisplacement(pig.world, mutable);

			if (isValidYDisplacement(yDisplacement)) {
				final Vec3d dismountPos = new Vec3d(
						mutable.getX() + 0.5,
						mutable.getY() + yDisplacement,
						mutable.getZ() + 0.5
				);

				if (!rider.world.checkBlockCollision(
						rider.getEntityBoundingBox().offset(dismountPos)
				)) {
					return dismountPos;
				}
			}
		}

		return getDismountPosition(pig);
	}

	public static Vec3d getXZDisplacement(double ridingWidth, double riderWidth, float ridingYaw) {
		final double averageWidth = (ridingWidth + riderWidth) / 2.0;
		final float sin = -MathHelper.sin((float) (ridingYaw * Math.PI / 180.0));
		final float cos = MathHelper.cos((float) (ridingYaw * Math.PI / 180.0));
		final float max = Math.max(Math.abs(sin), Math.abs(cos));
		return new Vec3d(sin * averageWidth / max, 0.0D, cos * averageWidth / max);
	}

	public static double getYDisplacement(World world, BlockPos pos) {
		return getYDisplacement(world, pos, state -> false);
	}

	public static double getYDisplacement(
			World world, BlockPos pos, Predicate<IBlockState> ignore
	) {
		IBlockState state = world.getBlockState(pos);
		AxisAlignedBB collisionBox =
				ignore.test(state) ? null : state.getCollisionBoundingBox(world, pos);

		if (collisionBox != null && collisionBox.maxY != 0.0) {
			return collisionBox.maxY;
		}

		pos = pos.down();
		state = world.getBlockState(pos);
		collisionBox = ignore.test(state) ? null : state.getCollisionBoundingBox(world, pos);
		final double maxY = collisionBox == null ? 0.0 : collisionBox.maxY;
		return maxY >= 1.0 ? maxY - 1.0 : Double.NEGATIVE_INFINITY;
	}

	public static boolean isValidYDisplacement(double displacement) {
		return !Double.isInfinite(displacement) && displacement < 1.0;
	}

	public static int[][] getOffsets(EnumFacing facing) {
		final EnumFacing direction1 = facing.rotateY();
		final EnumFacing direction2 = direction1.getOpposite();
		final EnumFacing direction3 = facing.getOpposite();

		return new int[][] {
				{
						direction1.getXOffset(),
						direction1.getZOffset()
				},
				{
						direction2.getXOffset(),
						direction2.getZOffset()
				},
				{
						direction3.getXOffset() + direction1.getXOffset(),
						direction3.getZOffset() + direction1.getZOffset()
				},
				{
						direction3.getXOffset() + direction2.getXOffset(),
						direction3.getZOffset() + direction2.getZOffset()
				},
				{
						facing.getXOffset() + direction1.getXOffset(),
						facing.getZOffset() + direction1.getZOffset()
				},
				{
						facing.getXOffset() + direction2.getXOffset(),
						facing.getZOffset() + direction2.getZOffset()
				},
				{
						direction3.getXOffset(),
						direction3.getZOffset()
				},
				{
						facing.getXOffset(),
						facing.getZOffset()
				}
		};
	}
}
