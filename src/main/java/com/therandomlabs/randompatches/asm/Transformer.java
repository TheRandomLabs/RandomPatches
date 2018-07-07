package com.therandomlabs.randompatches.asm;

import com.therandomlabs.randompatches.RandomPatches;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class Transformer {
	public abstract boolean transform(ClassNode node);

	public static MethodNode findMethod(ClassNode node, String name) {
		for(MethodNode method : node.methods) {
			if(name.equals(method.name)) {
				RandomPatches.LOGGER.debug("Patching method: " + method.name);
				return method;
			}
		}

		return null;
	}

	public static MethodNode findMethod(ClassNode node, String descriptor, String... names) {
		for(MethodNode method : node.methods) {
			for(String name : names) {
				if(name.equals(method.name) && descriptor.equals(method.desc)) {
					RandomPatches.LOGGER.debug("Patching method: " + method.name);
					return method;
				}
			}
		}

		return null;
	}

	public static String getName(String name, String obfuscatedName) {
		return RandomPatches.IS_DEOBFUSCATED ? name : obfuscatedName;
	}

	public static String getName(String name, String oneTen, String oneEleven, String oneTwelve) {
		if(RandomPatches.IS_DEOBFUSCATED) {
			return name;
		}

		if(RandomPatches.IS_ONE_TEN) {
			return oneTen;
		}

		if(RandomPatches.IS_ONE_ELEVEN) {
			return oneEleven;
		}

		return oneTwelve;
	}
}
