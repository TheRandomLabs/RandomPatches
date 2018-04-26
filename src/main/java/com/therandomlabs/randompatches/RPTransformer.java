package com.therandomlabs.randompatches;

import java.util.Arrays;
import java.util.List;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class RPTransformer implements IClassTransformer {
	private static final List<String> classNames = Arrays.asList(
			"net.minecraft.network.NetHandlerPlayServer",
			"net.minecraft.network.NetHandlerLoginServer",
			"net.minecraftforge.fml.common.network.internal.FMLNetworkHandler",
			"net.minecraft.client.gui.GuiIngameMenu"
	);

	static {
		RPConfig.reload();
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		try {
			for(String className : classNames) {
				if(className.equals(transformedName)) {
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

		for(int i = 0; i < methodNode.instructions.size(); i++) {
			final AbstractInsnNode instruction = methodNode.instructions.get(i);
			if(instruction.getType() == AbstractInsnNode.LDC_INSN) {
				final LdcInsnNode ldc = (LdcInsnNode) instruction;
				if(new Long(15000L).equals(ldc.cst)) {
					methodNode.instructions.insert(ldc, new FieldInsnNode(
							Opcodes.GETSTATIC,
							"com/therandomlabs/randompatches/RPConfig",
							"readTimeoutMillis",
							"J"
					));
					methodNode.instructions.remove(ldc);
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
					methodNode.instructions.insert(ldc, new FieldInsnNode(
							Opcodes.GETSTATIC,
							"com/therandomlabs/randompatches/RPConfig",
							"loginTimeout",
							"I"
					));
					methodNode.instructions.remove(ldc);
				}
			}
		}
	}

	public static void patchFMLNetworkHandler(ClassNode node) {
		if(!RPConfig.patchForgeDefaultTimeouts) {
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

	public static void patchGuiIngameMenu(ClassNode node) {
		if(!RPConfig.forceTitleScreenOnDisconnect) {
			return;
		}

		final MethodNode methodNode = findMethod(node, "actionPerformed", "a");

		for(int i = 0, frames = 0; i < methodNode.instructions.size(); i++) {
			final AbstractInsnNode instruction = methodNode.instructions.get(i);

			if(instruction.getType() == AbstractInsnNode.FRAME &&
					((FrameNode) instruction).type == Opcodes.F_SAME) {
				if(++frames > 2) {
					break;
				}
				continue;
			}

			if(frames != 2) {
				continue;
			}

			if(instruction.getType() == AbstractInsnNode.VAR_INSN) {
				final VarInsnNode istore = (VarInsnNode) instruction;
				if(istore.getOpcode() != Opcodes.ISTORE) {
					continue;
				}

				final LabelNode label = new LabelNode();
				final FieldInsnNode getStatic = new FieldInsnNode(
						Opcodes.GETSTATIC,
						"com/therandomlabs/randompatches/RPConfig",
						"forceTitleScreenOnDisconnect",
						"Z"
				);
				final JumpInsnNode ifeq = new JumpInsnNode(Opcodes.IFEQ, label);
				final InsnNode iconst1 = new InsnNode(Opcodes.ICONST_1);
				final VarInsnNode istore2 = new VarInsnNode(Opcodes.ISTORE, 2);

				methodNode.instructions.insert(istore, getStatic);
				methodNode.instructions.insert(getStatic, ifeq);
				methodNode.instructions.insert(ifeq, iconst1);
				methodNode.instructions.insert(iconst1, istore2);
				methodNode.instructions.insert(istore2, label);

				break;
			}
		}
	}

	public static MethodNode findMethod(ClassNode node, String... names) {
		for(MethodNode methodNode : node.methods) {
			for(String name : names) {
				if(name.equals(methodNode.name)) {
					System.out.println("Patching method: " + methodNode.name);
					return methodNode;
				}
			}
		}
		return null;
	}

	public static MethodNode findUpdateMethod(ClassNode node) {
		MethodNode foundNode = null;

		for(MethodNode methodNode : node.methods) {
			if(methodNode.name.equals("func_73660_a") || methodNode.name.equals("update")) {
				foundNode = methodNode;
				break;
			}
			if(methodNode.desc.equals("()V") && !methodNode.name.equals("b")) {
				foundNode = methodNode;
				break;
			}
		}

		if(foundNode == null) {
			return null;
		}

		System.out.println("Patching method: " + foundNode.name);
		return foundNode;
	}
}
