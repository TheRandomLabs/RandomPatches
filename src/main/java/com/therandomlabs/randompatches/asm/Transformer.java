package com.therandomlabs.randompatches.asm;

import com.therandomlabs.randompatches.RandomPatches;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class Transformer {
	public abstract void transform(ClassNode node);

	public static MethodNode findMethod(ClassNode node, String name) {
		for(MethodNode method : node.methods) {
			if(name.equals(method.name)) {
				RandomPatches.LOGGER.debug("Patching method: " + method.name);
				return method;
			}
		}

		return null;
	}

	public static MethodNode findMethod(ClassNode node, String name, String srgName) {
		for(MethodNode method : node.methods) {
			if(name.equals(method.name) || srgName.equals(method.name)) {
				RandomPatches.LOGGER.debug("Patching method: " + method.name);
				return method;
			}
		}

		return null;
	}

	public static String getName(String name, String srgName) {
		return RandomPatches.IS_DEOBFUSCATED ? name : srgName;
	}
}
