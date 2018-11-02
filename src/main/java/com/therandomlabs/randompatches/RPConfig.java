package com.therandomlabs.randompatches;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

//The most convoluted way to implement a config GUI, but it works
@Config(modid = RandomPatches.MODID, name = RandomPatches.MODID, category = "")
public class RPConfig {
	public static class Client {
		@Config.LangKey("randompatches.config.window")
		@Config.Comment(RPStaticConfig.WINDOW_COMMENT)
		public Window window = new Window();

		@Config.RequiresMcRestart
		@Config.LangKey("randompatches.config.client.fastLanguageSwitch")
		@Config.Comment(RPStaticConfig.Comments.FAST_LANGUAGE_SWITCH)
		public boolean fastLanguageSwitch = RPStaticConfig.Defaults.FAST_LANGUAGE_SWITCH;

		@Config.LangKey("randompatches.config.client.forceTitleScreenOnDisconnect")
		@Config.Comment(RPStaticConfig.Comments.FORCE_TITLE_SCREEN_ON_DISCONNECT)
		public boolean forceTitleScreenOnDisconnect =
				RPStaticConfig.Defaults.FORCE_TITLE_SCREEN_ON_DISCONNECT;

		@Config.RequiresMcRestart
		@Config.LangKey("randompatches.config.client.narratorKeybind")
		@Config.Comment(RPStaticConfig.Comments.NARRATOR_KEYBIND)
		public boolean narratorKeybind = RPStaticConfig.Defaults.NARRATOR_KEYBIND;

		@Config.RequiresMcRestart
		@Config.LangKey("randompatches.config.client.patchMinecraftClass")
		@Config.Comment(RPStaticConfig.Comments.PATCH_MINECRAFT_CLASS)
		public boolean patchMinecraftClass = RPStaticConfig.Defaults.PATCH_MINECRAFT_CLASS;

		@Config.RequiresMcRestart
		@Config.LangKey("randompatches.config.client.patchTitleScreenOnDisconnect")
		@Config.Comment(RPStaticConfig.Comments.PATCH_TITLE_SCREEN_ON_DISCONNECT)
		public boolean patchTitleScreenOnDisconnect =
				RPStaticConfig.Defaults.PATCH_TITLE_SCREEN_ON_DISCONNECT;

		@Config.RequiresMcRestart
		@Config.LangKey("randompatches.config.client.removePotionGlint")
		@Config.Comment(RPStaticConfig.Comments.REMOVE_POTION_GLINT)
		public boolean removePotionGlint = RPStaticConfig.Defaults.REMOVE_POTION_GLINT;

		@Config.RequiresWorldRestart
		@Config.LangKey("randompatches.config.client.rpreloadclient")
		@Config.Comment(RPStaticConfig.Comments.RPRELOADCLIENT)
		public boolean rpreloadclient = RPStaticConfig.Defaults.RPRELOADCLIENT;
	}

	public static class Misc {
		@Config.RequiresMcRestart
		@Config.LangKey("randompatches.config.misc.minecartAIFix")
		@Config.Comment(RPStaticConfig.Comments.MINECART_AI_FIX)
		public boolean minecartAIFix = RPStaticConfig.Defaults.MINECART_AI_FIX;

		@Config.RequiresMcRestart
		@Config.LangKey("randompatches.config.misc.patchNetHandlerPlayServer")
		@Config.Comment(RPStaticConfig.Comments.PATCH_NETHANDLERPLAYSERVER)
		public boolean patchNetHandlerPlayServer =
				RPStaticConfig.Defaults.PATCH_NETHANDLERPLAYSERVER;

		@Config.RequiresWorldRestart
		@Config.LangKey("randompatches.config.misc.rpreload")
		@Config.Comment(RPStaticConfig.Comments.RPRELOAD)
		public boolean rpreload = RPStaticConfig.Defaults.RPRELOAD;
	}

	public static class SpeedLimits {
		@Config.RangeDouble(min = 1.0)
		@Config.LangKey("randompatches.config.speedLimits.maxPlayerSpeed")
		@Config.Comment(RPStaticConfig.Comments.MAX_PLAYER_SPEED)
		public float maxPlayerSpeed = RPStaticConfig.Defaults.MAX_PLAYER_SPEED;

		@Config.RangeDouble(min = 1.0)
		@Config.LangKey("randompatches.config.speedLimits.maxPlayerElytraSpeed")
		@Config.Comment(RPStaticConfig.Comments.MAX_PLAYER_ELYTRA_SPEED)
		public float maxPlayerElytraSpeed = RPStaticConfig.Defaults.MAX_PLAYER_ELYTRA_SPEED;

		@Config.RangeDouble(min = 1.0)
		@Config.LangKey("randompatches.config.speedLimits.maxPlayerVehicleSpeed")
		@Config.Comment(RPStaticConfig.Comments.MAX_PLAYER_VEHICLE_SPEED)
		public double maxPlayerVehicleSpeed = RPStaticConfig.Defaults.MAX_PLAYER_VEHICLE_SPEED;
	}

	public static class Timeouts {
		@Config.RangeInt(min = 1)
		@Config.LangKey("randompatches.config.timeouts.keepAlivePacketInterval")
		@Config.Comment(RPStaticConfig.Comments.KEEP_ALIVE_PACKET_INTERVAL)
		public int keepAlivePacketInterval = RPStaticConfig.Defaults.KEEP_ALIVE_PACKET_INTERVAL;

		@Config.RangeInt(min = 1)
		@Config.LangKey("randompatches.config.timeouts.loginTimeout")
		@Config.Comment(RPStaticConfig.Comments.LOGIN_TIMEOUT)
		public int loginTimeout = RPStaticConfig.Defaults.LOGIN_TIMEOUT;

		@Config.RequiresMcRestart
		@Config.LangKey("randompatches.config.timeouts.patchLoginTimeout")
		@Config.Comment(RPStaticConfig.Comments.PATCH_LOGIN_TIMEOUT)
		public boolean patchLoginTimeout = RPStaticConfig.Defaults.PATCH_LOGIN_TIMEOUT;

		@Config.RangeInt(min = 1)
		@Config.LangKey("randompatches.config.timeouts.readTimeout")
		@Config.Comment(RPStaticConfig.Comments.READ_TIMEOUT)
		public int readTimeout = RPStaticConfig.Defaults.READ_TIMEOUT;
	}

	public static class Window implements NestedCategory {
		@Config.LangKey("randompatches.config.window.icon16")
		@Config.Comment(RPStaticConfig.Comments.ICON_16)
		public String icon16 = RPStaticConfig.Defaults.ICON_16;

		@Config.LangKey("randompatches.config.window.icon32")
		@Config.Comment(RPStaticConfig.Comments.ICON_32)
		public String icon32 = RPStaticConfig.Defaults.ICON_32;

		@Config.LangKey("randompatches.config.window.title")
		@Config.Comment(RPStaticConfig.Comments.TITLE)
		public String title = RPStaticConfig.Defaults.TITLE;
	}

	public interface NestedCategory {}

	@Config.LangKey("randompatches.config.client")
	@Config.Comment(RPStaticConfig.CLIENT_COMMENT)
	public static Client client = new Client();

	@Config.LangKey("randompatches.config.misc")
	@Config.Comment(RPStaticConfig.MISC_COMMENT)
	public static Misc misc = new Misc();

	@Config.LangKey("randompatches.config.speedLimits")
	@Config.Comment(RPStaticConfig.SPEED_LIMITS_COMMENT)
	public static SpeedLimits speedLimits = new SpeedLimits();

	@Config.LangKey("randompatches.config.timeouts")
	@Config.Comment(RPStaticConfig.TIMEOUTS_COMMENT)
	public static Timeouts timeouts = new Timeouts();

	private static final Field ASM_DATA = ReflectionHelper.findField(
			ConfigManager.class, "asm_data"
	);

	private static final Method GET_CONFIGURATION = ReflectionHelper.findMethod(
			ConfigManager.class, "getConfiguration", "getConfiguration", String.class, String.class
	);

	private static final Field CONFIGS = ReflectionHelper.findField(ConfigManager.class, "CONFIGS");

	private static Map<Object, Field[]> propertyCache;

	public static Map<Object, Field[]> getProperties(Class<?> configClass) {
		final Map<Object, Field[]> properties = new HashMap<>();

		try {
			final Map<Object, Field> nestedCategories = new HashMap<>();

			for(Field field : configClass.getDeclaredFields()) {
				final int modifiers = field.getModifiers();

				if(!Modifier.isPublic(modifiers) || Modifier.isFinal(modifiers)) {
					continue;
				}

				final Object object = field.get(null);
				final Field[] propertyArray = object.getClass().getDeclaredFields();
				final List<Field> propertyList = new ArrayList<>();

				for(Field property : propertyArray) {
					if(NestedCategory.class.isAssignableFrom(property.getType())) {
						nestedCategories.put(object, property);
					} else {
						propertyList.add(property);
					}
				}

				properties.put(object, propertyList.toArray(new Field[0]));
			}

			for(Map.Entry<Object, Field> category : nestedCategories.entrySet()) {
				final Object object = category.getValue().get(category.getKey());
				properties.put(object, object.getClass().getDeclaredFields());
			}
		} catch(Exception ex) {
			RPUtils.crashReport("Error while getting config properties", ex);
		}

		return properties;
	}

	public static void reload() {
		if(propertyCache == null) {
			propertyCache = getProperties(RPConfig.class);
		}

		reload(
				propertyCache,
				RandomPatches.MODID,
				RPConfig.class,
				RPStaticConfig.class,
				RPStaticConfig::onReload
		);
	}

	public static void reload(Map<Object, Field[]> properties, String modid, Class<?> configClass,
			Class<?> staticConfigClass, Runnable onReload) {
		if(!ConfigManager.hasConfigForMod(modid)) {
			try {
				injectASMData(modid, configClass);
			} catch(Exception ex) {
				RPUtils.crashReport("Failed to load config", ex);
			}
		}

		ConfigManager.sync(modid, Config.Type.INSTANCE);

		try {
			modifyConfig(modid);
			copyValuesToStatic(properties, staticConfigClass);
			onReload.run();
			copyValuesFromStatic(properties, staticConfigClass);
			ConfigManager.sync(modid, Config.Type.INSTANCE);
			modifyConfig(modid);
		} catch(Exception ex) {
			RPUtils.crashReport("Error while modifying config", ex);
		}
	}

	public static void reloadFromDisk() {
		try {
			final File file = new File(
					Loader.instance().getConfigDir(),
					RandomPatches.MODID + ".cfg"
			);

			((Map) CONFIGS.get(null)).remove(file.getAbsolutePath());

			reload();
		} catch(Exception ex) {
			RPUtils.crashReport("Error while modifying config", ex);
		}
	}

	public static void removeConfigForReloadFromDisk(String modid) {
		try {
			final File file = new File(Loader.instance().getConfigDir(), modid + ".cfg");
			((Map) CONFIGS.get(null)).remove(file.getAbsolutePath());
		} catch(Exception ex) {
			RPUtils.crashReport("Error while modifying config", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private static void injectASMData(String modid, Class<?> configClass)
			throws IllegalAccessException {
		final Map<String, Multimap<Config.Type, ASMDataTable.ASMData>> asmData =
				(Map<String, Multimap<Config.Type, ASMDataTable.ASMData>>) ASM_DATA.get(null);

		final Multimap<Config.Type, ASMDataTable.ASMData> data = asmData.computeIfAbsent(
				modid,
				id -> ArrayListMultimap.create()
		);

		final Map<String, Object> annotationInfo = new HashMap<>();

		annotationInfo.put("modid", modid);
		annotationInfo.put("name", modid);
		annotationInfo.put("category", "");

		data.put(
				Config.Type.INSTANCE,
				new ASMDataTable.ASMData(null, null, configClass.getName(), null, annotationInfo)
		);
	}

	private static void modifyConfig(String modid)
			throws IllegalAccessException, InvocationTargetException {
		final Configuration config = (Configuration) GET_CONFIGURATION.invoke(null, modid, modid);

		final Map<Property, String> comments = new HashMap<>();

		//Remove old elements
		for(String name : config.getCategoryNames()) {
			final ConfigCategory category = config.getCategory(name);

			category.getValues().forEach((key, property) -> {
				final String comment = property.getComment();

				if(comment == null || comment.isEmpty()) {
					category.remove(key);
					return;
				}

				//Add default value to comment
				comments.put(property, comment);
				property.setComment(comment + "\nDefault: " + property.getDefault());
			});

			if(category.getValues().isEmpty() || category.getComment() == null) {
				config.removeCategory(category);
			}
		}

		config.save();

		//Remove default values from comments so they don't show up in the configuration GUI
		for(String name : config.getCategoryNames()) {
			config.getCategory(name).getValues().forEach(
					(key, property) -> property.setComment(comments.get(property))
			);
		}
	}

	private static void copyValuesToStatic(Map<Object, Field[]> properties,
			Class<?> staticConfigClass) throws IllegalAccessException, NoSuchFieldException {
		for(Map.Entry<Object, Field[]> entry : properties.entrySet()) {
			final Object object = entry.getKey();
			final Field[] propertyArray = entry.getValue();

			for(Field property : propertyArray) {
				final Object value = property.get(object);
				staticConfigClass.getDeclaredField(property.getName()).set(null, value);
			}
		}
	}

	private static void copyValuesFromStatic(Map<Object, Field[]> properties,
			Class<?> staticConfigClass) throws IllegalAccessException, NoSuchFieldException {
		for(Map.Entry<Object, Field[]> entry : properties.entrySet()) {
			final Object object = entry.getKey();
			final Field[] propertyArray = entry.getValue();

			for(Field property : propertyArray) {
				final Object value =
						staticConfigClass.getDeclaredField(property.getName()).get(null);
				property.set(object, value);
			}
		}
	}
}
