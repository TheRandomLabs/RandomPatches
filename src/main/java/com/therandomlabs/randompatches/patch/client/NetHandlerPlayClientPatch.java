package com.therandomlabs.randompatches.patch.client;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;

public final class NetHandlerPlayClientPatch extends Patch {
	public static final String KEY_BIND_SNEAK = getName("keyBindSneak", "field_74311_E");

	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions =
				findInstructions(node, "handleSetPassengers", "func_184328_a");

		FieldInsnNode getSneakKeybind = null;

		for(int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if(instruction.getOpcode() == Opcodes.GETFIELD) {
				getSneakKeybind = (FieldInsnNode) instruction;

				if(KEY_BIND_SNEAK.equals(getSneakKeybind.name)) {
					break;
				}

				getSneakKeybind = null;
			}
		}

		//Get EntityPlayerSPPatch.DismountKeybind#keybind
		//We do this so the dismount key is shown instead of the sneak key in
		//"Press <key> to dismount"
		instructions.insert(getSneakKeybind, new FieldInsnNode(
				Opcodes.GETSTATIC,
				"com/therandomlabs/randompatches/patch/client/EntityPlayerSPPatch$DismountKeybind",
				"keybind",
				"Lnet/minecraft/client/settings/KeyBinding;"
		));

		final AbstractInsnNode getGameSettings = getSneakKeybind.getPrevious();
		final AbstractInsnNode getMinecraft = getGameSettings.getPrevious();

		instructions.remove(getMinecraft.getPrevious());
		instructions.remove(getMinecraft);
		instructions.remove(getGameSettings);
		instructions.remove(getSneakKeybind);

		return true;
	}
}
