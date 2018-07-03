package com.therandomlabs.randompatches.asm;

import java.util.HashMap;
import java.util.Map;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.asm.transformer.IngameMenuTransformer;
import com.therandomlabs.randompatches.asm.transformer.LoginServerTransformer;
import com.therandomlabs.randompatches.asm.transformer.PlayServerTransformer;
import com.therandomlabs.randompatches.event.RPEventHandler;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class RPTransformer implements IClassTransformer {
	private static final Map<String, Transformer> TRANSFORMERS = new HashMap<>();

	static {
		RPEventHandler.initialize();

		register("net.minecraft.network.NetHandlerPlayServer", PlayServerTransformer.INSTANCE);
		register("net.minecraft.network.NetHandlerLoginServer", LoginServerTransformer.INSTANCE);
		register("net.minecraft.client.gui.GuiIngameMenu", IngameMenuTransformer.INSTANCE);

		if(FMLInjectionData.data()[4].equals("1.12.2")) {
			RandomPatches.LOGGER.debug("Test");
		}
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

		if(!transformer.transform(node)) {
			RandomPatches.LOGGER.error("Failed to patch class: " + transformedName);
		}

		final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		node.accept(writer);
		return writer.toByteArray();
	}

	public static void register(String className, Transformer transformer) {
		TRANSFORMERS.put(className, transformer);
	}
}
