package com.therandomlabs.randompatches;

import com.therandomlabs.utils.config.ConfigManager;
import com.therandomlabs.utils.forge.ForgeUtils;
import com.therandomlabs.utils.forge.config.ConfigReloadCommand;
import com.therandomlabs.utils.forge.config.ForgeConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RandomPatches.MOD_ID)
public final class RandomPatches {
	public static final String MOD_ID = "randompatches";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static final String DEFAULT_WINDOW_TITLE = "Minecraft " + ForgeUtils.MC_VERSION;

	public static final CommonProxy PROXY =
			DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

	public RandomPatches() {
		if(!ForgeUtils.IS_CLIENT) {
			ForgeConfig.initialize();
			ConfigManager.register(RPConfig.class);
		}

		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		PROXY.init();
	}

	private void serverStarting(FMLServerStartingEvent event) {
		if(RPConfig.Misc.rpreload) {
			ConfigReloadCommand.server(
					event.getCommandDispatcher(), "rpreload", "rpreloadclient", RPConfig.class,
					"RandomPatches configuration reloaded!",
					(phase, source) -> RPConfig.Window.setWindowSettings =
							phase == ConfigReloadCommand.ReloadPhase.POST
			);
		}
	}
}
