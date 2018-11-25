package com.therandomlabs.randompatches.core;

import java.io.File;
import java.util.Map;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.util.RPUtils;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.SortingIndex(RandomPatches.SORTING_INDEX)
@IFMLLoadingPlugin.Name(RandomPatches.NAME)
@IFMLLoadingPlugin.TransformerExclusions("com.therandomlabs.randompatches")
public class RPCore implements IFMLLoadingPlugin {
	private static File modFile;

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {
				"com.therandomlabs.randompatches.core.RPTransformer"
		};
	}

	@Override
	public String getModContainerClass() {
		return "com.therandomlabs.randompatches.core.RPCoreContainer";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		modFile = RPUtils.getModFile(data, RPCore.class, "com/therandomlabs/randompatches/core");
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	public static File getModFile() {
		return modFile;
	}
}
