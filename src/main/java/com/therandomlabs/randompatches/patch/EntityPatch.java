package com.therandomlabs.randompatches.patch;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;

public final class EntityPatch {
	private EntityPatch() {}

	public static void writeAABBTag(Entity entity, NBTTagCompound compound) {
		final AxisAlignedBB aabb = entity.getBoundingBox();
		final NBTTagList list = new NBTTagList();

		//Store relative bounding box rather than absolute to retain compatibility with
		//EU2 Golden Lasso and similar items
		list.add(new NBTTagDouble(aabb.minX - entity.posX));
		list.add(new NBTTagDouble(aabb.minY - entity.posY));
		list.add(new NBTTagDouble(aabb.minZ - entity.posZ));
		list.add(new NBTTagDouble(aabb.maxX - entity.posX));
		list.add(new NBTTagDouble(aabb.maxY - entity.posY));
		list.add(new NBTTagDouble(aabb.maxZ - entity.posZ));

		compound.put("RelativeAABB", list);
	}

	public static void readAABBTag(Entity entity, NBTTagCompound compound) {
		if(!compound.contains("RelativeAABB")) {
			return;
		}

		final NBTTagList aabb = compound.getList("RelativeAABB", Constants.NBT.TAG_DOUBLE);

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
