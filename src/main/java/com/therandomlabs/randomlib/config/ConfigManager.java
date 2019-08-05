package com.therandomlabs.randomlib.config;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.therandomlabs.randomlib.TRLUtils;
import net.minecraftforge.forgespi.language.MavenVersionAdapter;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.versioning.VersionRange;

public final class ConfigManager {
	private static final ConfigManager INSTANCE = new ConfigManager();

	private static final Map<Class<?>, ConfigData> CONFIGS = new HashMap<>();
	private static final Map<String, List<ConfigData>> MODID_TO_CONFIGS = new HashMap<>();

	private ConfigManager() {}

	public static void register(Class<?> clazz) {
		final Config config = clazz.getAnnotation(Config.class);

		if(config == null) {
			throw new IllegalArgumentException(clazz.getName() + " is not a configuration class");
		}

		//We have to assume it's valid since if this is being loaded before Minecraft Forge is
		//initialized (i.e. in a coremod), Loader.isModLoaded cannot be called
		final String modid = config.value();

		//Ensure path is valid by initializing it first
		final String pathData = config.path();
		final String pathString =
				"config/" + (pathData.isEmpty() ? modid : config.path()) + ".toml";
		final Path path = Paths.get(pathString).toAbsolutePath();

		try {
			Files.createDirectories(path.getParent());
		} catch(IOException ex) {
			throw new ConfigException("Failed to create configuration directory", ex);
		}

		final List<TRLCategory> categories = new ArrayList<>();
		loadCategories("", modid + ".config.", "", clazz, categories);
		final ConfigData data = new ConfigData(clazz, pathString, path, categories);

		CONFIGS.put(clazz, data);
		MODID_TO_CONFIGS.computeIfAbsent(modid, id -> new ArrayList<>()).add(data);

		reloadFromDisk(clazz);
	}

	public static void reloadFromDisk(Class<?> clazz) {
		final ConfigData data = CONFIGS.get(clazz);
		data.config.load();
		reloadFromConfig(clazz);
	}

	public static void reloadFromConfig(Class<?> clazz) {
		final ConfigData data = CONFIGS.get(clazz);

		for(TRLCategory category : data.categories) {
			for(TRLProperty property : category.properties) {
				if(property.exists(data.config)) {
					try {
						if(property.adapter.shouldLoad()) {
							final Object delayedLoad =
									data.delayedLoad.get(property.fullyQualifiedName);

							if(delayedLoad != null) {
								property.reloadDefault();
								data.config.set(property.fullyQualifiedName, delayedLoad);
								//TODO necessary?
								property.set(data.config, delayedLoad);
							}

							property.deserialize(data.config);
						} else {
							//Mainly for ResourceLocations so that if a modded ResourceLocation
							//is loaded too early, it isn't reset in the config
							data.delayedLoad.put(
									property.fullyQualifiedName,
									data.config.get(property.fullyQualifiedName)
							);
						}
					} catch(Exception ex) {
						TRLUtils.crashReport(
								"Failed to deserialize configuration property " +
										property.fullyQualifiedName,
								ex
						);
					}
				}
			}
		}

		writeToDisk(clazz);
	}

	public static void writeToDisk(Class<?> clazz) {
		final ConfigData data = CONFIGS.get(clazz);

		//TODO remove old properties and comments

		for(TRLCategory category : data.categories) {
			category.onReload(false);

			if(TRLUtils.IS_CLIENT) {
				category.onReload(true);
			}

			for(TRLProperty property : category.properties) {
				try {
					final Object delayedLoad = data.delayedLoad.get(property.fullyQualifiedName);

					if(delayedLoad != null) {
						property.set(data.config, delayedLoad);
					}
				} catch(Exception ex) {
					TRLUtils.crashReport(
							"Failed to serialize configuration property " +
									property.fullyQualifiedName,
							ex
					);
				}
			}
		}

		data.config.save();

		//Reset comments
		for(TRLCategory category : data.categories) {
			for(TRLProperty property : category.properties) {
				property.get(data.config); //Sets comment back to normal
			}
		}
	}

	//TODO data.config.close()

	public static CommentedFileConfig get(Class<?> clazz) {
		return CONFIGS.get(clazz).config;
	}

	public static String getPathString(Class<?> clazz) {
		return CONFIGS.get(clazz).pathString;
	}

	public static Path getPath(Class<?> clazz) {
		return CONFIGS.get(clazz).path;
	}

	private static void loadCategories(
			String fullyQualifiedNamePrefix, String languageKeyPrefix, String parentCategory,
			Class<?> clazz, List<TRLCategory> categories
	) {
		for(Field field : clazz.getDeclaredFields()) {
			final Config.Category categoryData = field.getAnnotation(Config.Category.class);

			if(categoryData == null) {
				continue;
			}

			final String name = field.getName();
			final int modifiers = field.getModifiers();

			if(!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) ||
					!Modifier.isFinal(modifiers)) {
				throw new IllegalArgumentException(name + " is not public static final");
			}

			if(!testVersionRange(field)) {
				continue;
			}

			final Class<?> categoryClass = field.getType();
			final String categoryName = parentCategory + name;

			final TRLCategory category = new TRLCategory(
					fullyQualifiedNamePrefix, languageKeyPrefix, categoryClass, categoryName
			);
			loadCategory(category);
			categories.add(category);

			//Load subcategories
			loadCategories(
					fullyQualifiedNamePrefix, languageKeyPrefix, categoryName + ".", categoryClass,
					categories
			);
		}
	}

	private static void loadCategory(TRLCategory category) {
		for(Field field : category.clazz.getDeclaredFields()) {
			final Config.Property propertyData = field.getAnnotation(Config.Property.class);

			if(propertyData == null) {
				continue;
			}

			final String comment = StringUtils.join(propertyData.value(), "\n");

			if(comment.trim().isEmpty()) {
				throw new IllegalArgumentException("Property comment may not be empty");
			}

			final String name = field.getName();
			final int modifiers = field.getModifiers();

			if(!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) ||
					Modifier.isFinal(modifiers)) {
				throw new IllegalArgumentException(name + " is not public static non-final");
			}

			if(!testVersionRange(field)) {
				continue;
			}

			final Config.Previous previousData = field.getAnnotation(Config.Previous.class);
			final String previous = previousData == null ? null : previousData.value();

			try {
				category.properties.add(new TRLProperty(category, name, field, comment, previous));
			} catch(RuntimeException ex) {
				throw new ConfigException(name, ex);
			}
		}
	}

	private static boolean testVersionRange(Field field) {
		final Config.MCVersion mcVersion = field.getAnnotation(Config.MCVersion.class);

		if(mcVersion != null) {
			final String versionRange = mcVersion.value().trim();

			if(versionRange.isEmpty()) {
				throw new IllegalArgumentException("Version range must not be empty");
			}

			final VersionRange range = MavenVersionAdapter.createFromVersionSpec(versionRange);

			if(!range.containsVersion(TRLUtils.MC_ARTIFACT_VERSION)) {
				return false;
			}
		}

		final Config.MinForgeBuild minForgeBuild = field.getAnnotation(Config.MinForgeBuild.class);

		if(minForgeBuild == null) {
			return true;
		}

		final int forgeBuild = minForgeBuild.value();

		if(forgeBuild < 1) {
			throw new IllegalArgumentException("Invalid Forge build: " + forgeBuild);
		}

		return TRLUtils.FORGE_BUILD >= forgeBuild;
	}
}
