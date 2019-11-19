package com.therandomlabs.randompatches.patch.client;

import com.therandomlabs.randompatches.config.RPConfig;
import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;

public final class OptionsPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "<clinit>");
		AbstractInsnNode framerateLimit = null;

		for (int i = 0; i < instructions.size(); i++) {
			framerateLimit = instructions.get(i);

			if(framerateLimit.getOpcode() == Opcodes.LDC &&
					"options.framerateLimit".equals(((LdcInsnNode) framerateLimit).cst)) {
				break;
			}

			framerateLimit = null;
		}

		/*
		LDC "options.framerateLimit"
		ICONST_1
		ICONST_0
		LDC 10.0
		LDC 260.0
		LDC 10.0
		*/

		final AbstractInsnNode stepSize =
				framerateLimit.getNext().getNext().getNext().getNext().getNext();

		//Get RPConfig.Client#framerateLimitSliderStepSize
		instructions.insert(stepSize, new FieldInsnNode(
				Opcodes.GETSTATIC,
				getName(RPConfig.Client.class),
				"framerateLimitSliderStepSize",
				"F"
		));

		instructions.remove(stepSize);

		return true;
	}
}
