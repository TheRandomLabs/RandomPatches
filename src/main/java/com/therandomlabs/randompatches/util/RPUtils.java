package com.therandomlabs.randompatches.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.core.RPCoreContainer;
import net.minecraft.crash.CrashReport;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ReportedException;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.VersionParser;
import net.minecraftforge.fml.common.versioning.VersionRange;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public final class RPUtils {
	private RPUtils() {}

	public static boolean detect(String className) {
		try {
			Class.forName(className, false, Launch.classLoader);
		} catch(ClassNotFoundException ex) {
			return false;
		}

		return true;
	}

	public static File getModFile(Map<String, Object> data, Class<?> clazz, String packageName) {
		File modFile = (File) data.get("coremodLocation");

		if(modFile != null) {
			return modFile;
		}

		//If coremodLocation is null, the coremod was probably loaded by command-line arguments and
		//will most likely be in a directory

		String uri = clazz.getResource(
				"/" + StringUtils.replaceChars(clazz.getName(), '.', '/') + ".class"
		).toString();

		if(uri.startsWith("file:/")) {
			//e.g. file:/C:/examplemod/out/production/RandomPatches_main/com/therandomlabs/
			//randompatches/core/RPCore.class
			//Get rid of everything including and after the "/com"
			uri = uri.substring(6, uri.indexOf(packageName));
		} else if(uri.startsWith("jar:file:/")) {
			//e.g. jar:file:/C:/examplemod/libs/randompatches-version-deobf.jar!/com/therandomlabs/
			//randompatches/core/RPCore.class
			//Get rid of everything including and after the '!'
			uri = uri.substring(10, uri.indexOf(packageName) - 1);
		} else {
			//Give up
			return null;
		}

		try {
			return new File(URLDecoder.decode(uri, StandardCharsets.UTF_8.name()));
		} catch(UnsupportedEncodingException ignored) {}

		return null;
	}

	public static ModMetadata loadMetadata(File source, String modid, String name, String version) {
		InputStream stream = null;
		JarFile jar = null;

		if(source != null) {
			try {
				if(source.isDirectory()) {
					stream = new FileInputStream(new File(source, "mcmod.info"));
				} else {
					jar = new JarFile(source);
					stream = jar.getInputStream(jar.getJarEntry("mcmod.info"));
				}
			} catch(IOException ex) {
				RandomPatches.LOGGER.error("Failed to load mcmod.info: " + source, ex);

				IOUtils.closeQuietly(stream);
				IOUtils.closeQuietly(jar);
			}
		}

		final Map<String, Object> fallback = new HashMap<>();

		fallback.put("name", name);
		fallback.put("version", version);

		final ModMetadata metadata =
				MetadataCollection.from(stream, modid).getMetadataForId(modid, fallback);

		IOUtils.closeQuietly(stream);
		IOUtils.closeQuietly(jar);

		return metadata;
	}

	public static Class<?> getResourcePackClass(ModContainer container) {
		final File source = container.getSource();

		if(source == null) {
			return null;
		}

		final String className;

		if(source.isDirectory()) {
			className = "net.minecraftforge.fml.client.FMLFolderResourcePack";
		} else {
			className = "net.minecraftforge.fml.client.FMLFileResourcePack";
		}

		try {
			return Class.forName(className, true, RPCoreContainer.class.getClassLoader());
		} catch(ClassNotFoundException ignored) {}

		return null;
	}

	public static VersionRange createVersionRange(String versionRange) {
		try {
			return VersionRange.createFromVersionSpec(versionRange);
		} catch(InvalidVersionSpecificationException ex) {
			RandomPatches.LOGGER.error("Failed to create version range", ex);
		}

		return null;
	}

	public static boolean hasFingerprint(Class<?> clazz, String fingerprintToFind) {
		final List<String> fingerprints = CertificateHelper.getFingerprints(
				clazz.getProtectionDomain().getCodeSource().getCertificates()
		);

		for(String fingerprint : fingerprints) {
			if(fingerprintToFind.equals(fingerprint)) {
				return true;
			}
		}

		return false;
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

	public static Method findMethod(Class<?> clazz, String name, String obfName,
			Class<?>... parameterTypes) {
		for(Method method : clazz.getDeclaredMethods()) {
			if((name.equals(method.getName()) || obfName.equals(method.getName())) &&
					Arrays.equals(method.getParameterTypes(), parameterTypes)) {
				method.setAccessible(true);
				return method;
			}
		}

		return null;
	}

	public static void ensureMinimumRPVersion(String minimumVersion) {
		final VersionRange range =
				VersionParser.parseRange("[" + minimumVersion + ",)");
		final ArtifactVersion version =
				new DefaultArtifactVersion(RandomPatches.MOD_ID, RandomPatches.VERSION);

		if(!range.containsVersion(version)) {
			RPUtils.crashReport(
					"RandomPatches " + minimumVersion + " or higher is required",
					new IllegalStateException()
			);
		}
	}

	public static void crashReport(String message, Throwable throwable) {
		throw new ReportedException(new CrashReport(message, throwable));
	}
}
