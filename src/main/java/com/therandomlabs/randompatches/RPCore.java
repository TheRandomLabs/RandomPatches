package com.therandomlabs.randompatches;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name(RandomPatches.NAME)
@IFMLLoadingPlugin.MCVersion(RandomPatches.MINECRAFT_VERSIONS)
@IFMLLoadingPlugin.TransformerExclusions({"com.therandomlabs.randompatches"})
public class RPCore implements IFMLLoadingPlugin {
	@Override
	public String[] getASMTransformerClass() {
		return new String[] {"com.therandomlabs.randompatches.RPTransformer"};
	}

	@Override
	public String getModContainerClass() {
		return "com.therandomlabs.randompatches.RPCoreContainer";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
