package com.therandomlabs.randompatches.patch.packetsize;

import com.therandomlabs.randompatches.config.RPConfig;
import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;

public final class NettyCompressionDecoderPatch extends Patch {
	public static final String MISC_CONFIG = getName(RPConfig.Misc.class);
	public static final int VANILLA_LIMIT = 0x200000;

	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "decode");

		LdcInsnNode limit1 = null;
		LdcInsnNode limit2 = null;

		for(int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if(instruction.getOpcode() == Opcodes.LDC) {
				final LdcInsnNode ldc = (LdcInsnNode) instruction;

				if(((Integer) VANILLA_LIMIT).equals(ldc.cst)) {
					if(limit1 == null) {
						limit1 = ldc;
					} else {
						limit2 = ldc;
						break;
					}
				}
			}
		}

		//Get RPConfig.Misc#packetSizeLimit
		instructions.insert(limit1, new FieldInsnNode(
				Opcodes.GETSTATIC,
				MISC_CONFIG,
				"packetSizeLimit",
				"I"
		));

		instructions.remove(limit1);

		//Get RPConfig.Misc#packetSizeLimit
		instructions.insert(limit2, new FieldInsnNode(
				Opcodes.GETSTATIC,
				MISC_CONFIG,
				"packetSizeLimit",
				"I"
		));

		instructions.remove(limit2);

		return true;
	}
}
