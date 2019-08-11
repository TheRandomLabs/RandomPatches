package com.therandomlabs.randomlib;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import cpw.mods.modlauncher.ArgumentHandler;
import cpw.mods.modlauncher.Launcher;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public final class TRLUtils {
	public static final boolean IS_DEOBFUSCATED;
	public static final boolean IS_CLIENT = FMLEnvironment.dist.isClient();

	public static final String MC_VERSION = MCPVersion.getMCVersion();
	public static final int MC_VERSION_NUMBER = Integer.parseInt(MC_VERSION.split("\\.")[1]);
	public static final ArtifactVersion MC_ARTIFACT_VERSION =
			new DefaultArtifactVersion(MC_VERSION);

	public static final int FORGE_BUILD =
			Integer.parseInt(ForgeVersion.getVersion().split("\\.")[2]);

	private static final Logger LOGGER = LogManager.getLogger("randomlib");

	private static Field modifiers;

	static {
		boolean isDeobfuscated = false;

		try {
			final Object argumentHandler =
					findField(Launcher.class, "argumentHandler").get(Launcher.INSTANCE);
			final Object launchTarget =
					findMethod(ArgumentHandler.class, "getLaunchTarget").invoke(argumentHandler);
			isDeobfuscated = "fmluserdevclient".equals(launchTarget) ||
					"fmluserdevserver".equals(launchTarget);
		} catch(IllegalAccessException | InvocationTargetException ex) {
			LOGGER.error("Failed to determine launch target", ex);
		}

		IS_DEOBFUSCATED = isDeobfuscated;
	}

	private TRLUtils() {}

	public static Object toPrimitiveArray(Object[] boxedArray) {
		if(boxedArray instanceof Boolean[]) {
			return ArrayUtils.toPrimitive((Boolean[]) boxedArray);
		}

		if(boxedArray instanceof Byte[]) {
			return ArrayUtils.toPrimitive((Byte[]) boxedArray);
		}

		if(boxedArray instanceof Character[]) {
			return ArrayUtils.toPrimitive((Character[]) boxedArray);
		}

		if(boxedArray instanceof Double[]) {
			return ArrayUtils.toPrimitive((Double[]) boxedArray);
		}

		if(boxedArray instanceof Float[]) {
			return ArrayUtils.toPrimitive((Float[]) boxedArray);
		}

		if(boxedArray instanceof Integer[]) {
			return ArrayUtils.toPrimitive((Integer[]) boxedArray);
		}

		if(boxedArray instanceof Long[]) {
			return ArrayUtils.toPrimitive((Long[]) boxedArray);
		}

		if(boxedArray instanceof Short[]) {
			return ArrayUtils.toPrimitive((Short[]) boxedArray);
		}

		return boxedArray;
	}

	public static Object[] toBoxedArray(Object primitiveArray) {
		if(primitiveArray instanceof Object[]) {
			return (Object[]) primitiveArray;
		}

		if(primitiveArray instanceof boolean[]) {
			return ArrayUtils.toObject((byte[]) primitiveArray);
		}

		if(primitiveArray instanceof byte[]) {
			return ArrayUtils.toObject((byte[]) primitiveArray);
		}

		if(primitiveArray instanceof char[]) {
			return ArrayUtils.toObject((char[]) primitiveArray);
		}

		if(primitiveArray instanceof double[]) {
			return ArrayUtils.toObject((double[]) primitiveArray);
		}

		if(primitiveArray instanceof float[]) {
			return ArrayUtils.toObject((float[]) primitiveArray);
		}

		if(primitiveArray instanceof int[]) {
			return ArrayUtils.toObject((int[]) primitiveArray);
		}

		if(primitiveArray instanceof long[]) {
			return ArrayUtils.toObject((long[]) primitiveArray);
		}

		if(primitiveArray instanceof short[]) {
			return ArrayUtils.toObject((long[]) primitiveArray);
		}

		return (Object[]) primitiveArray;
	}

	public static Path getPath(String path) {
		try {
			return Paths.get(path).normalize();
		} catch(InvalidPathException ignored) {}

		return null;
	}

	public static String toStringWithUnixPathSeparators(Path path) {
		return path.toString().replace('\\', '/');
	}

	public static Field findField(Class<?> clazz, String... names) {
		for(Field field : clazz.getDeclaredFields()) {
			for(String name : names) {
				if(name.equals(field.getName())) {
					field.setAccessible(true);
					return field;
				}
			}
		}

		return null;
	}

	public static Method findMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		return findMethod(clazz, name, name, parameterTypes);
	}

	public static Method findMethod(Class<?> clazz, String name, String obfName,
			Class<?>... parameterTypes) {
		for(Method method : clazz.getDeclaredMethods()) {
			final String methodName = method.getName();

			if((name.equals(methodName) || obfName.equals(methodName)) &&
					Arrays.equals(method.getParameterTypes(), parameterTypes)) {
				method.setAccessible(true);
				return method;
			}
		}

		return null;
	}

	public static Field removeFinalModifier(Field field) {
		try {
			if(modifiers == null) {
				modifiers = Field.class.getDeclaredField("modifiers");
				modifiers.setAccessible(true);
			}

			modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		} catch(Exception ex) {
			crashReport("Failed to make " + field.getName() + " non-final", ex);
		}

		return field;
	}

	public static Class<?> getClass(String name) {
		try {
			return Class.forName(name);
		} catch(ClassNotFoundException ignored) {}

		return null;
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public static ModLoadingStage getModLoadingStage() {
		return ModList.get().getModContainerById("forge").get().getCurrentState();
	}

	public static boolean hasReachedStage(ModLoadingStage stage) {
		return stage.ordinal() <= getModLoadingStage().ordinal();
	}

	public static void crashReport(String message, Throwable throwable) {
		throw new ReportedException(new CrashReport(message, throwable));
	}
}
