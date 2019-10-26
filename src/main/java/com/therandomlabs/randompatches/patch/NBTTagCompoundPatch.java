package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

public final class NBTTagCompoundPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "equals");

		MethodInsnNode entrySet1 = null;
		MethodInsnNode entrySet2 = null;
		MethodInsnNode equals = null;

		for (int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);
			final int opcode = instruction.getOpcode();

			//On 1.11 and below, it's an invokeinterface (Set#equals)
			//On 1.12 and above, it's an invokestatic (Objects#equals)
			if (opcode != Opcodes.INVOKESTATIC && opcode != Opcodes.INVOKEINTERFACE) {
				continue;
			}

			final MethodInsnNode methodInsnNode = (MethodInsnNode) instruction;

			if (entrySet1 == null) {
				if ("entrySet".equals(methodInsnNode.name)) {
					entrySet1 = methodInsnNode;
				}

				continue;
			}

			if (entrySet2 == null) {
				if ("entrySet".equals(methodInsnNode.name)) {
					entrySet2 = methodInsnNode;
				}

				continue;
			}

			if ("equals".equals(methodInsnNode.name)) {
				equals = methodInsnNode;
				break;
			}
		}

		instructions.remove(entrySet1);
		instructions.remove(entrySet2);

		//Call NBTTagCompoundHook#areTagMapsEqual
		equals.setOpcode(Opcodes.INVOKESTATIC);
		equals.owner = hookClass;
		equals.name = "areTagMapsEqual";
		equals.desc = "(Ljava/util/Map;Ljava/util/Map;)Z";
		equals.itf = false;

		return true;
	}
}
