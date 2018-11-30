package com.therandomlabs.randompatches.core;

import java.util.HashMap;
import java.util.Map;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.config.RPStaticConfig;
import com.therandomlabs.randompatches.core.patch.EntityBoatPatch;
import com.therandomlabs.randompatches.core.patch.EntityPatch;
import com.therandomlabs.randompatches.core.patch.IngameMenuPatch;
import com.therandomlabs.randompatches.core.patch.ItemPotionPatch;
import com.therandomlabs.randompatches.core.patch.LanguageListPatch;
import com.therandomlabs.randompatches.core.patch.MinecartPatch;
import com.therandomlabs.randompatches.core.patch.MinecraftPatch;
import com.therandomlabs.randompatches.core.patch.NetHandlerLoginServerPatch;
import com.therandomlabs.randompatches.core.patch.NetHandlerPlayServerPatch;
import com.therandomlabs.randompatches.core.patch.ServerRecipeBookHelperPatch;
import com.therandomlabs.randompatches.core.patch.WorldServerPatch;
import com.therandomlabs.randompatches.core.patch.endportal.BlockEndPortalPatch;
import com.therandomlabs.randompatches.core.patch.endportal.BlockModelShapesPatch;
import com.therandomlabs.randompatches.core.patch.endportal.TileEntityEndPortalPatch;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class RPTransformer implements IClassTransformer {
	private static final Map<String, Patch> PATCHES = new HashMap<>();

	static {
		RPStaticConfig.reload();
		register();
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		final Patch patch = PATCHES.get(transformedName);

		if(patch == null) {
			return basicClass;
		}

		RandomPatches.LOGGER.debug("Patching class: " + transformedName);

		final ClassReader reader = new ClassReader(basicClass);
		final ClassNode node = new ClassNode();
		reader.accept(node, 0);

		try {
			patch.apply(node);

			final int flags;

			if(RandomPatches.SPONGEFORGE_INSTALLED &&
					patch.getClass() == NetHandlerPlayServerPatch.class) {
				flags = ClassWriter.COMPUTE_MAXS;
			} else {
				flags = ClassWriter.COMPUTE_FRAMES;
			}

			final ClassWriter writer = new RPClassWriter(flags);
			node.accept(writer);
			return writer.toByteArray();
		} catch(Exception ex) {
			RandomPatches.LOGGER.error("Failed to apply class: " + transformedName, ex);
		}

		return basicClass;
	}

	public static void register(String className, Patch patch) {
		PATCHES.put(className, patch);
	}

	private static void register() {
		if(RPStaticConfig.patchLoginTimeout) {
			register(
					"net.minecraft.network.NetHandlerLoginServer",
					new NetHandlerLoginServerPatch()
			);
		}

		if(RPStaticConfig.patchTitleScreenOnDisconnect) {
			register("net.minecraft.client.gui.GuiIngameMenu", new IngameMenuPatch());
		}

		if(RPStaticConfig.patchNetHandlerPlayServer && RandomPatches.MC_VERSION > 8) {
			register("net.minecraft.network.NetHandlerPlayServer", new NetHandlerPlayServerPatch());
		}

		if(RPStaticConfig.fastLanguageSwitch && RandomPatches.IS_CLIENT) {
			register("net.minecraft.client.gui.GuiLanguage$List", new LanguageListPatch());
		}

		if(RPStaticConfig.patchMinecraftClass && RandomPatches.IS_CLIENT) {
			register("net.minecraft.client.Minecraft", new MinecraftPatch());
		}

		if(RPStaticConfig.minecartAIFix) {
			register("net.minecraft.entity.item.EntityMinecart", new MinecartPatch());
		}

		if(RPStaticConfig.removePotionGlint && RandomPatches.IS_CLIENT) {
			register("net.minecraft.item.ItemPotion", new ItemPotionPatch());
		}

		if(RPStaticConfig.isEndPortalTweaksEnabled()) {
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

		if(RPStaticConfig.isRecipeBookNBTFixEnabled()) {
			register(
					"net.minecraft.util.ServerRecipeBookHelper",
					new ServerRecipeBookHelperPatch()
			);
		}

		if(RPStaticConfig.patchEntityBoat && RandomPatches.MC_VERSION > 8) {
			register("net.minecraft.entity.item.EntityBoat", new EntityBoatPatch());
		}

		if(RandomPatches.VERTICAL_END_PORTALS_INSTALLED) {
			register("net.minecraft.world.WorldServer", new WorldServerPatch());
		}

		//TODO find minimum version
		if(RPStaticConfig.mc2025Fix) {
			register("net.minecraft.entity.Entity", new EntityPatch());
		}
	}
}
