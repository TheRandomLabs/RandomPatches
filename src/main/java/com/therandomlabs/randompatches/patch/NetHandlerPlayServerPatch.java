package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randomlib.TRLUtils;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.config.RPConfig;
import com.therandomlabs.randompatches.core.Patch;
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

public final class NetHandlerPlayServerPatch extends Patch {
	public static final String TIMEOUTS_CONFIG = getName(RPConfig.Timeouts.class);
	public static final String SPEED_LIMITS_CONFIG = getName(RPConfig.SpeedLimits.class);
	public static final String SEND_PACKET = getName("sendPacket", "func_147359_a");

	@Override
	public boolean apply(ClassNode node) {
		patchUpdate(findMethod(node, "update", "func_73660_a"));

		if(!RandomPatches.SPONGEFORGE_INSTALLED) {
			patchProcessPlayer(findMethod(node, "processPlayer", "func_147347_a"));
			patchProcessVehicleMove(findMethod(node, "processVehicleMove", "func_184338_a"));
		}

		return true;
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
				//Inserting code here
				if(currentTime - lastPingTime >= READ_TIMEOUT) {
					//This line is kept from vanilla
					disconnect(new TextComponentTranslation("disconnect.timeout"));
				}
				//End code insertion
			} else {
				shouldDisconnect = true;
				lastPingTime = currentTime;
				keepAliveID = currentTime;
				sendPacket(new SPacketKeepAlive(keepAliveID));
			}
		}
	} */

	@SuppressWarnings("Duplicates")
	private static void patchUpdate(MethodNode method) {
		LdcInsnNode keepAliveInterval = null;
		JumpInsnNode ifeq = null;
		MethodInsnNode sendPacket = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			if(keepAliveInterval == null) {
				if(instruction.getOpcode() == Opcodes.LDC) {
					keepAliveInterval = (LdcInsnNode) instruction;

					if(TRLUtils.MC_VERSION_NUMBER > 11) {
						if(!((Long) 15000L).equals(keepAliveInterval.cst)) {
							keepAliveInterval = null;
						}
					} else {
						if(!((Long) 40L).equals(keepAliveInterval.cst)) {
							keepAliveInterval = null;
						}
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
				TIMEOUTS_CONFIG,
				TRLUtils.MC_VERSION_NUMBER > 11 ?
						"keepAlivePacketIntervalMillis" : "keepAlivePacketIntervalLong",
				"J"
		);

		method.instructions.insert(keepAliveInterval, getKeepAliveInterval);
		method.instructions.remove(keepAliveInterval);

		if(TRLUtils.MC_VERSION_NUMBER < 12) {
			return;
		}

		final LabelNode label = new LabelNode();

		final VarInsnNode loadCurrentTime = new VarInsnNode(Opcodes.LLOAD, 1);

		final VarInsnNode loadThis = new VarInsnNode(Opcodes.ALOAD, 0);
		final FieldInsnNode getLastPingTime = new FieldInsnNode(
				Opcodes.GETFIELD,
				"net/minecraft/network/NetHandlerPlayServer",
				"field_194402_f",
				"J"
		);

		//i - field_194402_f
		//currentTime - lastPingTime
		final InsnNode subtract = new InsnNode(Opcodes.LSUB);

		final FieldInsnNode getReadTimeoutMillis = new FieldInsnNode(
				Opcodes.GETSTATIC,
				TIMEOUTS_CONFIG,
				"readTimeoutMillis",
				"J"
		);

		//if(currentTime - lastPingTime >= RPStaticConfig.readTimeoutMillis)
		final InsnNode compare = new InsnNode(Opcodes.LCMP);
		final JumpInsnNode jumpIfNotLarger = new JumpInsnNode(Opcodes.IFLT, label);

		method.instructions.insert(ifeq, loadCurrentTime);
		method.instructions.insert(loadCurrentTime, loadThis);
		method.instructions.insert(loadThis, getLastPingTime);
		method.instructions.insert(getLastPingTime, subtract);
		method.instructions.insert(subtract, getReadTimeoutMillis);
		method.instructions.insert(getReadTimeoutMillis, compare);
		method.instructions.insert(compare, jumpIfNotLarger);

		//Break out of the if(i - field_194402_f >= 15000L) statement
		method.instructions.insert(sendPacket, label);
	}

	private static void patchProcessPlayer(MethodNode method) {
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
				SPEED_LIMITS_CONFIG,
				"maxPlayerElytraSpeed",
				"F"
		);

		final FieldInsnNode getNormalMaxSpeed = new FieldInsnNode(
				Opcodes.GETSTATIC,
				SPEED_LIMITS_CONFIG,
				"maxPlayerSpeed",
				"F"
		);

		method.instructions.insert(elytra, getElytraMaxSpeed);
		method.instructions.remove(elytra);

		method.instructions.insert(normal, getNormalMaxSpeed);
		method.instructions.remove(normal);
	}

	private static void patchProcessVehicleMove(MethodNode method) {
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
				SPEED_LIMITS_CONFIG,
				"maxPlayerVehicleSpeed",
				"D"
		);

		method.instructions.insert(speed, getVehicleMaxSpeed);
		method.instructions.remove(speed);
	}
}
