package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class BlockObserverPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "onBlockAdded", "func_176213_c");

		instructions.clear();

		instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
		instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));

		instructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				hookClass,
				"onBlockAdded",
				"(Lnet/minecraft/block/BlockObserver;Lnet/minecraft/world/World;Lnet/minecraft/" +
						"util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V",
				false
		));

		instructions.add(new InsnNode(Opcodes.RETURN));

		return true;
	}
}
