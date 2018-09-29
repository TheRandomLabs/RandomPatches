package com.therandomlabs.randompatches.core.transformer;

import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.core.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PlayServerTransformer extends Transformer {
	public static final String SEND_PACKET = getName("sendPacket", "func_147359_a");

	@Override
	public void transform(ClassNode node) {
		transformUpdate(findMethod(node, "update", "func_73660_a"));

		if(RandomPatches.SPONGEFORGE_INSTALLED) {
			return;
		}

		transformProcessPlayer(findMethod(node, "processPlayer", "func_147347_a"));
		transformProcessVehicleMove(findMethod(node, "processVehicleMove", "func_184338_a"));
	}

	/* Expected result:

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
	} */

	private static void transformUpdate(MethodNode method) {
		LdcInsnNode keepAliveInterval = null;
		JumpInsnNode ifeq = null;
		MethodInsnNode sendPacket = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

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

			if(instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				sendPacket = (MethodInsnNode) instruction;

				if(SEND_PACKET.equals(sendPacket.name)) {
					break;
				}

				sendPacket = null;
			}
		}

		final FieldInsnNode getKeepAliveInterval = new FieldInsnNode(
				Opcodes.GETSTATIC,
				"com/therandomlabs/randompatches/RPStaticConfig",
				"keepAlivePacketIntervalMillis",
				"J"
		);

		method.instructions.insert(keepAliveInterval, getKeepAliveInterval);
		method.instructions.remove(keepAliveInterval);

		final LabelNode label = new LabelNode();

		final VarInsnNode loadCurrentTime = new VarInsnNode(Opcodes.LLOAD, 1);

		final VarInsnNode loadThis = new VarInsnNode(Opcodes.ALOAD, 0);
		final FieldInsnNode getPreviousTime = new FieldInsnNode(
				Opcodes.GETFIELD,
				"net/minecraft/network/NetHandlerPlayServer",
				"field_194402_f",
				"J"
		);

		final InsnNode subtract = new InsnNode(Opcodes.LSUB);

		final FieldInsnNode getReadTimeoutMillis = new FieldInsnNode(
				Opcodes.GETSTATIC,
				"com/therandomlabs/randompatches/RPStaticConfig",
				"readTimeoutMillis",
				"J"
		);

		final InsnNode compare = new InsnNode(Opcodes.LCMP);
		final JumpInsnNode jumpIfNotLarger = new JumpInsnNode(Opcodes.IFLT, label);

		method.instructions.insert(ifeq, loadCurrentTime);
		method.instructions.insert(loadCurrentTime, loadThis);
		method.instructions.insert(loadThis, getPreviousTime);
		method.instructions.insert(getPreviousTime, subtract);
		method.instructions.insert(subtract, getReadTimeoutMillis);
		method.instructions.insert(getReadTimeoutMillis, compare);
		method.instructions.insert(compare, jumpIfNotLarger);

		method.instructions.insert(sendPacket, label);
	}

	private static void transformProcessPlayer(MethodNode method) {
		LdcInsnNode elytra = null;
		LdcInsnNode normal = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() != Opcodes.LDC) {
				continue;
			}

			final LdcInsnNode ldc = (LdcInsnNode) instruction;

			if(elytra == null && ((Float) 300.0F).equals(ldc.cst)) {
				elytra = ldc;
				continue;
			}

			if(((Float) 100.0F).equals(ldc.cst)) {
				normal = ldc;
				break;
			}
		}

		final FieldInsnNode getElytraMaxSpeed = new FieldInsnNode(
				Opcodes.GETSTATIC,
				"com/therandomlabs/randompatches/RPStaticConfig",
				"maxPlayerElytraSpeed",
				"F"
		);

		final FieldInsnNode getNormalMaxSpeed = new FieldInsnNode(
				Opcodes.GETSTATIC,
				"com/therandomlabs/randompatches/RPStaticConfig",
				"maxPlayerSpeed",
				"F"
		);

		method.instructions.insert(elytra, getElytraMaxSpeed);
		method.instructions.remove(elytra);

		method.instructions.insert(normal, getNormalMaxSpeed);
		method.instructions.remove(normal);
	}

	private static void transformProcessVehicleMove(MethodNode method) {
		LdcInsnNode speed = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(instruction.getOpcode() == Opcodes.LDC) {
				final LdcInsnNode ldc = (LdcInsnNode) instruction;

				if(((Double) 100.0).equals(ldc.cst)) {
					speed = ldc;
					break;
				}
			}
		}

		final FieldInsnNode getVehicleMaxSpeed = new FieldInsnNode(
				Opcodes.GETSTATIC,
				"com/therandomlabs/randompatches/RPStaticConfig",
				"maxPlayerVehicleSpeed",
				"D"
		);

		method.instructions.insert(speed, getVehicleMaxSpeed);
		method.instructions.remove(speed);
	}
}
