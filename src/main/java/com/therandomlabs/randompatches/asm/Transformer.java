package com.therandomlabs.randompatches.asm;

import java.util.regex.Pattern;
import com.therandomlabs.randompatches.RandomPatches;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class Transformer {
	public abstract boolean transform(ClassNode node);

	public static MethodNode findMethod(ClassNode node, String descriptor, String name,
			String obfuscatedName) {
		return findMethod(node, descriptor, null, name, obfuscatedName);
	}

	public static MethodNode findMethod(ClassNode node, String descriptor,
			Pattern obfuscatedDescriptor, String name, String obfuscatedName) {
		for(MethodNode method : node.methods) {
			if(name.equals(method.name) || obfuscatedName.equals(method.name)) {
				if(descriptor.equals(method.desc) || (obfuscatedDescriptor != null &&
						obfuscatedDescriptor.matcher(method.desc).matches())) {
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
