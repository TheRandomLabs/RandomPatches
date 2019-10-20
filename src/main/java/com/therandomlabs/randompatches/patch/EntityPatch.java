package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.core.Patch;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class EntityPatch extends Patch {
	public static final String ENTITYPATCH = getName(EntityPatch.class);
	public static final String SET_POSITION = getName("setPosition", "func_70107_b");

	@Override
	public boolean apply(ClassNode node) {
		patchWriteToNBT(findInstructions(node, "writeToNBT", "func_189511_e"));
		patchReadFromNBT(findInstructions(node, "readFromNBT", "func_70020_e"));

		return true;
	}

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

	private static void patchWriteToNBT(InsnList instructions) {
		MethodInsnNode setTag = null;

		for (int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				setTag = (MethodInsnNode) instruction;

				if ("net/minecraft/nbt/NBTTagCompound".equals(setTag.owner)) {
					break;
				}

				setTag = null;
			}
		}

		final InsnList newInstructions = new InsnList();

		//Get Entity (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get NBTTagCompound
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));

		//Call EntityPatch#writeAABBTag
		newInstructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				ENTITYPATCH,
				"writeAABBTag",
				"(Lnet/minecraft/entity/Entity;Lnet/minecraft/nbt/NBTTagCompound;)V",
				false
		));

		instructions.insert(setTag, newInstructions);
	}

	private static void patchReadFromNBT(InsnList instructions) {
		JumpInsnNode jumpIfShouldNotSetPosition = null;
		MethodInsnNode setPosition = null;

		for (int i = instructions.size() - 1; i >= 0; i--) {
			final AbstractInsnNode instruction = instructions.get(i);

			if (setPosition == null) {
				if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
					setPosition = (MethodInsnNode) instruction;

					if (!SET_POSITION.equals(setPosition.name)) {
						setPosition = null;
					}
				}

				continue;
			}

			if (instruction.getOpcode() == Opcodes.IFEQ) {
				jumpIfShouldNotSetPosition = (JumpInsnNode) instruction;
				break;
			}
		}

		final InsnList newInstructions = new InsnList();

		final LabelNode jumpTo = new LabelNode();

		jumpIfShouldNotSetPosition.label = jumpTo;

		newInstructions.add(jumpTo);

		//Get Entity (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get NBTTagCompound
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));

		//Call EntityPatch#readAABBTag
		newInstructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				ENTITYPATCH,
				"readAABBTag",
				"(Lnet/minecraft/entity/Entity;Lnet/minecraft/nbt/NBTTagCompound;)V",
				false
		));

		instructions.insert(setPosition, newInstructions);
	}
}
