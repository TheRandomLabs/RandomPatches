package com.therandomlabs.randomlib.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.therandomlabs.randomlib.TRLUtils;

final class TRLCategory {
	final String fullyQualifiedName;
	final String languageKeyPrefix;
	final String languageKey;
	final Class<?> clazz;
	final String comment;
	final String name;
	final List<TRLProperty> properties = new ArrayList<>();

	final Method onReload;
	final Method onReloadClient;

	TRLCategory(
			String fullyQualifiedNamePrefix, String languageKeyPrefix, Class<?> clazz,
			String comment, String name
	) {
		fullyQualifiedName = fullyQualifiedNamePrefix + name;
		this.languageKeyPrefix = languageKeyPrefix;
		languageKey = languageKeyPrefix + name;
		this.clazz = clazz;
		this.comment = comment;
		this.name = TRLUtils.MC_VERSION_NUMBER == 8 ? name.toLowerCase(Locale.ENGLISH) : name;
		onReload = getOnReloadMethod(clazz, "onReload");
		onReloadClient = getOnReloadMethod(clazz, "onReloadClient");
	}

	void initialize(CommentedFileConfig config) {
		config.setComment(fullyQualifiedName, comment);
	}

	void onReload(boolean client) {
		final Method method = client ? onReloadClient : onReload;

		if(method != null) {
			try {
				method.invoke(null);
			} catch(IllegalAccessException | InvocationTargetException ex) {
				TRLUtils.crashReport("Failed to reload configuration category", ex);
			}
		}
	}

	String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	String getLanguageKeyPrefix() {
		return languageKey + ".";
	}

	private static Method getOnReloadMethod(Class<?> clazz, String name) {
		final Method onReload = TRLUtils.findMethod(clazz, name);

		if(onReload != null) {
			final int modifiers = onReload.getModifiers();

			if(!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) ||
					onReload.getReturnType() != void.class) {
				throw new IllegalArgumentException(name + " must be public static void");
			}
		}

		return onReload;
	}
}
