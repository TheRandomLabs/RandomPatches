package com.therandomlabs.randompatches.patch.client.dismount;

import com.therandomlabs.randomlib.TRLUtils;
import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;

public final class NetHandlerPlayClientPatch extends Patch {
	public static final String KEY_BIND_SNEAK = getName("keyBindSneak", "field_74311_E");
	public static final String HANDLE_SET_PASSENGERS_SRG =
			TRLUtils.MC_VERSION_NUMBER > 8 ? "func_184328_a" : "func_147243_a";

	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions =
				findInstructions(node, "handleSetPassengers", HANDLE_SET_PASSENGERS_SRG);

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
				getName(EntityPlayerSPPatch.class) + "$DismountKeybind",
				"keybind",
				"Lnet/minecraft/client/settings/KeyBinding;"
		));

		final AbstractInsnNode getGameSettings = getSneakKeybind.getPrevious();

		if(TRLUtils.MC_VERSION_NUMBER > 8) {
			final AbstractInsnNode getMinecraft = getGameSettings.getPrevious();

			instructions.remove(getMinecraft.getPrevious());
			instructions.remove(getMinecraft);
		}

		instructions.remove(getGameSettings);
		instructions.remove(getSneakKeybind);

		return true;
	}
}
