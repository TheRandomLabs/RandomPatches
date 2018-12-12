package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.config.RPStaticConfig;
import com.therandomlabs.randompatches.core.Patch;
import net.minecraft.entity.item.EntityBoat;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class EntityBoatPatch extends Patch {
	public static final String STATUS = getName("status", "field_184469_aF");
	public static final String OUT_OF_CONTROL_TICKS =
			getName("outOfControlTicks", "field_184474_h");

	public static final double VANILLA_UNDERWATER_BUOYANCY = -0.0007;

	@SuppressWarnings("Duplicates")
	@Override
	public void apply(ClassNode node) {
		final MethodNode method = findMethod(node, "onUpdate", "func_70071_h_");
		InsnNode returnVoid = null;

		for(int i = method.instructions.size() - 1; i >= 0; i--) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() == Opcodes.RETURN) {
				returnVoid = (InsnNode) instruction;
				break;
			}
		}

		final LabelNode returnLabel = new LabelNode();

		final VarInsnNode loadThis = new VarInsnNode(
				Opcodes.ALOAD,
				0
		);

		final VarInsnNode loadThis2 = new VarInsnNode(
				Opcodes.ALOAD,
				0
		);

		final FieldInsnNode getStatus = new FieldInsnNode(
				Opcodes.GETFIELD,
				"net/minecraft/entity/item/EntityBoat",
				STATUS,
				"Lnet/minecraft/entity/item/EntityBoat$Status;"
		);

		final MethodInsnNode onUpdate = new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				"com/therandomlabs/randompatches/patch/EntityBoatPatch",
				"onUpdate",
				"(Lnet/minecraft/entity/item/EntityBoat;" +
						"Lnet/minecraft/entity/item/EntityBoat$Status;)V",
				false
		);

		final FieldInsnNode getPreventEjection = new FieldInsnNode(
				Opcodes.GETSTATIC,
				"com/therandomlabs/randompatches/config/RPStaticConfig",
				"preventUnderwaterBoatPassengerEjection",
				"Z"
		);

		final JumpInsnNode returnIfNotTrue = new JumpInsnNode(
				Opcodes.IFEQ,
				returnLabel
		);

		final VarInsnNode loadThis3 = new VarInsnNode(
				Opcodes.ALOAD,
				0
		);

		final InsnNode loadZero = new InsnNode(
				Opcodes.FCONST_0
		);

		final FieldInsnNode setOutOfControlTicks = new FieldInsnNode(
				Opcodes.PUTFIELD,
				"net/minecraft/entity/item/EntityBoat",
				OUT_OF_CONTROL_TICKS,
				"F"
		);

		method.instructions.insertBefore(returnVoid, loadThis);
		method.instructions.insert(loadThis, loadThis2);
		method.instructions.insert(loadThis2, getStatus);
		method.instructions.insert(getStatus, onUpdate);
		method.instructions.insert(onUpdate, getPreventEjection);
		method.instructions.insert(getPreventEjection, returnIfNotTrue);
		method.instructions.insert(returnIfNotTrue, loadThis3);
		method.instructions.insert(loadThis3, loadZero);
		method.instructions.insert(loadZero, setOutOfControlTicks);
		method.instructions.insert(setOutOfControlTicks, returnLabel);
	}

	public static void onUpdate(EntityBoat boat, EntityBoat.Status status) {
		if(status != EntityBoat.Status.UNDER_WATER &&
				status != EntityBoat.Status.UNDER_FLOWING_WATER) {
			return;
		}

		boat.motionY += -VANILLA_UNDERWATER_BUOYANCY + RPStaticConfig.underwaterBoatBuoyancy;
	}
}
