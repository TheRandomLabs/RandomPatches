package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.config.RPConfig;
import com.therandomlabs.randompatches.core.Patch;
import net.minecraft.entity.item.EntityBoat;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class EntityBoatPatch extends Patch {
	public static final String STATUS = getName("status", "field_184469_aF");
	public static final String OUT_OF_CONTROL_TICKS =
			getName("outOfControlTicks", "field_184474_h");

	public static final double VANILLA_UNDERWATER_BUOYANCY = -0.0007;

	@SuppressWarnings("Duplicates")
	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "onUpdate", "func_70071_h_");
		InsnNode returnVoid = null;

		for(int i = instructions.size() - 1; i >= 0; i--) {
			final AbstractInsnNode instruction = instructions.get(i);

			if(instruction.getOpcode() == Opcodes.RETURN) {
				returnVoid = (InsnNode) instruction;
				break;
			}
		}

		final InsnList newInstructions = new InsnList();

		final LabelNode returnLabel = new LabelNode();

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

		//Call EntityBoatPatch#onUpdate
		newInstructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				getName(EntityBoatPatch.class),
				"onUpdate",
				"(Lnet/minecraft/entity/item/EntityBoat;" +
						"Lnet/minecraft/entity/item/EntityBoat$Status;)V",
				false
		));

		//Get RPConfig.Boats#preventUnderwaterBoatPassengerEjection
		newInstructions.add(new FieldInsnNode(
				Opcodes.GETSTATIC,
				getName(RPConfig.Boats.class),
				"preventUnderwaterBoatPassengerEjection",
				"Z"
		));

		//Return if false
		newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, returnLabel));

		//Get EntityBoat (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Load 0.0F
		newInstructions.add(new InsnNode(Opcodes.FCONST_0));

		//Set EntityBoat#outOfControlTicks to 0.0F
		newInstructions.add(new FieldInsnNode(
				Opcodes.PUTFIELD,
				"net/minecraft/entity/item/EntityBoat",
				OUT_OF_CONTROL_TICKS,
				"F"
		));

		newInstructions.add(returnLabel);

		instructions.insertBefore(returnVoid, newInstructions);

		return true;
	}

	public static void onUpdate(EntityBoat boat, EntityBoat.Status status) {
		if(status == EntityBoat.Status.UNDER_FLOWING_WATER) {
			boat.motionY += -VANILLA_UNDERWATER_BUOYANCY + RPConfig.Boats.underwaterBoatBuoyancy;
		}
	}
}
