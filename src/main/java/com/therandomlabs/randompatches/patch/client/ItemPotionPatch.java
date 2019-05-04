package com.therandomlabs.randompatches.patch.client;

import com.therandomlabs.randompatches.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class ItemPotionPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final MethodNode method = findMethod(node, "hasEffect", "func_77962_s");

		final InsnList list = new InsnList();

		list.add(new InsnNode(Opcodes.ICONST_0));
		list.add(new InsnNode(Opcodes.IRETURN));

		method.instructions = list;

		return true;
	}
}
