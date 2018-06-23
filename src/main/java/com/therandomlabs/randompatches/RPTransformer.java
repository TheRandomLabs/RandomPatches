package com.therandomlabs.randompatches;

import java.lang.reflect.Method;
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
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class RPTransformer implements IClassTransformer {
	private static final List<String> classNames = Arrays.asList(
			"net.minecraft.network.NetHandlerPlayServer",
			"net.minecraft.network.NetHandlerLoginServer",
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

					final Method method = RPTransformer.class.getDeclaredMethod(
							"patch" + simpleName, ClassNode.class);
					final boolean success = (boolean) method.invoke(this, node);

					if(!success) {
						System.err.println("Failed to patch class: " + className);
					}

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

	/*
		What I'm trying to accomplish:

		final long KEEP_ALIVE_PACKET_INTERVAL = 15000L;
		final long READ_TIMEOUT = 90000L;

		long lastPingTime;
		long keepAliveID;
		boolean shouldDisconnect;

		void update() {
			final long currentTime = currentTimeMillis();

			if(currentTime - lastPingTime >= KEEP_ALIVE_PACKET_INTERVAL) {
				if(shouldDisconnect) {
					if(currentTime - lastPingTime >= READ_TIMEOUT) {
						disconnect(new TextComponentTranslation("disconnect.timeout"));
					}
				} else {
					shouldDisconnect = true;
					lastPingTime = currentTime;
					keepAliveID = currentTime;
					sendPacket(new SPacketKeepAlive(keepAliveID));
				}
			}
		}
	*/
	public static boolean patchNetHandlerPlayServer(ClassNode node) {
		final MethodNode methodNode = findUpdateMethod(node);
		final boolean deobfuscated = "update".equals(methodNode.name);

		final String LAST_PING_TIME = deobfuscated ? "field_194402_f" : "g";
		final String SEND_PACKET = deobfuscated ? "sendPacket" : "a";

		LdcInsnNode keepAliveInterval = null;
		JumpInsnNode ifeq = null;
		MethodInsnNode sendPacket = null;

		for(int i = 0; i < methodNode.instructions.size(); i++) {
			final AbstractInsnNode instruction = methodNode.instructions.get(i);

			if(keepAliveInterval == null) {
				if(instruction.getType() == AbstractInsnNode.LDC_INSN) {
					keepAliveInterval = (LdcInsnNode) instruction;

					if(!((Long) 15000L).equals(keepAliveInterval.cst)) {
						keepAliveInterval = null;
					}
				}

				continue;
			}

			if(ifeq == null) {
				if(instruction.getOpcode() == Opcodes.IFEQ &&
						instruction.getPrevious().getOpcode() == Opcodes.GETFIELD) {
					ifeq = (JumpInsnNode) instruction;
				}

				continue;
			}

			if(sendPacket == null) {
				if(instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
					sendPacket = (MethodInsnNode) instruction;

					if(SEND_PACKET.equals(sendPacket.name)) {
						break;
					}

					sendPacket = null;
				}
			}
		}

		if(sendPacket == null) {
			return false;
		}

		final FieldInsnNode getKeepAliveInterval = new FieldInsnNode(
				Opcodes.GETSTATIC,
				"com/therandomlabs/randompatches/RPConfig",
				"keepAlivePacketIntervalMillis",
				"J"
		);

		methodNode.instructions.insert(keepAliveInterval, getKeepAliveInterval);
		methodNode.instructions.remove(keepAliveInterval);

		final LabelNode label = new LabelNode();

		final VarInsnNode loadCurrentTime = new VarInsnNode(Opcodes.LLOAD, 1);

		final VarInsnNode loadThis = new VarInsnNode(Opcodes.ALOAD, 0);
		final FieldInsnNode getPreviousTime = new FieldInsnNode(
				Opcodes.GETFIELD,
				"net/minecraft/network/NetHandlerPlayServer",
				LAST_PING_TIME,
				"J"
		);

		final InsnNode subtract = new InsnNode(Opcodes.LSUB);

		final FieldInsnNode getReadTimeoutMillis = new FieldInsnNode(
				Opcodes.GETSTATIC,
				"com/therandomlabs/randompatches/RPConfig",
				"readTimeoutMillis",
				"J"
		);

		final InsnNode compare = new InsnNode(Opcodes.LCMP);
		final JumpInsnNode jumpIfNotLarger = new JumpInsnNode(Opcodes.IFLT, label);

		methodNode.instructions.insert(ifeq, loadCurrentTime);
		methodNode.instructions.insert(loadCurrentTime, loadThis);
		methodNode.instructions.insert(loadThis, getPreviousTime);
		methodNode.instructions.insert(getPreviousTime, subtract);
		methodNode.instructions.insert(subtract, getReadTimeoutMillis);
		methodNode.instructions.insert(getReadTimeoutMillis, compare);
		methodNode.instructions.insert(compare, jumpIfNotLarger);

		methodNode.instructions.insert(sendPacket, label);

		return true;
	}

	public static boolean patchNetHandlerLoginServer(ClassNode node) {
		final MethodNode methodNode = findUpdateMethod(node);

		AbstractInsnNode toPatch = null;

		for(int i = 0; i < methodNode.instructions.size(); i++) {
			final AbstractInsnNode instruction = methodNode.instructions.get(i);
			if(instruction.getType() == AbstractInsnNode.LDC_INSN) {
				final LdcInsnNode ldc = (LdcInsnNode) instruction;
				if(new Integer(600).equals(ldc.cst)) {
					toPatch = ldc;
					break;
				}
			}
		}

		if(toPatch == null) {
			return false;
		}

		final FieldInsnNode getLoginTimeout = new FieldInsnNode(
				Opcodes.GETSTATIC,
				"com/therandomlabs/randompatches/RPConfig",
				"loginTimeout",
				"I"
		);

		methodNode.instructions.insert(toPatch, getLoginTimeout);
		methodNode.instructions.remove(toPatch);

		return true;
	}

	public static boolean patchGuiIngameMenu(ClassNode node) {
		final MethodNode methodNode = findMethod(node, "actionPerformed", "a");

		AbstractInsnNode toPatch = null;

		for(int i = 0, frames = 0; i < methodNode.instructions.size() && frames <= 2; i++) {
			final AbstractInsnNode instruction = methodNode.instructions.get(i);

			if(instruction.getType() == AbstractInsnNode.FRAME &&
					((FrameNode) instruction).type == Opcodes.F_SAME) {
				frames++;
			}

			if(frames == 2 && instruction.getType() == AbstractInsnNode.VAR_INSN &&
					instruction.getOpcode() == Opcodes.ISTORE) {
				toPatch = instruction;
				break;
			}
		}

		if(toPatch == null) {
			return false;
		}

		final LabelNode label = new LabelNode();
		final FieldInsnNode getEnabled = new FieldInsnNode(
				Opcodes.GETSTATIC,
				"com/therandomlabs/randompatches/RPConfig",
				"forceTitleScreenOnDisconnect",
				"Z"
		);
		final JumpInsnNode jumpIfNotEnabled = new JumpInsnNode(Opcodes.IFEQ, label);
		final InsnNode loadTrue = new InsnNode(Opcodes.ICONST_1);
		final VarInsnNode storeTrue = new VarInsnNode(Opcodes.ISTORE, 2);

		methodNode.instructions.insert(toPatch, getEnabled);
		methodNode.instructions.insert(getEnabled, jumpIfNotEnabled);
		methodNode.instructions.insert(jumpIfNotEnabled, loadTrue);
		methodNode.instructions.insert(loadTrue, storeTrue);
		methodNode.instructions.insert(storeTrue, label);

		return true;
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
		MethodNode update = null;

		for(MethodNode methodNode : node.methods) {
			if(methodNode.name.equals("update")) {
				update = methodNode;
				break;
			}

			if(methodNode.desc.equals("()V") && !methodNode.name.equals("b")) {
				update = methodNode;
				break;
			}
		}

		if(update == null) {
			return null;
		}

		System.out.println("Patching method: " + update.name);
		return update;
	}
}
