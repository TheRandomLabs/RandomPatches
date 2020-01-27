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
		list.add(new DoubleNBT(aabb.minX - entity.posX));
		list.add(new DoubleNBT(aabb.minY - entity.posY));
		list.add(new DoubleNBT(aabb.minZ - entity.posZ));
		list.add(new DoubleNBT(aabb.maxX - entity.posX));
		list.add(new DoubleNBT(aabb.maxY - entity.posY));
		list.add(new DoubleNBT(aabb.maxZ - entity.posZ));

		compound.put("RelativeAABB", list);
	}

	public static void readAABBTag(Entity entity, CompoundNBT compound) {
		if (!compound.contains("RelativeAABB")) {
			return;
		}

		final ListNBT aabb = compound.getList("RelativeAABB", Constants.NBT.TAG_DOUBLE);

		entity.setBoundingBox(new AxisAlignedBB(
				entity.posX + aabb.getDouble(0),
				entity.posY + aabb.getDouble(1),
				entity.posZ + aabb.getDouble(2),
				entity.posX + aabb.getDouble(3),
				entity.posY + aabb.getDouble(4),
				entity.posZ + aabb.getDouble(5)
		));
	}
}
