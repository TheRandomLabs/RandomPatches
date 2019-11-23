package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class EntityPatch extends Patch {
	public static final String SET_POSITION = getName("setPosition", "func_70107_b");

	@Override
	public boolean apply(ClassNode node) {
		patchWriteToNBT(findInstructions(node, "writeToNBT", "func_189511_e"));
		patchReadFromNBT(findInstructions(node, "readFromNBT", "func_70020_e"));

		return true;
	}

	@Override
	public boolean computeFrames() {
		return true;
	}

	private void patchWriteToNBT(InsnList instructions) {
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

		//Call EntityHook#writeAABBTag
		newInstructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				hookClass,
				"writeAABBTag",
				"(Lnet/minecraft/entity/Entity;Lnet/minecraft/nbt/NBTTagCompound;)V",
				false
		));

		instructions.insert(setTag, newInstructions);
	}

	private void patchReadFromNBT(InsnList instructions) {
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

		//Call EntityHook#readAABBTag
		newInstructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				hookClass,
				"readAABBTag",
				"(Lnet/minecraft/entity/Entity;Lnet/minecraft/nbt/NBTTagCompound;)V",
				false
		));

		instructions.insert(setPosition, newInstructions);
	}
}
