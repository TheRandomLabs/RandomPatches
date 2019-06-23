package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class NextTickListEntryPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "compareTo");
		final InsnList newInstructions = new InsnList();

		final LabelNode continueLabel = new LabelNode();

		//Get NextTickListEntry (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get other NextTickListEntry (p_compareTo_1_)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));

		//Call NextTickListEntry#equals
		newInstructions.add(new MethodInsnNode(
				Opcodes.INVOKEVIRTUAL,
				"net/minecraft/world/NextTickListEntry",
				"equals",
				"(Ljava/lang/Object;)Z",
				false
		));

		//If NextTickListEntry#equals returns false, continue
		newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, continueLabel));

		//Load 0
		newInstructions.add(new InsnNode(Opcodes.ICONST_0));

		//Return 0
		newInstructions.add(new InsnNode(Opcodes.IRETURN));

		newInstructions.add(continueLabel);

		instructions.insertBefore(instructions.getFirst(), newInstructions);

		return true;
	}
}
