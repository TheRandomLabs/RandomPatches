package com.therandomlabs.randomlib.config;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;

final class ConfigData {
	final Class<?> clazz;
	final String pathString;
	final Path path;
	final List<TRLCategory> categories;
	final CommentedFileConfig config;
	final Map<String, Object> delayedLoad = new HashMap<>();

	ConfigData(Class<?> clazz, String pathString, Path path, List<TRLCategory> categories) {
		this.clazz = clazz;
		this.pathString = pathString;
		this.path = path;
		this.categories = categories;
		config = CommentedFileConfig.builder(path).build();
	}
}
