package com.therandomlabs.randompatches.asm.transformer;

import com.therandomlabs.randompatches.asm.Transformer;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class LanguageListTransformer extends Transformer {
	public static final String REFRESH_RESOURCES = getName("refreshResources", "f");

	@Override
	public boolean transform(ClassNode node) {
		final MethodNode method = findMethod(node, "(IZII)V", "elementClicked", "a");

		if(method == null) {
			return false;
		}

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

		if(refreshResources == null) {
			return false;
		}

		final MethodInsnNode callReloadLanguage = new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				"com/therandomlabs/randompatches/asm/transformer/LanguageListTransformer",
				"reloadLanguage",
				"()V",
				false
		);

		method.instructions.insert(refreshResources, callReloadLanguage);

		final AbstractInsnNode previous = refreshResources.getPrevious();

		method.instructions.remove(previous.getPrevious());
		method.instructions.remove(previous);
		method.instructions.remove(refreshResources);

		return false;
	}

	public static void reloadLanguage() {
		final Minecraft mc = Minecraft.getMinecraft();
		mc.getLanguageManager().onResourceManagerReload(mc.getResourceManager());
	}
}
