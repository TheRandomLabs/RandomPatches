package com.therandomlabs.randompatches.core.patch;

import com.therandomlabs.randompatches.core.Patch;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class LanguageListPatch extends Patch {
	public static final String REFRESH_RESOURCES = getName("refreshResources", "func_110436_a");

	@Override
	public void apply(ClassNode node) {
		final MethodNode method = findMethod(node, "elementClicked", "func_148144_a");
		MethodInsnNode refreshResources = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				refreshResources = (MethodInsnNode) instruction;

				if(REFRESH_RESOURCES.equals(refreshResources.name)) {
					break;
				}

				refreshResources = null;
			}
		}

		final MethodInsnNode callReloadLanguage = new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				"com/therandomlabs/randompatches/core/patch/LanguageListPatch",
				"reloadLanguage",
				"()V",
				false
		);

		method.instructions.insert(refreshResources, callReloadLanguage);

		final AbstractInsnNode previous = refreshResources.getPrevious();

		method.instructions.remove(previous.getPrevious());
		method.instructions.remove(previous);
		method.instructions.remove(refreshResources);
	}

	public static void reloadLanguage() {
		final Minecraft mc = Minecraft.getMinecraft();
		mc.getLanguageManager().onResourceManagerReload(mc.getResourceManager());
	}
}
