package com.therandomlabs.randompatches.asm;

import com.therandomlabs.randompatches.RandomPatches;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class Transformer {
	protected Transformer() {}

	public abstract boolean transform(ClassNode node);

	public static MethodNode findMethod(ClassNode node, String... names) {
		for(MethodNode methodNode : node.methods) {
			for(String name : names) {
				if(name.equals(methodNode.name)) {
					RandomPatches.LOGGER.debug("Patching method: " + methodNode.name);
					return methodNode;
				}
			}
		}

		return null;
	}

	public static MethodNode findUpdateMethod(ClassNode node) {
		MethodNode update = null;

		for(MethodNode methodNode : node.methods) {
			if(methodNode.name.equals("update")) {
				update = methodNode;
				break;
			}

			if(methodNode.desc.equals("()V") && !methodNode.name.equals("b")) {
				update = methodNode;
				break;
			}
		}

		if(update == null) {
			return null;
		}

		RandomPatches.LOGGER.debug("Patching method: " + update.name);

		return update;
	}

	public static String getFieldName(String name, String obfuscatedName) {
		return RandomPatches.IS_DEOBFUSCATED ? name : obfuscatedName;
	}
}
