package com.therandomlabs.randompatches.core;

import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.ClassWriter;

public class RPClassWriter extends ClassWriter {
	public RPClassWriter(int flags) {
		super(flags);
	}

	@Override
	protected String getCommonSuperClass(String type1, String type2) {
		Class<?> c;
		final Class<?> d;

		try {
			c = Class.forName(type1.replace('/', '.'), false, Launch.classLoader);
			d = Class.forName(type2.replace('/', '.'), false, Launch.classLoader);
		} catch (Exception ex) {
			throw new IllegalArgumentException(
					"Could not get common superclass of " + type1 + " and " + type2,
					ex
			);
		}

		if (c.isAssignableFrom(d)) {
			return type1;
		}

		if (d.isAssignableFrom(c)) {
			return type2;
		}

		if (c.isInterface() || d.isInterface()) {
			return "java/lang/Object";
		}

		do {
			c = c.getSuperclass();
		} while (!c.isAssignableFrom(d));

		return c.getName().replace('.', '/');
	}
}
