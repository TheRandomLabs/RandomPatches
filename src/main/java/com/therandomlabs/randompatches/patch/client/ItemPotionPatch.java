package com.therandomlabs.randompatches.patch.client;

import com.therandomlabs.randompatches.config.RPConfig;
import com.therandomlabs.randompatches.core.Patch;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class ItemPotionPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		MethodNode method = findMethod(node, "hasEffect", "func_77962_s");

		if(method == null) {
			method = findMethod(node, "hasEffect", "func_77636_d");
		}

		method.instructions.clear();

		method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		method.instructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				getName(ItemPotionPatch.class),
				"hasEffect",
				"(Lnet/minecraft/item/ItemStack;)Z",
				false
		));
		method.instructions.add(new InsnNode(Opcodes.IRETURN));

		return true;
	}

	public static boolean hasEffect(ItemStack stack) {
		if(stack.isItemEnchanted()) {
			return true;
		}

		return !RPConfig.Client.removePotionGlint &&
				!PotionUtils.getEffectsFromStack(stack).isEmpty();
	}
}
