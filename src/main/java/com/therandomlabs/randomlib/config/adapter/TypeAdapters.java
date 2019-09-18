package com.therandomlabs.randomlib.config.adapter;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.ArrayUtils;

@SuppressWarnings("unchecked")
public final class TypeAdapters {
	private static final Map<Class<?>, TypeAdapter> ADAPTERS = new HashMap<>();

	static {
		final Class[] defaultAdapterClasses = {
				boolean.class,
				Boolean.class,
				char.class,
				Character.class
		};

		for(Class clazz : defaultAdapterClasses) {
			register(clazz, new TypeAdapter() {});
		}

		register(boolean[].class, TypeAdapters.<Boolean>getArrayAdapter(
				list -> ArrayUtils.toPrimitive(list.toArray(new Boolean[0]))
		));

		register(Boolean[].class, TypeAdapters.<Boolean>getArrayAdapter(
				list -> list.toArray(new Boolean[0])
		));

		register(byte.class, Byte.class, getNumberAdapter(Byte::parseByte));

		register(byte[].class, getNumberArrayAdapter(
				Byte.class,
				Number::byteValue,
				stream -> ArrayUtils.toPrimitive(stream.toArray(Byte[]::new))
		));

		register(Byte[].class, getNumberArrayAdapter(
				Byte.class,
				Number::byteValue,
				stream -> stream.toArray(Byte[]::new)
		));

		register(char[].class, TypeAdapters.<Character>getArrayAdapter(
				list -> ArrayUtils.toPrimitive(list.toArray(new Character[0]))
		));

		register(Character[].class, TypeAdapters.<Character>getArrayAdapter(
				list -> list.toArray(new Character[0])
		));

		register(double.class, Double.class, getNumberAdapter(Double::parseDouble));

		register(double[].class, getNumberArrayAdapter(
				Double.class,
				Number::doubleValue,
				stream -> ArrayUtils.toPrimitive(stream.toArray(Double[]::new))
		));

		register(Double[].class, getNumberArrayAdapter(
				Double.class,
				Number::doubleValue,
				stream -> stream.toArray(Double[]::new)
		));

		register(float.class, Float.class, getNumberAdapter(Float::parseFloat));

		register(float[].class, getNumberArrayAdapter(
				Float.class,
				Number::floatValue,
				stream -> ArrayUtils.toPrimitive(stream.toArray(Float[]::new))
		));

		register(Float[].class, getNumberArrayAdapter(
				Float.class,
				Number::floatValue,
				stream -> stream.toArray(Float[]::new)
		));

		register(long.class, Long.class, getNumberAdapter(Long::parseLong));

		register(long[].class, getNumberArrayAdapter(
				Long.class,
				Number::longValue,
				stream -> ArrayUtils.toPrimitive(stream.toArray(Long[]::new))
		));

		register(Long[].class, getNumberArrayAdapter(
				Long.class,
				Number::longValue,
				stream -> stream.toArray(Long[]::new)
		));

		register(int.class, Integer.class, getNumberAdapter(Integer::parseInt));

		register(int[].class, getNumberArrayAdapter(
				Integer.class,
				Number::intValue,
				stream -> ArrayUtils.toPrimitive(stream.toArray(Integer[]::new))
		));

		register(Integer[].class, getNumberArrayAdapter(
				Integer.class,
				Number::intValue,
				stream -> stream.toArray(Integer[]::new)
		));

		register(short.class, Short.class, getNumberAdapter(Short::parseShort));

		register(short[].class, getNumberArrayAdapter(
				Short.class,
				Number::shortValue,
				stream -> ArrayUtils.toPrimitive(stream.toArray(Short[]::new))
		));

		register(Short[].class, getNumberArrayAdapter(
				Short.class,
				Number::shortValue,
				stream -> stream.toArray(Short[]::new)
		));

		register(String.class, new TypeAdapter() {
			@Override
			public String asString(Object value) {
				return value instanceof Enum ? ((Enum) value).name() : (String) value;
			}
		});

		register(String[].class, new TypeAdapter() {
			@Override
			public String asString(Object value) {
				return value instanceof Enum ? ((Enum) value).name() : String.valueOf(value);
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(Path.class, new PathTypeAdapter(false));
		register(Path[].class, new PathTypeAdapter(true));
	}

	public static TypeAdapter get(Class<?> clazz) {
		final TypeAdapter adapter = ADAPTERS.get(clazz);

		if(adapter != null) {
			return adapter;
		}

		if(IForgeRegistryEntry.class.isAssignableFrom(clazz)) {
			register((Class<IForgeRegistryEntry<?>>) clazz);
		} else if(clazz.isArray()) {
			final Class<?> componentType = clazz.getComponentType();

			if(IForgeRegistryEntry.class.isAssignableFrom(componentType)) {
				register((Class<IForgeRegistryEntry<?>>) componentType);
			} else {
				return null;
			}
		}

		return ADAPTERS.get(clazz);
	}

	public static void register(Class<?> clazz, TypeAdapter adapter) {
		ADAPTERS.put(clazz, adapter);
	}

	public static void register(Class<?> clazz1, Class<?> clazz2, TypeAdapter adapter) {
		register(clazz1, adapter);
		register(clazz2, adapter);
	}

	public static void register(Class<IForgeRegistryEntry<?>> registryEntryClass) {
		register(registryEntryClass, new ResourceLocationTypeAdapter(registryEntryClass, false));
		register(
				Array.newInstance(registryEntryClass, 0).getClass(),
				new ResourceLocationTypeAdapter(registryEntryClass, true)
		);
	}

	private static <T> TypeAdapter getArrayAdapter(Function<List<T>, Object> toArray) {
		return new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				return toArray.apply(config.get(name));
			}

			@Override
			public boolean isArray() {
				return true;
			}
		};
	}

	private static <N extends Number> TypeAdapter getNumberAdapter(Function<String, N> parser) {
		return new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				try {
					return parser.apply(config.get(name).toString());
				} catch(NumberFormatException ignored) {}

				return null;
			}
		};
	}

	private static <N extends Number> TypeAdapter getNumberArrayAdapter(
			Class<N> numberClass, Function<Number, N> converter,
			Function<Stream<N>, Object> toArray
	) {
		return new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				return toArray.apply((((List<Number>) config.get(name)).stream().map(number -> {
					try {
						return converter.apply(Double.parseDouble(number.toString()));
					} catch(NumberFormatException ignored) {}

					return null;
				}).filter(Objects::nonNull)));
			}

			@Override
			public boolean isArray() {
				return true;
			}
		};
	}
}
