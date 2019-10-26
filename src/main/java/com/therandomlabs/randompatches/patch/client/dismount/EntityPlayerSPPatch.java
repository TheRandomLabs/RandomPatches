package com.therandomlabs.randompatches.patch.client.dismount;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

public final class EntityPlayerSPPatch extends Patch {
	//So that KeyBindingPatch and NetHandlerPlayClientPatch can easily get the name of
	//EntityPlayerSP$DismountKeybind
	public static final EntityPlayerSPPatch INSTANCE = new EntityPlayerSPPatch();
	public static final String SNEAK = getName("sneak", "field_78899_d");

	private EntityPlayerSPPatch() {}

	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "onUpdate", "func_70071_h_");
		FieldInsnNode shouldDismount = null;

		for (int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if (instruction.getOpcode() == Opcodes.GETFIELD) {
				shouldDismount = (FieldInsnNode) instruction;

				if (SNEAK.equals(shouldDismount.name)) {
					break;
				}

				shouldDismount = null;
			}
		}

		//Call EntityPlayerSPHook#shouldDismount
		instructions.insert(shouldDismount, new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				hookClass,
				"shouldDismount",
				"()Z",
				false
		));

		final AbstractInsnNode getMovementInput = shouldDismount.getPrevious();

		instructions.remove(getMovementInput.getPrevious());
		instructions.remove(getMovementInput);
		instructions.remove(shouldDismount);

		return true;
	}
}
