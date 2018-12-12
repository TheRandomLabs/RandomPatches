package com.therandomlabs.randompatches.core;

import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.config.RPStaticConfig;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class Patch {
	public static final String RPSTATICCONFIG = getName(RPStaticConfig.class);

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

	public static String getName(Class<?> clazz) {
		return StringUtils.replaceChars(clazz.getName(), '.', '/');
	}
}
