package com.therandomlabs.randomlib.config;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;

final class ConfigData {
	final List<String> comment;
	final Class<?> clazz;
	final String pathString;
	final Path path;
	final List<TRLCategory> categories;
	final CommentedFileConfig config;
	final Map<String, Object> delayedLoad = new HashMap<>();

	ConfigData(
			String[] comment, Class<?> clazz, String pathString, Path path,
			List<TRLCategory> categories
	) {
		this.comment = Arrays.stream(comment).map(line -> "# " + line).collect(Collectors.toList());
		this.clazz = clazz;
		this.pathString = pathString;
		this.path = path;
		this.categories = categories;
		config = CommentedFileConfig.builder(path).build();
	}
}
