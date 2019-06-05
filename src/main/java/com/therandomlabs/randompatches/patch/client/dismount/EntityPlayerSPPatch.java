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
		FieldInsnNode shouldSneak = null;

		for(int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if(instruction.getOpcode() == Opcodes.GETFIELD) {
				shouldSneak = (FieldInsnNode) instruction;

				if(SNEAK.equals(shouldSneak.name)) {
					break;
				}

				shouldSneak = null;
			}
		}

		//Call EntityPlayerSPPPatch#shouldSneak
		instructions.insert(shouldSneak, new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				getName(EntityPlayerSPPatch.class),
				"shouldSneak",
				"()Z",
				false
		));

		final AbstractInsnNode getMovementInput = shouldSneak.getPrevious();

		instructions.remove(getMovementInput.getPrevious());
		instructions.remove(getMovementInput);
		instructions.remove(shouldSneak);

		return true;
	}

	public static boolean shouldSneak() {
		return DismountKeybind.keybind.isKeyDown();
	}
}
