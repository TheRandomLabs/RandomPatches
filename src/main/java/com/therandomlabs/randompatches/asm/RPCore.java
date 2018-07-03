package com.therandomlabs.randompatches.asm;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import com.therandomlabs.randompatches.RandomPatches;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.commons.lang3.StringUtils;

@IFMLLoadingPlugin.Name(RandomPatches.NAME)
@IFMLLoadingPlugin.TransformerExclusions({"com.therandomlabs.randompatches"})
public class RPCore implements IFMLLoadingPlugin {
	private static File modFile;

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {
				"com.therandomlabs.randompatches.asm.RPTransformer"
		};
	}

	@Override
	public String getModContainerClass() {
		return "com.therandomlabs.randompatches.asm.RPCoreContainer";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		modFile = (File) data.get("coremodLocation");

		if(modFile != null) {
			return;
		}

		//If coremodLocation is null, RandomPatches was loaded by command-line arguments
		//It will most likely be in a directory

		final Class<?> clazz = getClass();
		String uri = clazz.getResource(
				"/" + StringUtils.replaceChars(clazz.getName(), '.', '/') + ".class"
		).toString();

		//Give up
		if(!uri.startsWith("file:")) {
			return;
		}

		//Get rid of an extra slash at the end while we're at it
		uri = uri.substring(6, uri.indexOf("com/therandomlabs/randompatches"));

		try {
			modFile = new File(URLDecoder.decode(uri, "UTF-8"));
		} catch(UnsupportedEncodingException ignored) {}
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	public static File getModFile() {
		return modFile;
	}
}
