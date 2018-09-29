package com.therandomlabs.randompatches.core.transformer;

import com.therandomlabs.randompatches.core.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ItemPotionTransformer extends Transformer {
	@Override
	public void transform(ClassNode node) {
		MethodNode method = findMethod(node, "hasEffect", "func_77962_s");

		if(method == null) {
			method = findMethod(node, "hasEffect", "func_77636_d");
		}

		final InsnList list = new InsnList();

		list.add(new InsnNode(Opcodes.ICONST_0));
		list.add(new InsnNode(Opcodes.IRETURN));

		method.instructions = list;
	}
}
