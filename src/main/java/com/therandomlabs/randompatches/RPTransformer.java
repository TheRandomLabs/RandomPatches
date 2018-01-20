package com.therandomlabs.randompatches;

import java.util.Arrays;
import java.util.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import net.minecraft.launchwrapper.IClassTransformer;

public class RPTransformer implements IClassTransformer {
	private static final List<String> classNames = Arrays.asList(
			"net.minecraft.network.NetHandlerPlayServer",
			"net.minecraft.network.NetHandlerLoginServer",
			"net.minecraftforge.fml.common.network.internal.FMLNetworkHandler"
	);

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		try {
			for(String className : classNames) {
				if(className.equals(transformedName)) {
					RPConfig.init();

					System.out.println("Patching class: " + className);

					final ClassReader reader = new ClassReader(basicClass);
					final ClassNode node = new ClassNode();
					reader.accept(node, 0);

					final String[] split = className.split("\\.");
					final String simpleName = split[split.length - 1];
					RPTransformer.class.getDeclaredMethod("patch" + simpleName, ClassNode.class).
							invoke(this, node);

					final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
					node.accept(writer);
					return writer.toByteArray();
				}
			}
		} catch(Exception ex) {
			System.err.println("An error occurred while transforming " + transformedName + ':');
			ex.printStackTrace();
		}

		return basicClass;
	}

	public static void patchNetHandlerPlayServer(ClassNode node) {
		final MethodNode methodNode = findUpdateMethod(node);
		System.out.println("Patching method: " + methodNode.name);

		for(int i = 0; i < methodNode.instructions.size(); i++) {
			final AbstractInsnNode instruction = methodNode.instructions.get(i);
			if(instruction.getType() == AbstractInsnNode.LDC_INSN) {
				final LdcInsnNode ldc = (LdcInsnNode) instruction;
				if(new Long(15000L).equals(ldc.cst)) {
					ldc.cst = RPConfig.readTimeout * 1000L;
				}
			}
		}
	}

	public static void patchNetHandlerLoginServer(ClassNode node) {
		final MethodNode methodNode = findUpdateMethod(node);
		System.out.println("Patching method: " + methodNode.name);

		for(int i = 0; i < methodNode.instructions.size(); i++) {
			final AbstractInsnNode instruction = methodNode.instructions.get(i);
			if(instruction.getType() == AbstractInsnNode.LDC_INSN) {
				final LdcInsnNode ldc = (LdcInsnNode) instruction;
				if(new Integer(600).equals(ldc.cst)) {
					ldc.cst = RPConfig.loginTimeout;
				}
			}
		}
	}

	public static void patchFMLNetworkHandler(ClassNode node) {
		if(!RPConfig.patchForgeDefaultTimeouts) {
			System.setProperty("fml.readTimeout", Integer.toString(RPConfig.readTimeout));
			System.setProperty("fml.loginTimeout", Integer.toString(RPConfig.loginTimeout));
			return;
		}

		final MethodNode methodNode = findMethod(node, "<clinit>");

		for(int i = 0; i < methodNode.instructions.size(); i++) {
			final AbstractInsnNode instruction = methodNode.instructions.get(i);
			if(instruction.getType() == AbstractInsnNode.LDC_INSN) {
				final LdcInsnNode ldc = (LdcInsnNode) instruction;
				if("30".equals(ldc.cst)) {
					ldc.cst = Integer.toString(RPConfig.readTimeout);
				} else if("600".equals(ldc.cst)) {
					ldc.cst = Integer.toString(RPConfig.loginTimeout);
				}
			}
		}
	}

	public static MethodNode findMethod(ClassNode node, String... names) {
		for(MethodNode methodNode : node.methods) {
			for(String name : names) {
				if(name.equals(methodNode.name)) {
					return methodNode;
				}
			}
		}
		return null;
	}

	public static MethodNode findUpdateMethod(ClassNode node) {
		for(MethodNode methodNode : node.methods) {
			if(methodNode.name.equals("func_73660_a") || methodNode.name.equals("upate")) {
				return methodNode;
			}
			if(methodNode.desc.equals("()V") && !methodNode.name.equals("b")) {
				return methodNode;
			}
		}
		return null;
	}
}
