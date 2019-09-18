package com.therandomlabs.randomlib.config.adapter;

import java.util.Arrays;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.therandomlabs.randomlib.TRLUtils;

public interface TypeAdapter {
	default Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
		return config.get(name);
	}

	default void setValue(CommentedFileConfig config, String name, Object value) {
		if(isArray()) {
			config.set(name, Arrays.asList(TRLUtils.toBoxedArray(value)));
		} else {
			config.set(name, value);
		}
	}

	default String asString(Object value) {
		return String.valueOf(value);
	}

	default boolean isArray() {
		return false;
	}

	default boolean shouldLoad() {
		return true;
	}
}
