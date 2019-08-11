package com.therandomlabs.randomlib.config.adapter;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
				Byte.class,
				char.class,
				Character.class,
				double.class,
				Double.class,
				Float.class,
				int.class,
				Integer.class,
				long.class,
				Long.class,
				Short.class
		};

		for(Class clazz : defaultAdapterClasses) {
			register(clazz, TypeAdapter.DEFAULT);
		}

		register(boolean[].class, new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				return ArrayUtils.toPrimitive(
						((List<Boolean>) config.get(name)).toArray(new Boolean[0])
				);
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(Boolean[].class, new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				return ((List<Boolean>) config.get(name)).toArray(new Boolean[0]);
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(byte.class, new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				final Object value = config.get(name);
				return value instanceof Byte ? value : (byte) (int) value;
			}
		});

		register(byte[].class, new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				return ArrayUtils.toPrimitive(
						((List<Byte>) config.get(name)).toArray(new Byte[0])
				);
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(Byte[].class, new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				return ((List<Byte>) config.get(name)).toArray(new Byte[0]);
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(char[].class, new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				return ArrayUtils.toPrimitive(
						((List<Character>) config.get(name)).toArray(new Character[0])
				);
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(Character[].class, new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				return ArrayUtils.toPrimitive(
						((List<Character>) config.get(name)).toArray(new Character[0])
				);
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(double[].class, new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				return ArrayUtils.toPrimitive(
						((List<Double>) config.get(name)).toArray(new Double[0])
				);
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(Double[].class, new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				return ((List<Double>) config.get(name)).toArray(new Double[0]);
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(float.class, new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				final Object value = config.get(name);
				return value instanceof Float ? value : (float) (double) value;
			}
		});

		register(float[].class, new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				return ArrayUtils.toPrimitive(
						((List<Float>) config.get(name)).toArray(new Float[0])
				);
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(Float[].class, new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				return ((List<Float>) config.get(name)).toArray(new Float[0]);
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(int[].class, new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				return ArrayUtils.toPrimitive(
						((List<Integer>) config.get(name)).toArray(new Integer[0])
				);
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(Integer[].class, new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				return ((List<Integer>) config.get(name)).toArray(new Integer[0]);
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(long[].class, new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				return ArrayUtils.toPrimitive(
						((List<Long>) config.get(name)).toArray(new Long[0])
				);
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(Long[].class, new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				return ((List<Long>) config.get(name)).toArray(new Long[0]);
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(short.class, new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				final Object value = config.get(name);
				return value instanceof Short ? value : (short) (int) value;
			}
		});

		register(short[].class, new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				return ArrayUtils.toPrimitive(
						((List<Short>) config.get(name)).toArray(new Short[0])
				);
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(Short[].class, new TypeAdapter() {
			@Override
			public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
				return ((List<Short>) config.get(name)).toArray(new Short[0]);
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

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

	public static void register(Class<IForgeRegistryEntry<?>> registryEntryClass) {
		register(registryEntryClass, new ResourceLocationTypeAdapter(registryEntryClass, false));
		register(
				Array.newInstance(registryEntryClass, 0).getClass(),
				new ResourceLocationTypeAdapter(registryEntryClass, true)
		);
	}
}
