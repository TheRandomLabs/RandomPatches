package com.therandomlabs.randompatches.patch.packetsize;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;

public final class PacketBufferPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "readCompoundTag", "func_150793_b");
		LdcInsnNode limit = null;

		for(int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if(instruction.getOpcode() == Opcodes.LDC) {
				limit = (LdcInsnNode) instruction;
				break;
			}
		}

		//Get RPConfig.Misc#packetSizeLimit
		instructions.insert(limit, new FieldInsnNode(
				Opcodes.GETSTATIC,
				NettyCompressionDecoderPatch.MISC_CONFIG,
				"packetSizeLimitLong",
				"J"
		));

		instructions.remove(limit);

		return true;
	}
}
