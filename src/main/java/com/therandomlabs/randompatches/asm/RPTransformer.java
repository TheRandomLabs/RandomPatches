package com.therandomlabs.randompatches.asm;

import java.util.HashMap;
import java.util.Map;
import com.therandomlabs.randompatches.RPStaticConfig;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.asm.transformer.IngameMenuTransformer;
import com.therandomlabs.randompatches.asm.transformer.LanguageListTransformer;
import com.therandomlabs.randompatches.asm.transformer.LoginServerTransformer;
import com.therandomlabs.randompatches.asm.transformer.PlayServerTransformer;
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
			if(!transformer.transform(node)) {
				RandomPatches.LOGGER.error("Failed to transformer class: " + transformedName);
			}
		} catch(Exception ex) {
			RandomPatches.LOGGER.error("Failed to transformer class: " + transformedName, ex);
		}

		final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		node.accept(writer);
		return writer.toByteArray();
	}

	public static void register(String className, Transformer transformer) {
		TRANSFORMERS.put(className, transformer);
	}

	private static void register() {
		register("net.minecraft.network.NetHandlerPlayServer", new PlayServerTransformer());
		register("net.minecraft.network.NetHandlerLoginServer", new LoginServerTransformer());
		register("net.minecraft.client.gui.GuiIngameMenu", new IngameMenuTransformer());

		if(RPStaticConfig.fastLanguageSwitch) {
			register("net.minecraft.client.gui.GuiLanguage$List", new LanguageListTransformer());
		}
	}
}
