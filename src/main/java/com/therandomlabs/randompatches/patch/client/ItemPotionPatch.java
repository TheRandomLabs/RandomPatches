package com.therandomlabs.randompatches.patch.client;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class ItemPotionPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		InsnList instructions = findInstructions(node, "hasEffect", "func_77962_s");

		if (instructions == null) {
			instructions = findInstructions(node, "hasEffect", "func_77636_d");
		}

		instructions.clear();

		//Get ItemStack
		instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));

		//Call ItemPotionHook#hasEffect
		instructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				hookClass,
				"hasEffect",
				"(Lnet/minecraft/item/ItemStack;)Z",
				false
		));

		//Return ItemPotionHook#hasEffect
		instructions.add(new InsnNode(Opcodes.IRETURN));

		return true;
	}
}
