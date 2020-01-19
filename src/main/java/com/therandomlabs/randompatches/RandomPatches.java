package com.therandomlabs.randompatches;

import static com.therandomlabs.randompatches.core.RPTransformer.register;

import com.google.common.eventbus.Subscribe;
import com.therandomlabs.randomlib.TRLUtils;
import com.therandomlabs.randomlib.config.CommandConfigReload;
import com.therandomlabs.randomlib.config.ConfigManager;
import com.therandomlabs.randompatches.client.RPTileEntityEndPortalRenderer;
import com.therandomlabs.randompatches.config.RPConfig;
import com.therandomlabs.randompatches.hook.client.MinecraftHook;
import com.therandomlabs.randompatches.hook.client.dismount.EntityPlayerSPHook;
import com.therandomlabs.randompatches.patch.BlockObserverPatch;
import com.therandomlabs.randompatches.patch.EntityBoatPatch;
import com.therandomlabs.randompatches.patch.EntityMinecartPatch;
import com.therandomlabs.randompatches.patch.EntityPatch;
import com.therandomlabs.randompatches.patch.ItemBucketPatch;
import com.therandomlabs.randompatches.patch.NBTTagCompoundPatch;
import com.therandomlabs.randompatches.patch.NetHandlerLoginServerPatch;
import com.therandomlabs.randompatches.patch.NetHandlerPlayServerPatch;
import com.therandomlabs.randompatches.patch.PlayerInteractionManagerPatch;
import com.therandomlabs.randompatches.patch.ServerRecipeBookHelperPatch;
import com.therandomlabs.randompatches.patch.ServerWorldEventHandlerPatch;
import com.therandomlabs.randompatches.patch.TileEntityPistonPatch;
import com.therandomlabs.randompatches.patch.WorldServerPatch;
import com.therandomlabs.randompatches.patch.client.EntityRendererPatch;
import com.therandomlabs.randompatches.patch.client.GuiIngameMenuPatch;
import com.therandomlabs.randompatches.patch.client.GuiLanguageListPatch;
import com.therandomlabs.randompatches.patch.client.ItemPotionPatch;
import com.therandomlabs.randompatches.patch.client.MinecraftPatch;
import com.therandomlabs.randompatches.patch.client.OptionsPatch;
import com.therandomlabs.randompatches.patch.client.RenderPlayerPatch;
import com.therandomlabs.randompatches.patch.client.dismount.EntityPlayerSPPatch;
import com.therandomlabs.randompatches.patch.client.dismount.KeyBindingPatch;
import com.therandomlabs.randompatches.patch.client.dismount.NetHandlerPlayClientPatch;
import com.therandomlabs.randompatches.patch.endportal.BlockEndPortalPatch;
import com.therandomlabs.randompatches.patch.endportal.BlockModelShapesPatch;
import com.therandomlabs.randompatches.patch.endportal.TileEntityEndPortalPatch;
import com.therandomlabs.randompatches.patch.packetsize.NettyCompressionDecoderPatch;
import com.therandomlabs.randompatches.patch.packetsize.PacketBufferPatch;
import com.therandomlabs.randompatches.util.RPUtils;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RandomPatches {
	public static final String MOD_ID = "randompatches";
	public static final String NAME = "RandomPatches";
	public static final String VERSION = "@VERSION@";
	public static final String MINECRAFT_VERSIONS = "[1.8,1.13)";
	public static final String CERTIFICATE_FINGERPRINT = "@FINGERPRINT@";

	public static final int SORTING_INDEX = Integer.MAX_VALUE - 10000;

	public static final boolean IS_DEOBFUSCATED =
			(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

	public static final String DEFAULT_WINDOW_TITLE = "Minecraft " + TRLUtils.MC_VERSION;

	public static final boolean BIGGER_PACKETS_PLEASE_INSTALLED =
			RPUtils.detect("net.elnounch.mc.biggerpacketsplz.BiggerBacketsPlzCoreMod");

	public static final boolean EIGENCRAFT_INSTALLED =
			RPUtils.detect("org.gr1m.mc.mup.core.MupCore");

	public static final boolean ICE_AND_FIRE_INSTALLED =
			RPUtils.detect("com.github.alexthe666.iceandfire.asm.IceAndFirePlugin");

	public static final boolean LITTLETILES_INSTALLED =
			RPUtils.detect("com.creativemd.littletiles.LittleTilesCore");

	public static final boolean PARTICLE_FIXES_INSTALLED =
			RPUtils.detect("com.fuzs.particlefixes.ParticleFixes");

	public static final boolean REBIND_INSTALLED =
			RPUtils.detect("austeretony.rebind.common.core.ReBindCorePlugin");

	public static final boolean REPLAY_MOD_INSTALLED =
			RPUtils.detect("com.replaymod.core.ReplayMod");

	public static final boolean REBIND_NARRATOR_INSTALLED =
			RPUtils.detect("quaternary.rebindnarrator.RebindNarrator");

	public static final boolean SPONGEFORGE_INSTALLED =
			RPUtils.detect("org.spongepowered.mod.SpongeMod");

	public static final boolean UNRIDE_KEYBIND_INSTALLED =
			RPUtils.detect("io.github.barteks2x.unridekeybind.core.UnRideKeybindCoremod");

	public static final boolean VANILLAFIX_INSTALLED =
			RPUtils.detect("org.dimdev.vanillafix.VanillaFix");

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		if (TRLUtils.IS_CLIENT && RPConfig.Client.rpreloadclient &&
				TRLUtils.MC_VERSION_NUMBER > 8) {
			ClientCommandHandler.instance.registerCommand(CommandConfigReload.client(
					"rpreloadclient",
					RPConfig.class,
					(phase, command, sender) -> RPConfig.Window.setWindowSettings =
							phase == CommandConfigReload.ReloadPhase.POST
			));
		}

		if (RPConfig.Misc.areEndPortalTweaksEnabled()) {
			final RPTileEntityEndPortalRenderer renderer = new RPTileEntityEndPortalRenderer();
			renderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
			TileEntityRendererDispatcher.instance.renderers.put(
					TileEntityEndPortal.class, renderer
			);
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		ConfigManager.registerEventHandler();

		if (RPConfig.Client.isDismountKeybindEnabled()) {
			EntityPlayerSPHook.DismountKeybind.register();
		}

		if (RPConfig.Client.isNarratorKeybindEnabled()) {
			MinecraftHook.ToggleNarratorKeybind.register();
		}
	}

	@Subscribe
	public void serverStarting(FMLServerStartingEvent event) {
		if (RPConfig.Misc.rpreload && TRLUtils.MC_VERSION_NUMBER > 8) {
			event.registerServerCommand(CommandConfigReload.server(
					"rpreload",
					"rpreloadclient",
					RPConfig.class,
					"RandomPatches configuration reloaded!",
					(phase, command, sender) -> RPConfig.Window.setWindowSettings =
							phase == CommandConfigReload.ReloadPhase.POST
			));
		}
	}

	public static void containerInit() {
		if (!RPUtils.hasFingerprint(RandomPatches.class, CERTIFICATE_FINGERPRINT)) {
			if (IS_DEOBFUSCATED) {
				LOGGER.debug("Invalid fingerprint detected!");
			} else {
				LOGGER.error("Invalid fingerprint detected!");
			}
		}
	}

	public static void registerPatches() {
		if (RPConfig.Boats.patchEntityBoat && TRLUtils.MC_VERSION_NUMBER > 8) {
			register("net.minecraft.entity.item.EntityBoat", new EntityBoatPatch());
		}

		if (RPConfig.Client.isDismountKeybindEnabled()) {
			register("net.minecraft.client.entity.EntityPlayerSP", EntityPlayerSPPatch.INSTANCE);
			register(
					"net.minecraft.client.network.NetHandlerPlayClient",
					new NetHandlerPlayClientPatch()
			);

			if (TRLUtils.MC_VERSION_NUMBER > 8) {
				register("net.minecraft.client.settings.KeyBinding", new KeyBindingPatch());
			}
		}

		if (RPConfig.Client.fastLanguageSwitch && TRLUtils.IS_CLIENT) {
			register("net.minecraft.client.gui.GuiLanguage$List", new GuiLanguageListPatch());
		}

		if (RPConfig.Client.framerateLimitSliderStepSize != 10.0F && TRLUtils.IS_CLIENT) {
			register("net.minecraft.client.settings.GameSettings$Options", new OptionsPatch());
		}

		if (RPConfig.Client.invisiblePlayerModelFix && TRLUtils.IS_CLIENT &&
				TRLUtils.MC_VERSION_NUMBER > 8) {
			register("net.minecraft.client.renderer.entity.RenderPlayer", new RenderPlayerPatch());
		}

		if (RPConfig.Client.patchMinecraftClass && TRLUtils.IS_CLIENT) {
			register("net.minecraft.client.Minecraft", new MinecraftPatch());
		}

		if (RPConfig.Client.patchPotionGlint && TRLUtils.IS_CLIENT) {
			register("net.minecraft.item.ItemPotion", new ItemPotionPatch());
		}

		if (RPConfig.Client.patchTitleScreenOnDisconnect) {
			register("net.minecraft.client.gui.GuiIngameMenu", new GuiIngameMenuPatch());
		}

		if (RPConfig.Client.patchSmoothEyeLevelChanges && TRLUtils.IS_CLIENT) {
			register("net.minecraft.client.renderer.EntityRenderer", new EntityRendererPatch());
		}

		if (RPConfig.Misc.disableObserverSignalOnPlace && TRLUtils.MC_VERSION_NUMBER > 11) {
			register("net.minecraft.block.BlockObserver", new BlockObserverPatch());
		}

		if (RPConfig.Misc.areEndPortalTweaksEnabled()) {
			register("net.minecraft.block.BlockEndPortal", BlockEndPortalPatch.INSTANCE);
			register(
					"net.minecraft.client.renderer.BlockModelShapes",
					new BlockModelShapesPatch()
			);
			register(
					"net.minecraft.tileentity.TileEntityEndPortal",
					new TileEntityEndPortalPatch()
			);
		}

		if (RPConfig.Misc.fixTickNextTickListOutOfSynch) {
			register("net.minecraft.world.WorldServer", new WorldServerPatch());
		}

		if (RPConfig.Misc.mc2025Fix && TRLUtils.MC_VERSION_NUMBER > 9 && !EIGENCRAFT_INSTALLED) {
			register("net.minecraft.entity.Entity", new EntityPatch());
		}

		if (RPConfig.Misc.minecartAIFix && !EIGENCRAFT_INSTALLED) {
			register("net.minecraft.entity.item.EntityMinecart", new EntityMinecartPatch());
		}

		if (RPConfig.Misc.miningGhostBlocksFix && TRLUtils.MC_VERSION_NUMBER > 8) {
			register(
					"net.minecraft.server.management.PlayerInteractionManager",
					new PlayerInteractionManagerPatch()
			);
		}

		if (RPConfig.Misc.particleFixes && TRLUtils.MC_VERSION_NUMBER > 9 &&
				!PARTICLE_FIXES_INSTALLED) {
			register(
					"net.minecraft.world.ServerWorldEventHandler",
					new ServerWorldEventHandlerPatch()
			);
		}

		if (RPConfig.Misc.patchNetHandlerPlayServer && TRLUtils.MC_VERSION_NUMBER > 8) {
			register("net.minecraft.network.NetHandlerPlayServer", new NetHandlerPlayServerPatch());
		}

		if (RPConfig.Misc.patchPacketSizeLimit && !BIGGER_PACKETS_PLEASE_INSTALLED &&
				!LITTLETILES_INSTALLED && !SPONGEFORGE_INSTALLED) {
			register(
					"net.minecraft.network.NettyCompressionDecoder",
					new NettyCompressionDecoderPatch()
			);
			register("net.minecraft.network.PacketBuffer", new PacketBufferPatch());
		}

		if (RPConfig.Misc.pistonGhostBlocksFix && TRLUtils.MC_VERSION_NUMBER > 8) {
			register("net.minecraft.tileentity.TileEntityPiston", new TileEntityPistonPatch());
		}

		if (RPConfig.Misc.portalBucketReplacementFix && TRLUtils.MC_VERSION_NUMBER > 8) {
			register("net.minecraft.item.ItemBucket", new ItemBucketPatch());
		}

		if (RPConfig.Misc.isRecipeBookNBTFixEnabled()) {
			register(
					"net.minecraft.util.ServerRecipeBookHelper",
					new ServerRecipeBookHelperPatch()
			);
		}

		if (RPConfig.Misc.skullStackingFix) {
			register("net.minecraft.nbt.NBTTagCompound", new NBTTagCompoundPatch());
		}

		if (RPConfig.Timeouts.patchLoginTimeout) {
			register(
					"net.minecraft.network.NetHandlerLoginServer",
					new NetHandlerLoginServerPatch()
			);
		}
	}
}
