package com.therandomlabs.randompatches;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Config(modid = RandomPatches.MODID, name = RandomPatches.MODID, category = "")
@Config.LangKey("randompatches.config.title")
public class RPConfig {
	public static class Misc {
		@Config.LangKey("randompatches.config.misc.forceTitleScreenOnDisconnect")
		@Config.Comment(RPStaticConfig.Comments.FORCE_TITLE_SCREEN_ON_DISCONNECT)
		public boolean forceTitleScreenOnDisconnect =
				RPStaticConfig.Defaults.FORCE_TITLE_SCREEN_ON_DISCONNECT;

		@Config.RequiresWorldRestart
		@Config.LangKey("randompatches.config.commands.rpreload")
		@Config.Comment(RPStaticConfig.Comments.RPRELOAD)
		public boolean rpreload = RPStaticConfig.Defaults.RPRELOAD;
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

		@Config.RangeInt(min = 1)
		@Config.LangKey("randompatches.config.timeouts.readTimeout")
		@Config.Comment(RPStaticConfig.Comments.READ_TIMEOUT)
		public int readTimeout = RPStaticConfig.Defaults.READ_TIMEOUT;
	}

	@Config.LangKey("randompatches.config.misc")
	@Config.Comment(RPStaticConfig.MISC_COMMENT)
	public static Misc misc = new Misc();

	@Config.LangKey("randompatches.config.timeouts")
	@Config.Comment(RPStaticConfig.TIMEOUTS_COMMENT)
	public static Timeouts timeouts = new Timeouts();

	private static final Method GET_CONFIGURATION = ReflectionHelper.findMethod(ConfigManager.class,
			"getConfiguration", "getConfiguration", String.class, String.class);

	public static void reload() {
		ConfigManager.sync(RandomPatches.MODID, Config.Type.INSTANCE);

		try {
			modifyConfig();
			copyValuesToStatic();
		} catch(Exception ex) {
			throw new ReportedException(new CrashReport("Error while modifying config", ex));
		}

		RPStaticConfig.onReload();
	}

	private static void modifyConfig() throws Exception {
		final Configuration config = (Configuration) GET_CONFIGURATION.invoke(null,
				RandomPatches.MODID, RandomPatches.MODID);

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
				property.setComment(comment + "\n" + "Default: " + property.getDefault());
			});

			if(category.getValues().isEmpty() || category.getComment() == null) {
				config.removeCategory(category);
			}
		}

		config.save();

		//Remove default values from comments so they don't show up in the configuration GUI
		for(String name : config.getCategoryNames()) {
			config.getCategory(name).getValues().forEach((key, property) ->
					property.setComment(comments.get(property)));
		}
	}

	private static void copyValuesToStatic() throws Exception {
		for(Field field : RPConfig.class.getDeclaredFields()) {
			final int modifiers = field.getModifiers();

			if(!Modifier.isPublic(modifiers) || Modifier.isFinal(modifiers)) {
				continue;
			}

			final Object object = field.get(null);

			for(Field property : object.getClass().getDeclaredFields()) {
				final Object value = property.get(object);
				RPStaticConfig.class.getDeclaredField(property.getName()).set(null, value);
			}
		}
	}
}
