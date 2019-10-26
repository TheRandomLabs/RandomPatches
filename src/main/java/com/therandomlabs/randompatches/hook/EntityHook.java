package com.therandomlabs.randompatches.hook;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;

public final class EntityHook {
	private EntityHook() {}

	public static void writeAABBTag(Entity entity, NBTTagCompound compound) {
		final AxisAlignedBB aabb = entity.getEntityBoundingBox();
		final NBTTagList list = new NBTTagList();

		//Store relative bounding box rather than absolute to retain compatibility with
		//EU2 Golden Lasso and similar items
		list.appendTag(new NBTTagDouble(aabb.minX - entity.posX));
		list.appendTag(new NBTTagDouble(aabb.minY - entity.posY));
		list.appendTag(new NBTTagDouble(aabb.minZ - entity.posZ));
		list.appendTag(new NBTTagDouble(aabb.maxX - entity.posX));
		list.appendTag(new NBTTagDouble(aabb.maxY - entity.posY));
		list.appendTag(new NBTTagDouble(aabb.maxZ - entity.posZ));

		compound.setTag("RelativeAABB", list);
	}

	public static void readAABBTag(Entity entity, NBTTagCompound compound) {
		if (!compound.hasKey("RelativeAABB")) {
			return;
		}

		final NBTTagList aabb = compound.getTagList("RelativeAABB", Constants.NBT.TAG_DOUBLE);

		entity.setEntityBoundingBox(new AxisAlignedBB(
				entity.posX + aabb.getDoubleAt(0),
				entity.posY + aabb.getDoubleAt(1),
				entity.posZ + aabb.getDoubleAt(2),
				entity.posX + aabb.getDoubleAt(3),
				entity.posY + aabb.getDoubleAt(4),
				entity.posZ + aabb.getDoubleAt(5)
		));
	}
}
