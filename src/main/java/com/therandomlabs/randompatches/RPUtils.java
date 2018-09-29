package com.therandomlabs.randompatches;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import com.therandomlabs.randompatches.core.RPCoreContainer;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.VersionRange;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public final class RPUtils {
	private RPUtils() {}

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

		//Give up
		if(!uri.startsWith("file:")) {
			return null;
		}

		//Get rid of an extra slash at the end while we're at it
		uri = uri.substring(6, uri.indexOf(packageName));

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
				RandomPatches.LOGGER.error("Failed to load mcmod.info", ex);

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
}
