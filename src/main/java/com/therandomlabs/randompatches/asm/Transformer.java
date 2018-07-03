package com.therandomlabs.randompatches.asm;

import com.therandomlabs.randompatches.RandomPatches;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class Transformer {
	public abstract boolean transform(ClassNode node);

	public static MethodNode findMethod(ClassNode node, String... names) {
		for(MethodNode method : node.methods) {
			for(String name : names) {
				if(name.equals(method.name)) {
					RandomPatches.LOGGER.debug("Patching method: " + method.name);
					return method;
				}
			}
		}

		return null;
	}

	public static MethodNode findUpdateMethod(ClassNode node) {
		MethodNode update = null;

		for(MethodNode method : node.methods) {
			if(method.name.equals("update")) {
				update = method;
				break;
			}

			if(method.desc.equals("()V") && !method.name.equals("b")) {
				update = method;
				break;
			}
		}

		if(update == null) {
			return null;
		}

		RandomPatches.LOGGER.debug("Patching method: " + update.name);

		return update;
	}

	public static String getName(String name, String obfuscatedName) {
		return RandomPatches.IS_DEOBFUSCATED ? name : obfuscatedName;
	}
}
