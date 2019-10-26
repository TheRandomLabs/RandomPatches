package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class EntityBoatPatch extends Patch {
	public static final String STATUS = getName("status", "field_184469_aF");
	public static final String OUT_OF_CONTROL_TICKS =
			getName("outOfControlTicks", "field_184474_h");

	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "onUpdate", "func_70071_h_");
		InsnNode returnVoid = null;

		for (int i = instructions.size() - 1; i >= 0; i--) {
			final AbstractInsnNode instruction = instructions.get(i);

			if (instruction.getOpcode() == Opcodes.RETURN) {
				returnVoid = (InsnNode) instruction;
				break;
			}
		}

		final InsnList newInstructions = new InsnList();

		//Get EntityBoat (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get EntityBoat (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get EntityBoat#status
		newInstructions.add(new FieldInsnNode(
				Opcodes.GETFIELD,
				"net/minecraft/entity/item/EntityBoat",
				STATUS,
				"Lnet/minecraft/entity/item/EntityBoat$Status;"
		));

		//Call EntityBoatHook#onUpdate
		newInstructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				hookClass,
				"onUpdate",
				"(Lnet/minecraft/entity/item/EntityBoat;" +
						"Lnet/minecraft/entity/item/EntityBoat$Status;)V",
				false
		));

		instructions.insertBefore(returnVoid, newInstructions);

		return true;
	}
}
