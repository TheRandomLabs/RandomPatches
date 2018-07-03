package com.therandomlabs.randompatches.asm;

import java.util.Map;
import com.therandomlabs.randompatches.RandomPatches;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name(RandomPatches.NAME)
@IFMLLoadingPlugin.TransformerExclusions({"com.therandomlabs.randompatches"})
public class RPCore implements IFMLLoadingPlugin {
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
	public void injectData(Map<String, Object> data) {}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
