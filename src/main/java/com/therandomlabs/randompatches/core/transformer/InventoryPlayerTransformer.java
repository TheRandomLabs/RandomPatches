package com.therandomlabs.randompatches.core.transformer;

import com.therandomlabs.randompatches.core.Transformer;
import jdk.internal.org.objectweb.asm.Opcodes;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class InventoryPlayerTransformer extends Transformer {
	@Override
	public void transform(ClassNode node) {
		final MethodNode method = findMethod(node, "findSlotMatchingUnusedItem", "func_194014_c");

		MethodInsnNode stackEqualExact = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() == Opcodes.INVOKESPECIAL) {
				stackEqualExact = (MethodInsnNode) instruction;
				break;
			}
		}

		stackEqualExact.setOpcode(Opcodes.INVOKESTATIC);
		stackEqualExact.owner =
				"com/therandomlabs/randompatches/core/transformer/InventoryPlayerTransformer";
		stackEqualExact.name = "stackEqualExact";
		stackEqualExact.desc =
				"(Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z";
	}

	@SuppressWarnings("unused")
	public static boolean stackEqualExact(Object inventory, ItemStack stack1, ItemStack stack2) {
		return stack1.getItem() == stack2.getItem() && (stack1.getMetadata() == Short.MAX_VALUE ||
				stack1.getMetadata() == stack2.getMetadata());
	}
}
