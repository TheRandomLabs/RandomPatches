package com.therandomlabs.randompatches.patch.client;

import com.therandomlabs.randompatches.core.Patch;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

public final class GuiLanguageListPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "elementClicked", "func_148144_a");
		MethodInsnNode refreshResources = null;

		for (int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				refreshResources = (MethodInsnNode) instruction;

				//On 1.8, the vanilla refreshResources is called
				if ("refreshResources".equals(refreshResources.name) ||
						"func_110436_a".equals(refreshResources.name)) {
					break;
				}

				refreshResources = null;
			}
		}

		//Call GuiLanguageListPatch#reloadLanguage
		instructions.insert(refreshResources, new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				getName(GuiLanguageListPatch.class),
				"reloadLanguage",
				"()V",
				false
		));

		final AbstractInsnNode previous = refreshResources.getPrevious();

		//It works, don't question
		instructions.remove(previous.getPrevious());
		instructions.remove(previous);
		instructions.remove(refreshResources);

		return true;
	}

	public static void reloadLanguage() {
		final Minecraft mc = Minecraft.getMinecraft();
		mc.getLanguageManager().onResourceManagerReload(mc.getResourceManager());
	}
}
