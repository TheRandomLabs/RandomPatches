package com.therandomlabs.randompatches.patch.client;

import com.therandomlabs.randompatches.Patch;
import com.therandomlabs.randompatches.RPConfig;
import com.therandomlabs.randompatches.RandomPatches;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class MainWindowPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		if(RandomPatches.DEFAULT_WINDOW_TITLE.equals(RPConfig.Window.title)) {
			return false;
		}

		final MethodNode method = findMethod(node, "<init>");
		LdcInsnNode ldc = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() == Opcodes.LDC) {
				ldc = (LdcInsnNode) instruction;

				if(ldc.cst.equals(RandomPatches.DEFAULT_WINDOW_TITLE)) {
					break;
				}

				ldc = null;
			}
		}

		ldc.cst = RPConfig.Window.title;
		return true;
	}
}
