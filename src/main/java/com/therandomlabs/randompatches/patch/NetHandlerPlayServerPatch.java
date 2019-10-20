package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randomlib.TRLUtils;
import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.config.RPConfig;
import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class NetHandlerPlayServerPatch extends Patch {
	public static final String TIMEOUTS_CONFIG = getName(RPConfig.Timeouts.class);
	public static final String SPEED_LIMITS_CONFIG = getName(RPConfig.SpeedLimits.class);
	public static final String SEND_PACKET = getName("sendPacket", "func_147359_a");

	@Override
	public boolean apply(ClassNode node) {
		patchUpdate(findInstructions(node, "update", "func_73660_a"));

		if (!RandomPatches.SPONGEFORGE_INSTALLED) {
			patchProcessPlayer(findInstructions(node, "processPlayer", "func_147347_a"));

			if (!RandomPatches.ICE_AND_FIRE_INSTALLED) {
				patchProcessVehicleMove(findInstructions(
						node, "processVehicleMove", "func_184338_a"
				));
			}
		}

		return true;
	}

	/* Expected result:

	final long KEEP_ALIVE_PACKET_INTERVAL = 15000L;
	final long READ_TIMEOUT = 90000L;

	long lastPingTime;
	long keepAliveID;
	boolean keepAlivePending;

	void update() {
		final long currentTimeMillis = currentTimeMillis();

		if(currentTimeMillis - lastPingTime >= KEEP_ALIVE_PACKET_INTERVAL) {
			if(keepAlivePending) {
				//Inserting code here
				if(currentTimeMillis - lastPingTime >= READ_TIMEOUT) {
					//This line is kept from vanilla
					disconnect(new TextComponentTranslation("disconnect.timeout"));
				}
				//End code insertion
			} else {
				keepAlivePending = true;
				lastPingTime = currentTimeMillis;
				keepAliveID = currentTimeMillis;
				sendPacket(new SPacketKeepAlive(keepAliveID));
			}
		}
	} */

	private static void patchUpdate(InsnList instructions) {
		LdcInsnNode keepAliveInterval = null;
		JumpInsnNode jumpIfKeepAlivePending = null;
		MethodInsnNode sendPacket = null;

		for (int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if (keepAliveInterval == null) {
				if (instruction.getOpcode() == Opcodes.LDC) {
					keepAliveInterval = (LdcInsnNode) instruction;

					if (TRLUtils.MC_VERSION_NUMBER > 11) {
						if (!((Long) 15000L).equals(keepAliveInterval.cst)) {
							keepAliveInterval = null;
						}
					} else {
						if (!((Long) 40L).equals(keepAliveInterval.cst)) {
							keepAliveInterval = null;
						}
					}
				}

				continue;
			}

			if (TRLUtils.MC_VERSION_NUMBER < 12) {
				break;
			}

			if (jumpIfKeepAlivePending == null) {
				if (instruction.getOpcode() == Opcodes.IFEQ &&
						instruction.getPrevious().getOpcode() == Opcodes.GETFIELD) {
					jumpIfKeepAlivePending = (JumpInsnNode) instruction;
				}

				continue;
			}

			if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				sendPacket = (MethodInsnNode) instruction;

				if (SEND_PACKET.equals(sendPacket.name)) {
					break;
				}

				sendPacket = null;
			}
		}

		//Get RPConfig.Timeouts#keepAlivePacketInterval (in milliseconds above 1.11)
		instructions.insert(keepAliveInterval, new FieldInsnNode(
				Opcodes.GETSTATIC,
				TIMEOUTS_CONFIG,
				TRLUtils.MC_VERSION_NUMBER > 11 ?
						"keepAlivePacketIntervalMillis" : "keepAlivePacketIntervalLong",
				"J"
		));

		instructions.remove(keepAliveInterval);

		if (TRLUtils.MC_VERSION_NUMBER < 12) {
			return;
		}

		final LabelNode label = new LabelNode();

		final InsnList newInstructions = new InsnList();

		//Get i (currentTimeMillis)
		newInstructions.add(new VarInsnNode(Opcodes.LLOAD, 1));

		//Get NetHandlerPlayServer (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get NetHandlerPlayServer#field_194402_f (lastPingTime)
		newInstructions.add(new FieldInsnNode(
				Opcodes.GETFIELD,
				"net/minecraft/network/NetHandlerPlayServer",
				"field_194402_f",
				"J"
		));

		//Substract field_199402_f (lastPingTime) from i (currentTimeMillis):
		//currentTimeMillis - lastPingTime
		newInstructions.add(new InsnNode(Opcodes.LSUB));

		//Get RPConfig.Timeouts#readTimeoutMillis
		newInstructions.add(new FieldInsnNode(
				Opcodes.GETSTATIC,
				TIMEOUTS_CONFIG,
				"readTimeoutMillis",
				"J"
		));

		//Compare the subtraction result to readTimeoutMillis and jump if it is not larger:
		//if(currentTimeMillis - lastPingTime >= RPStaticConfig#readTimeoutMillis)
		newInstructions.add(new InsnNode(Opcodes.LCMP));
		newInstructions.add(new JumpInsnNode(Opcodes.IFLT, label));

		instructions.insert(jumpIfKeepAlivePending, newInstructions);

		//Break out of the if(i - field_194402_f >= 15000L) statement
		instructions.insert(sendPacket, label);
	}

	private static void patchProcessPlayer(InsnList instructions) {
		LdcInsnNode elytra = null;
		LdcInsnNode normal = null;

		for (int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if (instruction.getOpcode() != Opcodes.LDC) {
				continue;
			}

			final LdcInsnNode ldc = (LdcInsnNode) instruction;

			if (elytra == null) {
				if (((Float) 300.0F).equals(ldc.cst)) {
					elytra = ldc;
				}

				continue;
			}

			if (((Float) 100.0F).equals(ldc.cst)) {
				normal = ldc;
				break;
			}
		}

		//Get RPConfig.SpeedLimits#maxPlayerElytraSpeed
		instructions.insert(elytra, new FieldInsnNode(
				Opcodes.GETSTATIC,
				SPEED_LIMITS_CONFIG,
				"maxPlayerElytraSpeed",
				"F"
		));

		instructions.remove(elytra);

		//Get RPConfig.SpeedLimits#maxPlayerSpeed
		instructions.insert(normal, new FieldInsnNode(
				Opcodes.GETSTATIC,
				SPEED_LIMITS_CONFIG,
				"maxPlayerSpeed",
				"F"
		));

		instructions.remove(normal);
	}

	private static void patchProcessVehicleMove(InsnList instructions) {
		LdcInsnNode speed = null;

		for (int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if (instruction.getOpcode() == Opcodes.LDC) {
				final LdcInsnNode ldc = (LdcInsnNode) instruction;

				if (((Double) 100.0).equals(ldc.cst)) {
					speed = ldc;
					break;
				}
			}
		}

		//Get RPConfig.SpeedLimits#maxPlayerVehicleSpeed
		instructions.insert(speed, new FieldInsnNode(
				Opcodes.GETSTATIC,
				SPEED_LIMITS_CONFIG,
				"maxPlayerVehicleSpeed",
				"D"
		));

		instructions.remove(speed);
	}
}
