package com.therandomlabs.randompatches;

import com.google.common.eventbus.Subscribe;
import com.therandomlabs.randomlib.config.ConfigManager;
import com.therandomlabs.randompatches.client.TileEntityEndPortalRenderer;
import com.therandomlabs.randompatches.common.CommandRPReload;
import com.therandomlabs.randompatches.config.RPConfig;
import com.therandomlabs.randompatches.patch.EntityBoatPatch;
import com.therandomlabs.randompatches.patch.EntityPatch;
import com.therandomlabs.randompatches.patch.ItemBucketPatch;
import com.therandomlabs.randompatches.patch.MinecartPatch;
import com.therandomlabs.randompatches.patch.NBTTagCompoundPatch;
import com.therandomlabs.randompatches.patch.NetHandlerLoginServerPatch;
import com.therandomlabs.randompatches.patch.NetHandlerPlayServerPatch;
import com.therandomlabs.randompatches.patch.ServerRecipeBookHelperPatch;
import com.therandomlabs.randompatches.patch.WorldServerPatch;
import com.therandomlabs.randompatches.patch.client.GuiIngameMenuPatch;
import com.therandomlabs.randompatches.patch.client.ItemPotionPatch;
import com.therandomlabs.randompatches.patch.client.LanguageListPatch;
import com.therandomlabs.randompatches.patch.client.MinecraftPatch;
import com.therandomlabs.randompatches.patch.endportal.BlockEndPortalPatch;
import com.therandomlabs.randompatches.patch.endportal.BlockModelShapesPatch;
import com.therandomlabs.randompatches.patch.endportal.TileEntityEndPortalPatch;
import com.therandomlabs.randompatches.util.RPUtils;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static com.therandomlabs.randompatches.core.RPTransformer.register;

public final class RandomPatches {
	public static final String MOD_ID = "randompatches";
	public static final String NAME = "RandomPatches";
	public static final String VERSION = "@VERSION@";
	public static final String MINECRAFT_VERSIONS = "[1.8,1.13)";
	public static final String CERTIFICATE_FINGERPRINT = "@FINGERPRINT@";

	public static final int SORTING_INDEX = Integer.MAX_VALUE - 10000;

	public static final boolean IS_DEOBFUSCATED =
			(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

	public static final boolean IS_CLIENT = FMLLaunchHandler.side().isClient();
	public static final String MC_VERSION_STRING = (String) FMLInjectionData.data()[4];
	public static final int MC_VERSION = Integer.parseInt(MC_VERSION_STRING.split("\\.")[1]);

	public static final String DEFAULT_WINDOW_TITLE = "Minecraft " + MC_VERSION_STRING;

	public static final boolean SPONGEFORGE_INSTALLED =
			RPUtils.detect("org.spongepowered.mod.SpongeMod");

	public static final boolean ITLT_INSTALLED =
			RPUtils.detect("dk.zlepper.itlt.about.mod");

	public static final boolean REBIND_NARRATOR_INSTALLED =
			RPUtils.detect("quaternary.rebindnarrator.RebindNarrator");

	public static final boolean VANILLAFIX_INSTALLED =
			RPUtils.detect("org.dimdev.vanillafix.VanillaFix");

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		if(IS_CLIENT && RPConfig.Client.rpreloadclient) {
			ClientCommandHandler.instance.registerCommand(new CommandRPReload(Side.CLIENT));
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		ConfigManager.registerEventHandler();

		if(RPConfig.Client.isNarratorKeybindEnabled()) {
			MinecraftPatch.ToggleNarratorKeybind.register();
		}
	}

	@Subscribe
	public void serverStarting(FMLServerStartingEvent event) {
		if(RPConfig.Misc.rpreload) {
			event.registerServerCommand(new CommandRPReload(Side.SERVER));
		}
	}

	public static void containerInit() {
		if(!RPUtils.hasFingerprint(RandomPatches.class, CERTIFICATE_FINGERPRINT)) {
			if(IS_DEOBFUSCATED) {
				LOGGER.debug("Invalid fingerprint detected!");
			} else {
				LOGGER.error("Invalid fingerprint detected!");
			}
		}

		if(RPConfig.Misc.areEndPortalTweaksEnabled()) {
			final TileEntityEndPortalRenderer renderer = new TileEntityEndPortalRenderer();
			renderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
			TileEntityRendererDispatcher.instance.renderers.put(
					TileEntityEndPortal.class, renderer
			);
		}

		RPConfig.Client.Window.setWindowSettings();
	}

	public static void registerPatches() {
		if(RPConfig.Timeouts.patchLoginTimeout) {
			register(
					"net.minecraft.network.NetHandlerLoginServer",
					new NetHandlerLoginServerPatch()
			);
		}

		if(RPConfig.Client.patchTitleScreenOnDisconnect) {
			register("net.minecraft.client.gui.GuiIngameMenu", new GuiIngameMenuPatch());
		}

		if(RPConfig.Misc.patchNetHandlerPlayServer && RandomPatches.MC_VERSION > 8) {
			register("net.minecraft.network.NetHandlerPlayServer", new NetHandlerPlayServerPatch());
		}

		if(RPConfig.Client.fastLanguageSwitch && RandomPatches.IS_CLIENT) {
			register("net.minecraft.client.gui.GuiLanguage$List", new LanguageListPatch());
		}

		if(RPConfig.Client.patchMinecraftClass && RandomPatches.IS_CLIENT) {
			register("net.minecraft.client.Minecraft", new MinecraftPatch());
		}

		if(RPConfig.Misc.minecartAIFix) {
			register("net.minecraft.entity.item.EntityMinecart", new MinecartPatch());
		}

		if(RPConfig.Client.removePotionGlint && RandomPatches.IS_CLIENT) {
			register("net.minecraft.item.ItemPotion", new ItemPotionPatch());
		}

		if(RPConfig.Misc.areEndPortalTweaksEnabled()) {
			register("net.minecraft.block.BlockEndPortal", new BlockEndPortalPatch());
			register(
					"net.minecraft.client.renderer.BlockModelShapes",
					new BlockModelShapesPatch()
			);
			register(
					"net.minecraft.tileentity.TileEntityEndPortal",
					new TileEntityEndPortalPatch()
			);
		}

		if(RPConfig.Misc.isRecipeBookNBTFixEnabled()) {
			register(
					"net.minecraft.util.ServerRecipeBookHelper",
					new ServerRecipeBookHelperPatch()
			);
		}

		if(RPConfig.Boats.patchEntityBoat && RandomPatches.MC_VERSION > 8) {
			register("net.minecraft.entity.item.EntityBoat", new EntityBoatPatch());
		}

		if(RPConfig.Misc.mc2025Fix && RandomPatches.MC_VERSION > 9) {
			register("net.minecraft.entity.Entity", new EntityPatch());
		}

		if(RPConfig.Misc.replaceTeleporter && RandomPatches.MC_VERSION == 12) {
			register("net.minecraft.world.WorldServer", new WorldServerPatch());
		}

		if(RPConfig.Misc.skullStackingFix) {
			register("net.minecraft.nbt.NBTTagCompound", new NBTTagCompoundPatch());
		}

		if(RPConfig.Misc.portalBucketReplacementFix) {
			register("net.minecraft.item.ItemBucket", new ItemBucketPatch());
		}
	}
}
