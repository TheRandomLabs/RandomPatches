package com.therandomlabs.randompatches.core;

import java.util.HashMap;
import java.util.Map;
import com.therandomlabs.randompatches.RPStaticConfig;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.core.transformer.BlockModelShapesTransformer;
import com.therandomlabs.randompatches.core.transformer.IngameMenuTransformer;
import com.therandomlabs.randompatches.core.transformer.ItemPotionTransformer;
import com.therandomlabs.randompatches.core.transformer.LanguageListTransformer;
import com.therandomlabs.randompatches.core.transformer.LoginServerTransformer;
import com.therandomlabs.randompatches.core.transformer.MinecartTransformer;
import com.therandomlabs.randompatches.core.transformer.MinecraftTransformer;
import com.therandomlabs.randompatches.core.transformer.PlayServerTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class RPTransformer implements IClassTransformer {
	private static final Map<String, Transformer> TRANSFORMERS = new HashMap<>();

	static {
		RPStaticConfig.reload();
		register();
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		final Transformer transformer = TRANSFORMERS.get(transformedName);

		if(transformer == null) {
			return basicClass;
		}

		RandomPatches.LOGGER.debug("Patching class: " + transformedName);

		final ClassReader reader = new ClassReader(basicClass);
		final ClassNode node = new ClassNode();
		reader.accept(node, 0);

		try {
			transformer.transform(node);

			final int flags;

			if(RandomPatches.SPONGEFORGE_INSTALLED &&
					transformer.getClass() == PlayServerTransformer.class) {
				flags = ClassWriter.COMPUTE_MAXS;
			} else {
				flags = ClassWriter.COMPUTE_FRAMES;
			}

			final ClassWriter writer = new RPClassWriter(flags);
			node.accept(writer);
			return writer.toByteArray();
		} catch(Exception ex) {
			RandomPatches.LOGGER.error("Failed to transform class: " + transformedName, ex);
		}

		return basicClass;
	}

	public static void register(String className, Transformer transformer) {
		TRANSFORMERS.put(className, transformer);
	}

	private static void register() {
		if(RPStaticConfig.patchLoginTimeout) {
			register("net.minecraft.network.NetHandlerLoginServer", new LoginServerTransformer());
		}

		if(RPStaticConfig.patchTitleScreenOnDisconnect) {
			register("net.minecraft.client.gui.GuiIngameMenu", new IngameMenuTransformer());
		}

		if(RPStaticConfig.patchNetHandlerPlayServer && !RandomPatches.IS_ONE_EIGHT) {
			register("net.minecraft.network.NetHandlerPlayServer", new PlayServerTransformer());
		}

		if(RPStaticConfig.fastLanguageSwitch && RandomPatches.IS_CLIENT) {
			register("net.minecraft.client.gui.GuiLanguage$List", new LanguageListTransformer());
		}

		if(RPStaticConfig.patchMinecraftClass && RandomPatches.IS_CLIENT) {
			register("net.minecraft.client.Minecraft", new MinecraftTransformer());
		}

		if(RPStaticConfig.minecartAIFix) {
			register("net.minecraft.entity.item.EntityMinecart", new MinecartTransformer());
		}

		if(RPStaticConfig.removePotionGlint && RandomPatches.IS_CLIENT) {
			register("net.minecraft.item.ItemPotion", new ItemPotionTransformer());
		}

		if(RandomPatches.VERTICAL_END_PORTALS_INSTALLED && RandomPatches.IS_CLIENT) {
			register(
					"net.minecraft.client.renderer.BlockModelShapes",
					new BlockModelShapesTransformer()
			);
		}
	}
}
