package com.therandomlabs.randomlib.config.adapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.therandomlabs.randomlib.TRLUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

public final class ResourceLocationTypeAdapter implements TypeAdapter {
	private final Class<?> registryEntryClass;
	private final IForgeRegistry<?> registry;
	private final boolean isArray;

	public ResourceLocationTypeAdapter(
			Class<IForgeRegistryEntry<?>> registryEntryClass, boolean isArray
	) {
		this.registryEntryClass = registryEntryClass;
		registry = RegistryManager.ACTIVE.getRegistry(registryEntryClass);
		this.isArray = isArray;
	}

	@Override
	public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
		if(!isArray) {
			final String location = config.get(name);

			if(location.isEmpty()) {
				return null;
			}

			final Object object =
					registry.getValue(new ResourceLocation(location.replaceAll("\\s", "")));
			return object == null ? defaultValue : object;
		}

		final List<String> list = config.get(name);
		final List<Object> values = new ArrayList<>(list.size());

		for(String element : list) {
			final Object object =
					registry.getValue(new ResourceLocation(element.replaceAll("\\s", "")));

			if(object != null) {
				values.add(object);
			}
		}

		return values.toArray((Object[]) Array.newInstance(registryEntryClass, 0));
	}

	@Override
	public void setValue(CommentedFileConfig config, String name, Object value) {
		if(isArray) {
			config.set(
					name,
					Arrays.stream((Object[]) value).
							map(this::asString).
							collect(Collectors.toList())
			);
		} else {
			config.set(name, asString(value));
		}
	}

	@Override
	public String asString(Object value) {
		return value == null ? "" : ((IForgeRegistryEntry) value).getRegistryName().toString();
	}

	@Override
	public boolean isArray() {
		return isArray;
	}

	@Override
	public boolean shouldLoad() {
		return TRLUtils.hasReachedStage(ModLoadingStage.COMMON_SETUP);
	}
}
