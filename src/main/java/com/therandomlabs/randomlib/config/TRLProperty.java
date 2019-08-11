package com.therandomlabs.randomlib.config;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.therandomlabs.randomlib.TRLUtils;
import com.therandomlabs.randomlib.config.adapter.TypeAdapter;
import com.therandomlabs.randomlib.config.adapter.TypeAdapters;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

//Enums are implemented as a special case here instead of in TRLTypeAdapters
//Numbers also receive some special treatment
final class TRLProperty {
	final TRLCategory category;

	final String name;
	final Field field;

	final String fullyQualifiedName;
	final String languageKey;

	final String previous;

	final TypeAdapter adapter;
	final Class<?> clazz;
	final boolean isArray;
	final boolean isResourceLocation;

	final Class<?> enumClass;
	final Enum[] enumConstants;
	final String[] validValues;
	final String[] validValuesDisplay;

	Object defaultValue;

	final boolean requiresMCRestart;
	final boolean requiresWorldReload;

	final boolean nonNull;

	final double min;
	final double max;

	final String[] blacklist;

	final String comment;

	@SuppressWarnings("unchecked")
	TRLProperty(TRLCategory category, String name, Field field, String comment, String previous) {
		this.category = category;

		this.name = name;
		this.field = field;

		fullyQualifiedName = this.category.getFullyQualifiedName() + "." + name;
		languageKey = this.category.getLanguageKeyPrefix() + name;

		this.previous = previous;

		clazz = field.getType();

		if(Enum.class.isAssignableFrom(clazz)) {
			enumClass = clazz;
			adapter = TypeAdapters.get(String.class);
		} else if(Enum[].class.isAssignableFrom(clazz)) {
			enumClass = clazz.getComponentType();
			adapter = TypeAdapters.get(String[].class);
		} else {
			enumClass = null;
			adapter = TypeAdapters.get(clazz);
		}

		if(adapter == null) {
			throw new ConfigException(
					name,
					new UnsupportedOperationException(
							"Invalid configuration property type: " + clazz.getName()
					)
			);
		}

		if(enumClass == null) {
			enumConstants = null;
			validValues = null;
			validValuesDisplay = null;
		} else {
			final List<String> validValues = new ArrayList<>();
			final List<String> validValuesDisplay = new ArrayList<>();

			enumConstants = ((Class<? extends Enum>) enumClass).getEnumConstants();

			for(Enum element : enumConstants) {
				validValues.add(element.name());
				validValuesDisplay.add(element.toString());
			}

			this.validValues = validValues.toArray(new String[0]);
			this.validValuesDisplay = validValuesDisplay.toArray(new String[0]);
		}

		isArray = adapter.isArray();
		isResourceLocation = IForgeRegistryEntry.class.isAssignableFrom(clazz);

		Object defaultValue = null;

		try {
			defaultValue = field.get(null);
		} catch(IllegalAccessException ex) {
			TRLUtils.crashReport("Failed to load default value of configuration property", ex);
		}

		nonNull = field.getAnnotation(Config.NonNull.class) != null;

		if(defaultValue == null && (!isResourceLocation || nonNull)) {
			throw new IllegalArgumentException(
					"Default value of configuration property may not be null unless it is a " +
							"registry entry without the @Config.NonNull annotation"
			);
		}

		this.defaultValue = defaultValue;

		requiresMCRestart = field.getAnnotation(Config.RequiresMCRestart.class) != null;
		requiresWorldReload = field.getAnnotation(Config.RequiresWorldReload.class) != null;

		if(requiresMCRestart && requiresWorldReload) {
			throw new IllegalArgumentException(
					"A property cannot both require a Minecraft restart and a world reload"
			);
		}

		final double smallestMin;
		final double largestMax;

		if(defaultValue instanceof Byte) {
			smallestMin = Byte.MIN_VALUE;
			largestMax = Byte.MAX_VALUE;
		} else if(defaultValue instanceof Float) {
			smallestMin = -Float.MAX_VALUE;
			largestMax = Float.MAX_VALUE;
		} else if(defaultValue instanceof Integer) {
			smallestMin = Integer.MIN_VALUE;
			largestMax = Integer.MAX_VALUE;
		} else if(defaultValue instanceof Long) {
			smallestMin = Long.MIN_VALUE;
			largestMax = Long.MAX_VALUE;
		} else if(defaultValue instanceof Short) {
			smallestMin = Short.MIN_VALUE;
			largestMax = Short.MAX_VALUE;
		} else {
			smallestMin = -Double.MAX_VALUE;
			largestMax = Double.MAX_VALUE;
		}

		final Config.RangeInt rangeInt = field.getAnnotation(Config.RangeInt.class);
		final Config.RangeDouble rangeDouble = field.getAnnotation(Config.RangeDouble.class);

		double min = -Double.MAX_VALUE;
		double max = Double.MAX_VALUE;

		if(rangeInt != null) {
			if(rangeDouble != null) {
				throw new IllegalArgumentException("Two ranges cannot be defined");
			}

			min = rangeInt.min();
			max = rangeInt.max();

			if(min == Integer.MIN_VALUE && min < smallestMin) {
				min = smallestMin;
			}

			if(max == Integer.MAX_VALUE && max > largestMax) {
				max = largestMax;
			}

			if(min > max) {
				throw new IllegalArgumentException("min cannot be larger than max");
			}
		} else if(rangeDouble != null) {
			min = rangeDouble.min();
			max = rangeDouble.max();

			if(min == -Double.MAX_VALUE) {
				min = smallestMin;
			}

			if(max == Double.MAX_VALUE) {
				max = largestMax;
			}

			if(min > max) {
				throw new IllegalArgumentException("min cannot be larger than max");
			}
		}

		if(min == -Double.MAX_VALUE) {
			min = smallestMin;
		}

		if(max == Double.MAX_VALUE) {
			max = largestMax;
		}

		if(min < smallestMin) {
			throw new IllegalArgumentException(String.format(
					"min is too small: %s < %s", min, smallestMin
			));
		}

		if(max > largestMax) {
			throw new IllegalArgumentException(String.format(
					"max is too large: %s > %s", max, largestMax
			));
		}

		this.min = min;
		this.max = max;

		final Config.Blacklist blacklist = field.getAnnotation(Config.Blacklist.class);
		this.blacklist = blacklist == null ? null : blacklist.value();

		if(isArray) {
			for(Object element : TRLUtils.toBoxedArray(defaultValue)) {
				if(ArrayUtils.contains(this.blacklist, adapter.asString(element))) {
					throw new IllegalArgumentException("Default value is blacklisted");
				}
			}
		} else if(ArrayUtils.contains(this.blacklist, adapter.asString(defaultValue))) {
			throw new IllegalArgumentException("Default value is blacklisted");
		}

		final StringBuilder commentBuilder = new StringBuilder(comment);

		if(enumConstants != null) {
			commentBuilder.append("\n Valid values:");

			for(Enum element : enumConstants) {
				commentBuilder.append("\n ").append(element.name());
			}
		}

		if(defaultValue instanceof Number) {
			if(defaultValue instanceof Double || defaultValue instanceof Float) {
				commentBuilder.append("\n Min: ").
						append(min).
						append("\n Max: ").
						append(max);
			} else {
				commentBuilder.append("\n Min: ").
						append((long) min).
						append("\n Max: ").
						append((long) max);
			}
		}

		commentBuilder.append("\n Default: ");

		if(isArray) {
			commentBuilder.append(
					Arrays.stream(TRLUtils.toBoxedArray(defaultValue)).
							map(adapter::asString).
							collect(Collectors.toList())
			);
		} else {
			commentBuilder.append(adapter.asString(defaultValue));
		}

		this.comment = commentBuilder.toString();
	}

	//For registry entries, the default value might be registry replaced after the property
	//is initialized
	@SuppressWarnings("unchecked")
	void reloadDefault() {
		if(!isArray) {
			if(defaultValue != null) {
				final IForgeRegistry registry = RegistryManager.ACTIVE.getRegistry(
						(Class<IForgeRegistryEntry<?>>) clazz
				);
				defaultValue =
						registry.getValue(((IForgeRegistryEntry) defaultValue).getRegistryName());
			}

			return;
		}

		final Object[] oldDefaults = (Object[]) defaultValue;
		final List<Object> newDefaults = new ArrayList<>(oldDefaults.length);
		final IForgeRegistry registry = RegistryManager.ACTIVE.getRegistry(
				(Class<IForgeRegistryEntry<?>>) clazz.getComponentType()
		);

		for(Object oldDefault : oldDefaults) {
			newDefaults.add(
					registry.getValue(((IForgeRegistryEntry) oldDefault).getRegistryName())
			);
		}

		defaultValue = newDefaults.toArray(Arrays.copyOf(oldDefaults, 0));
	}

	boolean exists(CommentedFileConfig config) {
		return config.contains(fullyQualifiedName) ||
				(previous != null && config.contains(previous));
	}

	Object get(CommentedFileConfig config) {
		if(!config.contains(fullyQualifiedName)) {
			if(previous != null && config.contains(previous)) {
				config.set(fullyQualifiedName, config.get(previous));
			} else {
				set(config, defaultValue);
			}
		}

		//Validate
		set(config, adapter.getValue(config, fullyQualifiedName, defaultValue));
		return adapter.getValue(config, fullyQualifiedName, defaultValue);
	}

	String getAsString(CommentedFileConfig config) {
		return adapter.asString(get(config));
	}

	void set(CommentedFileConfig config, Object value) {
		config.setComment(fullyQualifiedName, comment);
		adapter.setValue(config, fullyQualifiedName, validate(value, isArray));
	}

	Object validate(Object value, boolean isArray) {
		if(value == null && !isResourceLocation) {
			value = defaultValue;
		}

		if(isArray) {
			final boolean primitive = !(value instanceof Object[]);
			final Object[] boxedArray = TRLUtils.toBoxedArray(value);
			final List<Object> filtered = new ArrayList<>();

			for(Object element : boxedArray) {
				if(element != null) {
					final Object validated = validate(element, false);

					if(validated != null) {
						filtered.add(validated);
					}
				}
			}

			final Object[] filteredArray = filtered.toArray(Arrays.copyOf(boxedArray, 0));
			return primitive ? TRLUtils.toPrimitiveArray(filteredArray) : filteredArray;
		} else if(ArrayUtils.contains(blacklist, adapter.asString(value))) {
			return null;
		}

		if(value instanceof Number) {
			double number = ((Number) value).doubleValue();

			if(number < min) {
				number = min;
			} else if(number > max) {
				number = max;
			}

			if(value instanceof Byte) {
				return (byte) number;
			}

			if(value instanceof Double) {
				return number;
			}

			if(value instanceof Float) {
				return (float) number;
			}

			if(value instanceof Integer) {
				return (int) number;
			}

			if(value instanceof Long) {
				return (long) number;
			}

			if(value instanceof Short) {
				return (short) number;
			}
		}

		return value;
	}

	void serialize(CommentedFileConfig config) throws IllegalAccessException {
		Object value = validate(field.get(null), isArray);

		if(value == null) {
			value = defaultValue;
		}

		if(enumConstants == null) {
			set(config, value);
		} else if(!isArray) {
			set(config, ((Enum) value).name());
		} else {
			set(config, Arrays.stream((Enum[]) value).map(Enum::name).toArray(String[]::new));
		}
	}

	void deserialize(CommentedFileConfig config) throws IllegalAccessException {
		if(enumConstants == null) {
			final Object value = get(config);

			if(nonNull && value == null) {
				field.set(null, defaultValue);
			} else {
				final Object validated = validate(value, isArray);
				field.set(null, validated == null ? defaultValue : validated);
			}

			return;
		}

		if(!isArray) {
			//Ignore underscores when matching enums
			//Hopefully this will never cause issues
			final String value = StringUtils.remove(getAsString(config), '_');

			for(Enum element : enumConstants) {
				if(StringUtils.remove(element.name(), '_').equalsIgnoreCase(value)) {
					field.set(null, element);
					return;
				}
			}

			field.set(null, defaultValue);
			return;
		}

		final String[] values = (String[]) get(config);
		final List<Object> enumValues = new ArrayList<>(values.length);

		for(String value : values) {
			value = StringUtils.remove(value, '_');

			for(Enum element : enumConstants) {
				if(StringUtils.remove(element.name(), '_').equalsIgnoreCase(value)) {
					enumValues.add(element);
					break;
				}
			}
		}

		field.set(null, enumValues.toArray((Object[]) Array.newInstance(enumClass, 0)));
	}
}
