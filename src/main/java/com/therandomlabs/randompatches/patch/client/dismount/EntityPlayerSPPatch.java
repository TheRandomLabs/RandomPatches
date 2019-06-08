package com.therandomlabs.randompatches.patch.client.dismount;

import com.therandomlabs.randompatches.core.Patch;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

public final class EntityPlayerSPPatch extends Patch {
	public static final class DismountKeybind {
		public static KeyBinding keybind;

		private DismountKeybind() {}

		public static void register() {
			keybind = new KeyBinding("key.dismount", Keyboard.KEY_Z, "key.categories.movement");
			ClientRegistry.registerKeyBinding(keybind);
		}
	}

	public static final String SNEAK = getName("sneak", "field_78899_d");

	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "onUpdate", "func_70071_h_");
		FieldInsnNode shouldDismount = null;

		for(int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if(instruction.getOpcode() == Opcodes.GETFIELD) {
				shouldDismount = (FieldInsnNode) instruction;

				if(SNEAK.equals(shouldDismount.name)) {
					break;
				}

				shouldDismount = null;
			}
		}

		//Call EntityPlayerSPPPatch#shouldDismount
		instructions.insert(shouldDismount, new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				getName(EntityPlayerSPPatch.class),
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

	public static boolean shouldDismount() {
		return DismountKeybind.keybind.isKeyDown();
	}
}
