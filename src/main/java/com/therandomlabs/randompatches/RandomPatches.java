package com.therandomlabs.randompatches;

import com.therandomlabs.randompatches.common.RPReloadCommand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RandomPatches.MOD_ID)
public final class RandomPatches {
	public static final String MOD_ID = "randompatches";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static final boolean IS_CLIENT = FMLEnvironment.dist.isClient();
	public static final boolean IS_DEOBFUSCATED = true;

	public static final boolean SPONGEFORGE_INSTALLED = false;

	public static final String DEFAULT_WINDOW_TITLE = "Minecraft " + MCPVersion.getMCVersion();

	public static final CommonProxy PROXY =
			DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

	public RandomPatches() {
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);

		RPConfig.Timeouts.onReload();
		RPConfig.Window.onReload();

		PROXY.init();
	}

	private void serverStarting(FMLServerStartingEvent event) {
		if(RPConfig.Misc.rpreload) {
			RPReloadCommand.register(event.getCommandDispatcher(), Dist.DEDICATED_SERVER);
		}
	}
}
