package com.therandomlabs.randompatches.hook;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.utils.forge.ForgeUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

//Taken from 1.16.1.
//Sometimes I wonder if this was worth the effort of backporting.
public final class LivingEntityHook {
	private static final ImmutableMap<Pose, ImmutableList<Integer>> MINECART_POSE_Y =
			ImmutableMap.of(
					Pose.STANDING, ImmutableList.of(0, 1, -1),
					Pose.SNEAKING, ImmutableList.of(0, 1, -1),
					Pose.SWIMMING, ImmutableList.of(0, 1)
			);

	private static final Method SET_POSE =
			ObfuscationReflectionHelper.findMethod(Entity.class, "func_213301_b", Pose.class);

	private LivingEntityHook() {}

	public static void dismountEntity(LivingEntity rider, Entity riding) {
		if (riding.isAlive() &&
				rider.world.getBlockState(riding.getPosition()).getMaterial() != Material.PORTAL) {
			final Vec3d pos = getDismountPosition(rider, riding);
			rider.setPositionAndUpdate(pos.x, pos.y, pos.z);
		} else {
			rider.setPositionAndUpdate(riding.posX, riding.posY + riding.getHeight(), riding.posZ);
		}
	}

	public static Vec3d getDismountPosition(Entity riding) {
		return new Vec3d(riding.posX, riding.getBoundingBox().maxY, riding.posZ);
	}

	public static Vec3d getDismountPosition(LivingEntity rider, Entity riding) {
		if (riding instanceof BoatEntity) {
			return getDismountPosition(rider, (BoatEntity) riding);
		}

		if (riding instanceof MinecartEntity) {
			return getDismountPosition(rider, (MinecartEntity) riding);
		}

		if (riding instanceof PigEntity) {
			return getDismountPosition(rider, (PigEntity) riding);
		}

		return getDismountPosition(riding);
	}

	public static Vec3d getDismountPosition(LivingEntity rider, BoatEntity boat) {
		final Vec3d xzDisplacement = getXZDisplacement(
				boat.getWidth() * MathHelper.SQRT_2, rider.getWidth(), boat.rotationYaw
		);

		final double x = boat.posX + xzDisplacement.x;
		final double z = boat.posZ + xzDisplacement.z;

		BlockPos pos = new BlockPos(x, boat.getBoundingBox().maxY, z);

		if (boat.world.hasWater(pos.down())) {
			return getDismountPosition(boat);
		}

		for (Pose pose : getPoses(rider)) {
			final AxisAlignedBB boundingBox = getBoundingBox(rider, pose);
			double yDisplacement = getYDisplacement(boat.world, pos);

			if (isValidYDisplacement(yDisplacement)) {
				final Vec3d dismountPos = new Vec3d(x, pos.getY() + yDisplacement, z);

				if (canDismount(rider.world, rider, boundingBox.offset(dismountPos))) {
					setPose(rider, pose);
					return dismountPos;
				}
			}

			pos = pos.down();
			yDisplacement = getYDisplacement(boat.world, pos);

			if (isValidYDisplacement(yDisplacement)) {
				final Vec3d dismountPos = new Vec3d(x, pos.getY() + yDisplacement, z);

				if (canDismount(rider.world, rider, boundingBox.offset(dismountPos))) {
					setPose(rider, pose);
					return dismountPos;
				}
			}
		}

		return getDismountPosition(boat);
	}

	@SuppressWarnings("Duplicates")
	public static Vec3d getDismountPosition(LivingEntity rider, MinecartEntity minecart) {
		final Direction facing = minecart.getAdjustedHorizontalFacing();

		if (facing.getAxis() == Direction.Axis.Y) {
			return getDismountPosition(minecart);
		}

		final int[][] offsets = getOffsets(facing);
		final BlockPos pos = minecart.getPosition();
		final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		for (Pose pose : getPoses(rider)) {
			final EntitySize size = rider.getSize(pose);
			final float f = Math.min(size.width, 1.0F) / 2.0F;

			for (int i : MINECART_POSE_Y.get(pose)) {
				for (int[] offset : offsets) {
					mutable.setPos(pos.getX() + offset[0], pos.getY() + i, pos.getZ() + offset[1]);

					final double yDisplacement =
							getYDisplacement(minecart.world, mutable, state -> {
								if (state.getBlock().isLadder(state, minecart.world, pos, rider)) {
									return true;
								}

								return state.getBlock() instanceof TrapDoorBlock &&
										state.get(TrapDoorBlock.OPEN);
							});

					if (isValidYDisplacement(yDisplacement)) {
						final AxisAlignedBB boundingBox = new AxisAlignedBB(
								-f, yDisplacement, -f, f, yDisplacement + size.height, f
						);
						final Vec3d dismountPos = new Vec3d(
								mutable.getX() + 0.5,
								mutable.getY() + yDisplacement,
								mutable.getZ() + 0.5
						);

						if (canDismount(rider.world, rider, boundingBox.offset(dismountPos))) {
							setPose(rider, pose);
							return dismountPos;
						}
					}
				}
			}
		}

		return getDismountPosition(minecart);
	}

	@SuppressWarnings("Duplicates")
	public static Vec3d getDismountPosition(LivingEntity rider, PigEntity pig) {
		final Direction facing = pig.getAdjustedHorizontalFacing();

		if (facing.getAxis() == Direction.Axis.Y) {
			return getDismountPosition(pig);
		}

		final int[][] offsets = getOffsets(facing);
		final BlockPos pos = pig.getPosition();
		final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		for (Pose pose : getPoses(rider)) {
			final AxisAlignedBB boundingBox = getBoundingBox(rider, pose);

			for (int[] offset : offsets) {
				mutable.setPos(pos.getX() + offset[0], pos.getY(), pos.getZ() + offset[1]);

				final double yDisplacement = getYDisplacement(pig.world, mutable);

				if (isValidYDisplacement(yDisplacement)) {
					final Vec3d dismountPos = new Vec3d(
							mutable.getX() + 0.5,
							mutable.getY() + yDisplacement,
							mutable.getZ() + 0.5
					);

					if (canDismount(rider.world, rider, boundingBox.offset(dismountPos))) {
						setPose(rider, pose);
						return dismountPos;
					}
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
			World world, BlockPos pos, Predicate<BlockState> ignore
	) {
		BlockState state = world.getBlockState(pos);
		VoxelShape shape =
				ignore.test(state) ? VoxelShapes.empty() : state.getCollisionShape(world, pos);

		if (!shape.isEmpty()) {
			return shape.getEnd(Direction.Axis.Y);
		}

		pos = pos.down();
		state = world.getBlockState(pos);
		shape = ignore.test(state) ? VoxelShapes.empty() : state.getCollisionShape(world, pos);
		final double end = shape.getEnd(Direction.Axis.Y);
		return end >= 1.0 ? end - 1.0 : Double.NEGATIVE_INFINITY;
	}

	public static boolean isValidYDisplacement(double displacement) {
		return !Double.isInfinite(displacement) && displacement < 1.0;
	}

	public static int[][] getOffsets(Direction facing) {
		final Direction direction1 = facing.rotateY();
		final Direction direction2 = direction1.getOpposite();
		final Direction direction3 = facing.getOpposite();

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

	public static ImmutableList<Pose> getPoses(Entity entity) {
		return entity instanceof PlayerEntity ? ImmutableList.of(
				Pose.STANDING, Pose.SNEAKING, Pose.SWIMMING
		) : ImmutableList.of(Pose.STANDING);
	}

	public static AxisAlignedBB getBoundingBox(Entity entity, Pose pose) {
		final EntitySize size = entity.getSize(pose);
		return new AxisAlignedBB(
				-size.width / 2.0,
				0.0,
				-size.width / 2.0,
				size.width / 2.0,
				size.height,
				size.width / 2.0F
		);
	}

	public static boolean canDismount(World world, Entity entity, AxisAlignedBB boundingBox) {
		return world.getCollisionShapes(entity, boundingBox).allMatch(VoxelShape::isEmpty);
	}

	public static void setPose(Entity entity, Pose pose) {
		try {
			SET_POSE.invoke(entity, pose);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			ForgeUtils.crashReport("Failed to set pose", ex);
		}
	}
}
