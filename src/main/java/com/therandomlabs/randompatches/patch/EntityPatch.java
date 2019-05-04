package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.Patch;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class EntityPatch extends Patch {
	public static final String ENTITYPATCH = getName(EntityPatch.class);
	public static final String SET_POSITION = getName("setPosition", "func_70107_b");

	@Override
	public boolean apply(ClassNode node) {
		patchWriteWithoutTypeId(findMethod(node, "writeWithoutTypeId", "func_189511_e"));
		patchRead(findMethod(node, "read", "func_70020_e"));

		return true;
	}

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

	private static void patchWriteWithoutTypeId(MethodNode method) {
		MethodInsnNode setTag = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				setTag = (MethodInsnNode) instruction;

				if("net/minecraft/nbt/NBTTagCompound".equals(setTag.owner)) {
					break;
				}

				setTag = null;
			}
		}

		final VarInsnNode loadThis = new VarInsnNode(
				Opcodes.ALOAD,
				0
		);

		final VarInsnNode loadCompound = new VarInsnNode(
				Opcodes.ALOAD,
				1
		);

		final MethodInsnNode writeAABBTag = new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				ENTITYPATCH,
				"writeAABBTag",
				"(Lnet/minecraft/entity/Entity;Lnet/minecraft/nbt/NBTTagCompound;)V",
				false
		);

		method.instructions.insert(setTag, loadThis);
		method.instructions.insert(loadThis, loadCompound);
		method.instructions.insert(loadCompound, writeAABBTag);
	}

	private static void patchRead(MethodNode method) {
		JumpInsnNode jumpIfShouldNotSetPosition = null;
		MethodInsnNode setPosition = null;

		for(int i = method.instructions.size() - 1; i >= 0; i--) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(setPosition == null) {
				if(instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
					setPosition = (MethodInsnNode) instruction;

					if(!SET_POSITION.equals(setPosition.name)) {
						setPosition = null;
					}
				}

				continue;
			}

			if(instruction.getOpcode() == Opcodes.IFEQ) {
				jumpIfShouldNotSetPosition = (JumpInsnNode) instruction;
				break;
			}
		}

		final LabelNode jumpTo = new LabelNode();

		jumpIfShouldNotSetPosition.label = jumpTo;

		final VarInsnNode loadThis = new VarInsnNode(
				Opcodes.ALOAD,
				0
		);

		final VarInsnNode loadCompound = new VarInsnNode(
				Opcodes.ALOAD,
				1
		);

		final MethodInsnNode readAABBTag = new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				ENTITYPATCH,
				"readAABBTag",
				"(Lnet/minecraft/entity/Entity;Lnet/minecraft/nbt/NBTTagCompound;)V",
				false
		);

		method.instructions.insert(setPosition, jumpTo);
		method.instructions.insert(jumpTo, loadThis);
		method.instructions.insert(loadThis, loadCompound);
		method.instructions.insert(loadCompound, readAABBTag);
	}
}
