/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 TheRandomLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.therandomlabs.randompatches;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.ForceBreakdown;
import com.electronwill.nightconfig.core.conversion.InvalidValueException;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.conversion.SpecEnum;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.base.CaseFormat;
import me.shedaniel.autoconfig1u.AutoConfig;
import me.shedaniel.autoconfig1u.ConfigData;
import me.shedaniel.autoconfig1u.ConfigManager;
import me.shedaniel.autoconfig1u.annotation.ConfigEntry;
import me.shedaniel.autoconfig1u.serializer.ConfigSerializer;
import me.shedaniel.autoconfig1u.util.Utils;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A {@link ConfigSerializer} for TOML that uses NightConfig.
 * <p>
 * Compared to {@link me.shedaniel.autoconfig1u.serializer.Toml4jConfigSerializer},
 * this {@link ConfigSerializer} ensures that {@link ConfigData#validatePostLoad()} is always
 * called and any updated values are written to disk on both serialization and deserialization.
 * <p>
 * In addition, NightConfig's {@code Spec*} annotations are supported, and invalid values
 * are automatically reset to the defaults.
 * <p>
 * Furthermore, lower_snake_case for key names is enforced, and comments for properties, categories
 * and configuration files may be specified through the use of {@link Comment}.
 * <p>
 * {@link #reloadFromDisk()} may be used to reload the configuration from disk if a reference to
 * the {@link TOMLConfigSerializer} is stored.
 *
 * @param <T> the configuration type.
 */
@SuppressWarnings("DuplicatedCode")
public final class TOMLConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {
	/**
	 * A TOML configuration comment.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Comment {
		/**
		 * The lines of this comment.
		 *
		 * @return an array of strings containing the lines of this comment.
		 */
		String[] value();
	}

	private static final Logger logger = LogManager.getLogger();

	//Used to access private utility methods through reflection.
	private static final ObjectConverter objectConverter = new ObjectConverter();

	private static final Method mustPreserve;
	private static final Method getConverter;
	private static final Method getPath;
	private static final Method checkField;

	private static final Method bottomElementType;
	private static final Method elementTypes;

	private static final Method load;

	private final Class<T> configClass;
	private final CommentedFileConfig fileConfig;

	private T config;

	static {
		//Preserve declaration order.
		Config.setInsertionOrderPreserved(true);

		Class<?> annotationUtils;

		try {
			annotationUtils = Class.forName(
					"com.electronwill.nightconfig.core.conversion.AnnotationUtils"
			);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}

		mustPreserve = ObfuscationReflectionHelper.findMethod(
				annotationUtils, "mustPreserve", Field.class, Class.class
		);
		getConverter = ObfuscationReflectionHelper.findMethod(
				annotationUtils, "getConverter", Field.class
		);
		getPath = ObfuscationReflectionHelper.findMethod(annotationUtils, "getPath", Field.class);
		checkField = ObfuscationReflectionHelper.findMethod(
				annotationUtils, "checkField", Field.class, Object.class
		);

		bottomElementType = ObfuscationReflectionHelper.findMethod(
				ObjectConverter.class, "bottomElementType", Collection.class
		);
		elementTypes = ObfuscationReflectionHelper.findMethod(
				ObjectConverter.class, "elementTypes", ParameterizedType.class
		);

		load = ObfuscationReflectionHelper.findMethod(ConfigManager.class, "load");
	}

	/**
	 * Constructs a {@link TOMLConfigSerializer} with the specified definition and
	 * configuration class.
	 *
	 * @param definition a definition.
	 * @param configClass a configuration class.
	 */
	public TOMLConfigSerializer(
			me.shedaniel.autoconfig1u.annotation.Config definition, Class<T> configClass
	) {
		this.configClass = configClass;
		this.fileConfig = CommentedFileConfig.of(
				FMLPaths.CONFIGDIR.get().toAbsolutePath().resolve(definition.name() + ".toml")
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void serialize(T config) {
		try {
			final T defaultConfig = createDefault();

			//This resets any invalid values to the ones found in createDefault().
			moveToFileConfig(config, configClass, fileConfig, defaultConfig);
			//Move data from the CommentedFileConfig back to the config so that reset values
			//are updated.
			moveToObjectConfig(fileConfig, config, configClass, defaultConfig);

			this.config = validateAndSave(config, defaultConfig);
		} catch (RuntimeException | IllegalAccessException | InvocationTargetException |
				IOException ex) {
			//We throw a RuntimeException instead of a SerializationException so that errors
			//can be detected more easily.
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T deserialize() {
		T config = createDefault();

		if (!Files.exists(fileConfig.getNioPath())) {
			return config;
		}

		final T defaultConfig = createDefault();

		try {
			fileConfig.load();
			moveToObjectConfig(fileConfig, config, configClass, defaultConfig);
			this.config = validateAndSave(config, defaultConfig);
			return this.config;
		} catch (RuntimeException | IllegalAccessException | InvocationTargetException |
				IOException ex) {
			//We throw a RuntimeException instead of a SerializationException so that errors
			//can be detected more easily.
			throw new RuntimeException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T createDefault() {
		return Utils.constructUnsafely(configClass);
	}

	/**
	 * Returns this {@link TOMLConfigSerializer}'s configuration.
	 *
	 * @return this {@link TOMLConfigSerializer}'s configuration.
	 */
	public T getConfig() {
		return config;
	}

	/**
	 * Reloads this {@link TOMLConfigSerializer}'s configuration from disk.
	 */
	public void reloadFromDisk() {
		try {
			load.invoke(AutoConfig.getConfigHolder(configClass));
		} catch (IllegalAccessException | InvocationTargetException ex) {
			throw new RuntimeException("Failed to reload from disk: " + configClass, ex);
		}
	}

	private T validateAndSave(T config, Object defaultConfig)
			throws IllegalAccessException, InvocationTargetException, IOException {
		config = validate(config);
		//Reset the file configuration before moving data to it.
		fileConfig.entrySet().clear();
		moveToFileConfig(config, configClass, fileConfig, defaultConfig);
		fileConfig.save();

		String string = FileUtils.readFileToString(
				fileConfig.getFile(), StandardCharsets.UTF_8
		).trim() + System.lineSeparator();

		//Add top comment.
		if (configClass.isAnnotationPresent(Comment.class)) {
			final String[] lines = configClass.getAnnotation(Comment.class).value();
			final String comment = Arrays.stream(lines).
					map(line -> "# " + line).
					collect(Collectors.joining(System.lineSeparator()));
			string = comment + System.lineSeparator() + System.lineSeparator() + string;
		}

		FileUtils.write(fileConfig.getFile(), string, StandardCharsets.UTF_8);
		return config;
	}

	@SuppressWarnings("PMD.PreserveStackTrace")
	private T validate(T config) {
		try {
			config.validatePostLoad();
		} catch (ConfigData.ValidationException ex) {
			logger.error("Failed to load config '{}'. Using default!", configClass, ex);
			config = createDefault();

			try {
				config.validatePostLoad();
			} catch (ConfigData.ValidationException ex2) {
				throw new RuntimeException("Result of createDefault() was invalid!", ex2);
			}
		}

		return config;
	}

	//Taken and adapted from ObjectConverter.
	@SuppressWarnings({"unchecked", "PMD.PreserveStackTrace"})
	private void moveToFileConfig(
			Object object, Class<?> clazz, CommentedConfig destination, Object defaultConfig
	) throws IllegalAccessException, InvocationTargetException {
		while (clazz != Object.class) {
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(ConfigEntry.Gui.Excluded.class)) {
					continue;
				}

				/* Check modifiers. */
				final int fieldModifiers = field.getModifiers();

				if (Modifier.isTransient(fieldModifiers)) {
					continue;
				}

				if (!field.isAccessible()) {
					field.setAccessible(true);
				}

				/* Get value. */
				Object value = checkField(field, field.get(object), defaultConfig);

				final Converter<Object, Object> converter =
						(Converter<Object, Object>) getConverter.invoke(null, field);

				if (converter != null) {
					value = converter.convertFromField(value);
				}

				/* Write to config. */
				final List<String> path = getPath(field);
				final ConfigFormat<?> format = destination.configFormat();

				if (value == null) {
					destination.set(path, null);
				} else {
					final Class<?> valueType = value.getClass();

					if (Enum.class.isAssignableFrom(valueType)) {
						if (destination.configFormat().supportsType(Enum.class)) {
							destination.set(path, value);
						} else {
							destination.set(path, value.toString());
						}
					} else if (field.isAnnotationPresent(ForceBreakdown.class) ||
							!format.supportsType(valueType)) {
						//This is a category.
						destination.set(path, value);
						final CommentedConfig converted = destination.createSubConfig();

						if (value instanceof ConfigData) {
							try {
								((ConfigData) value).validatePostLoad();
							} catch (ConfigData.ValidationException ex) {
								logger.error(
										"Failed to load '{}' in config '{}. Using default!",
										path, configClass, ex
								);

								value = field.get(defaultConfig);

								try {
									((ConfigData) value).validatePostLoad();
								} catch (ConfigData.ValidationException ex2) {
									throw new RuntimeException(
											"Result of createDefault() was invalid!", ex2
									);
								}
							}
						}

						moveToFileConfig(value, valueType, converted, field.get(defaultConfig));
						destination.set(path, converted);
					} else if (value instanceof Collection) {
						//Ensure ConfigFormat supports collection element type.
						Collection<?> source = (Collection<?>) value;
						Class<?> bottomType =
								(Class<?>) bottomElementType.invoke(objectConverter, source);

						if (format.supportsType(bottomType)) {
							destination.set(path, value);
						} else {
							//AutoConfig doesn't support collections of objects.
							throw new UnsupportedOperationException(
									"Collections of objects are not supported!"
							);
						}
					} else {
						destination.set(path, value);
					}
				}

				if (field.isAnnotationPresent(Comment.class)) {
					final String[] lines = field.getAnnotation(Comment.class).value();
					final String comment = Arrays.stream(lines).
							map(line -> " " + line).
							collect(Collectors.joining(System.lineSeparator()));
					destination.setComment(path, comment);
				}
			}

			clazz = clazz.getSuperclass();
		}
	}

	//Taken and adapted from ObjectConverter.
	@SuppressWarnings({"rawtypes", "unchecked", "PMD.PreserveStackTrace"})
	private void moveToObjectConfig(
			UnmodifiableConfig config, Object object, Class<?> clazz, Object defaultConfig
	) throws IllegalAccessException, InvocationTargetException {
		while (clazz != Object.class) {
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(ConfigEntry.Gui.Excluded.class)) {
					continue;
				}

				/* Check modifiers. */
				final int fieldModifiers = field.getModifiers();

				if (Modifier.isTransient(fieldModifiers)) {
					continue;
				}

				if (!field.isAccessible()) {
					field.setAccessible(true);
				}

				/* Get value. */
				final List<String> path = getPath(field);

				//If the value doesn't exist, use the default value.
				if (config.getRaw(path) == null) {
					field.set(object, field.get(defaultConfig));
					continue;
				}

				Object value = config.get(path);

				final Converter<Object, Object> converter =
						(Converter<Object, Object>) getConverter.invoke(null, field);

				if (converter != null) {
					value = converter.convertToField(value);
				}

				/* Write value to field, and convert if necessary. */
				final Class<?> fieldType = field.getType();

				try {
					if (value instanceof UnmodifiableConfig &&
							!(fieldType.isAssignableFrom(value.getClass()))) {
						final UnmodifiableConfig cfg = (UnmodifiableConfig) value;

						// Gets or creates the field and convert it (if null OR not preserved)
						Object fieldValue = field.get(object);

						if (fieldValue == null) {
							fieldValue = Utils.constructUnsafely(fieldType);
							field.set(object, fieldValue);
							moveToObjectConfig(
									cfg, fieldValue, fieldType, field.get(defaultConfig)
							);
						} else if (!(boolean) mustPreserve.invoke(null, field, clazz)) {
							moveToObjectConfig(
									cfg, fieldValue, fieldType, field.get(defaultConfig)
							);
						}
					} else if (value instanceof Collection &&
							Collection.class.isAssignableFrom(fieldType)) {
						final Collection<?> source = (Collection<?>) value;
						final Class<?> sourceBottomType =
								(Class<?>) bottomElementType.invoke(objectConverter, source);

						final ParameterizedType genericType =
								(ParameterizedType) field.getGenericType();
						final List<Class<?>> destinationTypes =
								(List<Class<?>>) elementTypes.invoke(objectConverter, genericType);
						final Class<?> destinationBottomType =
								destinationTypes.get(destinationTypes.size() - 1);

						if (sourceBottomType == null ||
								destinationBottomType == null ||
								destinationBottomType.isAssignableFrom(sourceBottomType)) {
							value = checkField(field, value, defaultConfig);
							field.set(object, value);
						} else {
							//AutoConfig doesn't support collections of objects.
							throw new UnsupportedOperationException(
									"Collections of objects are not supported!"
							);
						}
					} else {
						if (value == null && (boolean) mustPreserve.invoke(null, field, clazz)) {
							checkField(field, field.get(object), defaultConfig);
						} else {
							checkField(field, value, defaultConfig);

							if (fieldType.isEnum()) {
								final Class<? extends Enum> enumType =
										(Class<? extends Enum>) fieldType;
								final SpecEnum specEnum = field.getAnnotation(SpecEnum.class);
								final EnumGetMethod method = specEnum == null ?
										EnumGetMethod.NAME_IGNORECASE : specEnum.method();
								field.set(object, method.get(value, enumType));
							} else {
								//Blast the lack of proper type converters -_-
								if (value != null && value.getClass() == Double.class &&
										(fieldType == float.class || fieldType == Float.class)) {
									value = (float) (double) value;
								}

								field.set(object, value);
							}
						}
					}
				} catch (RuntimeException ex) {
					throw new RuntimeException("Failed to deserialize: " + field, ex);
				}
			}

			clazz = clazz.getSuperclass();
		}
	}

	@SuppressWarnings("unchecked")
	private List<String> getPath(Field field)
			throws IllegalAccessException, InvocationTargetException {
		//Convert lowerCamel to lower_underscore for TOML.
		return ((List<String>) getPath.invoke(null, field)).stream().
				map(element -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, element)).
				collect(Collectors.toList());
	}

	private Object checkField(Field field, Object value, Object defaultConfig)
			throws IllegalAccessException, InvocationTargetException {
		try {
			checkField.invoke(null, field, value);
			return value;
		} catch (InvalidValueException ex) {
			//Reset to default value.
			value = field.get(defaultConfig);
			//Ensure default value is valid.
			checkField.invoke(null, field, value);
			return value;
		}
	}
}
