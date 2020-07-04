package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class EntityLivingBasePatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final MethodNode method = findMethod(node, "dismountEntity", "func_110145_l");
		method.localVariables.clear();
		final InsnList instructions = method.instructions;

		instructions.clear();

		instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		instructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				hookClass,
				"dismountEntity",
				"(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/Entity;)V",
				false
		));
		instructions.add(new InsnNode(Opcodes.RETURN));

		return true;
	}
}
