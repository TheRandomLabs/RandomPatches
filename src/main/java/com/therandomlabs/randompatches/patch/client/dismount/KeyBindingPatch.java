package com.therandomlabs.randompatches.patch.client.dismount;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class KeyBindingPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "conflicts");

		final LabelNode label = new LabelNode();

		final InsnList newInstructions = new InsnList();

		//Get KeyBinding (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get binding (other KeyBinding)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));

		//Call EntityPlayerSPHook.DismountKeybind#isDismountAndSneak
		newInstructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				EntityPlayerSPPatch.INSTANCE.getHookInnerClass("DismountKeybind"),
				"isDismountAndSneak",
				"(Lnet/minecraft/client/settings/KeyBinding;" +
						"Lnet/minecraft/client/settings/KeyBinding;)Z",
				false
		));

		//If false, continue
		newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, label));

		//Otherwise, load false
		newInstructions.add(new InsnNode(Opcodes.ICONST_0));

		//Then return
		newInstructions.add(new InsnNode(Opcodes.IRETURN));

		newInstructions.add(label);

		instructions.insertBefore(instructions.getFirst(), newInstructions);

		return true;
	}
}
