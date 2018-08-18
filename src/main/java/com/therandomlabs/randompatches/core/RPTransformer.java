package com.therandomlabs.randompatches.core;

import java.util.HashMap;
import java.util.Map;
import com.therandomlabs.randompatches.RPStaticConfig;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.core.transformer.IngameMenuTransformer;
import com.therandomlabs.randompatches.core.transformer.LanguageListTransformer;
import com.therandomlabs.randompatches.core.transformer.LoginServerTransformer;
import com.therandomlabs.randompatches.core.transformer.MinecartTransformer;
import com.therandomlabs.randompatches.core.transformer.MinecraftTransformer;
import com.therandomlabs.randompatches.core.transformer.PlayServerTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class RPTransformer implements IClassTransformer {
	public static final boolean SPONGEFORGE_INSTALLED;

	private static final Map<String, Transformer> TRANSFORMERS = new HashMap<>();

	static {
		RPStaticConfig.reload();
		register();

		boolean installed = false;

		try {
			Class.forName("org.spongepowered.mod.SpongeMod");
			installed = true;
		} catch(ClassNotFoundException ignored) {}

		SPONGEFORGE_INSTALLED = installed;
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

			int flags = ClassWriter.COMPUTE_FRAMES;

			if(SPONGEFORGE_INSTALLED && transformer.getClass() == PlayServerTransformer.class) {
				flags = ClassWriter.COMPUTE_MAXS;
			}

			final ClassWriter writer = new ClassWriter(flags) {
				@Override
				protected String getCommonSuperClass(String type1, String type2) {
					final ClassLoader classLoader = Launch.classLoader;

					Class<?> c;
					final Class<?> d;

					try {
						c = Class.forName(type1.replace('/', '.'), false, classLoader);
						d = Class.forName(type2.replace('/', '.'), false, classLoader);
					} catch(Exception ex) {
						throw new RuntimeException(ex.toString());
					}

					if(c.isAssignableFrom(d)) {
						return type1;
					}

					if(d.isAssignableFrom(c)) {
						return type2;
					}

					if(c.isInterface() || d.isInterface()) {
						return "java/lang/Object";
					}

					do {
						c = c.getSuperclass();
					} while(!c.isAssignableFrom(d));

					return c.getName().replace('.', '/');
				}
			};
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
		register("net.minecraft.network.NetHandlerLoginServer", new LoginServerTransformer());
		register("net.minecraft.client.gui.GuiIngameMenu", new IngameMenuTransformer());

		if(!RandomPatches.IS_ONE_EIGHT) {
			register("net.minecraft.network.NetHandlerPlayServer", new PlayServerTransformer());
		}

		if(RPStaticConfig.fastLanguageSwitch) {
			register("net.minecraft.client.gui.GuiLanguage$List", new LanguageListTransformer());
		}

		if(RPStaticConfig.narratorKeybind && RandomPatches.IS_ONE_TWELVE &&
				!RandomPatches.REBIND_NARRATOR_INSTALLED) {
			register("net.minecraft.client.Minecraft", new MinecraftTransformer());
		}

		if(RPStaticConfig.minecartAIFix) {
			register("net.minecraft.entity.item.EntityMinecart", new MinecartTransformer());
		}

		register("net.minecraft.entity.EntityLivingBase", new EntityLivingBaseTransformer());
	}
}
