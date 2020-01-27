package com.therandomlabs.randompatches.hook;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;

public final class EntityHook {
	private EntityHook() {}

	public static void writeAABBTag(Entity entity, CompoundNBT compound) {
		final AxisAlignedBB aabb = entity.getBoundingBox();
		final ListNBT list = new ListNBT();

		//Store relative bounding box rather than absolute to retain compatibility with
		//EU2 Golden Lasso and similar items
		list.add(DoubleNBT.valueOf(aabb.minX - entity.getPosX()));
		list.add(DoubleNBT.valueOf(aabb.minY - entity.getPosY()));
		list.add(DoubleNBT.valueOf(aabb.minZ - entity.getPosZ()));
		list.add(DoubleNBT.valueOf(aabb.maxX - entity.getPosX()));
		list.add(DoubleNBT.valueOf(aabb.maxY - entity.getPosY()));
		list.add(DoubleNBT.valueOf(aabb.maxZ - entity.getPosZ()));

		compound.put("RelativeAABB", list);
	}

	public static void readAABBTag(Entity entity, CompoundNBT compound) {
		if (!compound.contains("RelativeAABB")) {
			return;
		}

		final ListNBT aabb = compound.getList("RelativeAABB", Constants.NBT.TAG_DOUBLE);

		entity.setBoundingBox(new AxisAlignedBB(
				entity.getPosX() + aabb.getDouble(0),
				entity.getPosY() + aabb.getDouble(1),
				entity.getPosZ() + aabb.getDouble(2),
				entity.getPosX() + aabb.getDouble(3),
				entity.getPosY() + aabb.getDouble(4),
				entity.getPosZ() + aabb.getDouble(5)
		));
	}
}
