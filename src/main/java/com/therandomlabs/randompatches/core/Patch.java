package com.therandomlabs.randompatches.core;

import com.therandomlabs.randompatches.RandomPatches;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class Patch {
	public abstract void apply(ClassNode node);

	public static MethodNode findMethod(ClassNode node, String name) {
		return findMethod(node, name, name);
	}

	public static MethodNode findMethod(ClassNode node, String name, String srgName) {
		name = getName(name, srgName);

		for(MethodNode method : node.methods) {
			if(name.equals(method.name)) {
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
