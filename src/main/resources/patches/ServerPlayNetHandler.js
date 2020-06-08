var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");
var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var JumpInsnNode = Java.type("org.objectweb.asm.tree.JumpInsnNode");
var LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

var tickPatched;
var processKeepAlivePatched;
var processPlayerPatched;
var processVehicleMovePatched;

var TICK = ASMAPI.mapMethod("func_73660_a");
var PROCESS_KEEPALIVE = ASMAPI.mapMethod("func_147353_a");
var PROCESS_PLAYER = ASMAPI.mapMethod("func_147347_a");
var PROCESS_VEHICLE_MOVE = ASMAPI.mapMethod("func_184338_a");
var SEND_PACKET = ASMAPI.mapMethod("func_147359_a");
var KEEP_ALIVE_TIME = ASMAPI.mapField("field_194402_f");

function log(message) {
	print("[RandomPatches ServerPlayNetHandler Transformer]: " + message);
}

function patch(method, name, patchFunction) {
	if (method.name != name) {
		return false;
	}

	log("Patching method: " + name + " (" + method.name + ")");
	patchFunction(method.instructions);
	return true;
}

function initializeCoreMod() {
	return {
		"RandomPatches ServerPlayNetHandler Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.network.play.ServerPlayNetHandler"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for (var i in methods) {
					var method = methods[i];

					if (tickPatched && processKeepAlivePatched && processPlayerPatched &&
							processVehicleMovePatched) {
						break;
					}

					if (patch(method, TICK, patchTick)) {
						tickPatched = true;
						continue;
					}

					if (patch(method, PROCESS_KEEPALIVE, patchProcessKeepAlive)) {
						processKeepAlivePatched = true;
						continue;
					}

					if (patch(method, PROCESS_PLAYER, patchProcessPlayer)) {
						processPlayerPatched = true;
						continue;
					}

					if (patch(method, PROCESS_VEHICLE_MOVE, patchProcessVehicleMove)) {
						processVehicleMovePatched = true;
					}
				}

				return classNode;
			}
		}
	};
}

/* Expected result:

final long KEEP_ALIVE_PACKET_INTERVAL = 15000L;
final long READ_TIMEOUT = 90000L;

long keepAliveTime;
long keepAliveKey;
boolean keepAlivePending;

void update() {
	final long currentTime = Util.milliTime();

	if(currentTime - keepAliveTime >= KEEP_ALIVE_PACKET_INTERVAL) {
		if(keepAlivePending) {
			//Inserting code here
			if(currentTime - keepAliveTime >= READ_TIMEOUT) {
				//This line is kept from vanilla
				disconnect(new TextComponentTranslation("disconnect.timeout"));
			}
			//End code insertion
		} else {
			keepAlivePending = true;
			keepAliveTime = currentTime;
			keepAliveKey = currentTime;
			sendPacket(new SPacketKeepAlive(keepAliveKey));
		}
	}
} */

function patchTick(instructions) {
	var keepAliveInterval;
	var jumpIfKeepAlivePending;
	var sendPacket;

	for (var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if (keepAliveInterval == null) {
			if (instruction.getOpcode() == Opcodes.LDC && instruction.cst == 15000) {
				keepAliveInterval = instruction;
			}

			continue;
		}

		if (jumpIfKeepAlivePending == null) {
			if (instruction.getOpcode() == Opcodes.IFEQ &&
				instruction.getPrevious().getOpcode() == Opcodes.GETFIELD) {
				jumpIfKeepAlivePending = instruction;
			}

			continue;
		}

		if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL && instruction.name == SEND_PACKET) {
			sendPacket = instruction;
			break;
		}
	}

	//Get RPConfig.Timeouts#keepAlivePacketIntervalMillis
	instructions.insert(keepAliveInterval, new FieldInsnNode(
		Opcodes.GETSTATIC,
		"com/therandomlabs/randompatches/RPConfig$Timeouts",
		"keepAlivePacketIntervalMillis",
		"J"
	));

	instructions.remove(keepAliveInterval);

	var label = new LabelNode();

	var newInstructions = new InsnList();

	//Get i (currentTimeMillis)
	newInstructions.add(new VarInsnNode(Opcodes.LLOAD, 1));

	//Get ServerPlayNetHandler (this)
	newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

	//Get ServerPlayNetHandler#keepAliveTime
	newInstructions.add(new FieldInsnNode(
		Opcodes.GETFIELD,
		"net/minecraft/network/play/ServerPlayNetHandler",
		KEEP_ALIVE_TIME,
		"J"
	));

	//Substract field_199402_f (keepAliveTime) from i (currentTimeMillis):
	//currentTimeMillis - keepAliveTime
	newInstructions.add(new InsnNode(Opcodes.LSUB));

	//Get RPConfig.Timeouts#readTimeoutMillis
	newInstructions.add(new FieldInsnNode(
		Opcodes.GETSTATIC,
		"com/therandomlabs/randompatches/RPConfig$Timeouts",
		"readTimeoutMillis",
		"J"
	));

	//Compare the subtraction result to readTimeoutMillis and jump if it is not larger:
	//if(currentTimeMillis - lastPingTime >= RPStaticConfig#readTimeoutMillis)
	newInstructions.add(new InsnNode(Opcodes.LCMP));
	newInstructions.add(new JumpInsnNode(Opcodes.IFLT, label));

	instructions.insert(jumpIfKeepAlivePending, newInstructions);

	//Break out of the if(i - keepAliveTime >= 15000L) statement
	instructions.insert(sendPacket, label);
}

function patchProcessKeepAlive(instructions) {
	var aloadFound;
	var aload;

	for (var i = instructions.size() - 1; i >= 0; i--) {
		var instruction = instructions.get(i);

		if (instruction.getOpcode() != Opcodes.ALOAD) {
			continue;
		}

		if (!aloadFound) {
			aloadFound = true;
			continue;
		}

		//Return before SeerverPlayNetHandler#disconnect(ITextComponent) can be called.
		instructions.insertBefore(instruction, new InsnNode(Opcodes.RETURN));
		return;
	}
}

function patchProcessPlayer(instructions) {
	var elytra;
	var normal;

	for (var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if (instruction.getOpcode() != Opcodes.LDC) {
			continue;
		}

		if (elytra == null) {
			if (instruction.cst == 300.0) {
				elytra = instruction;
			}

			continue;
		}

		if (instruction.cst == 100.0) {
			normal = instruction;
			break;
		}
	}

	//Get RPConfig.SpeedLimits#maxPlayerElytraSpeed
	instructions.insert(elytra, new FieldInsnNode(
		Opcodes.GETSTATIC,
		"com/therandomlabs/randompatches/RPConfig$SpeedLimits",
		"maxPlayerElytraSpeed",
		"F"
	));

	instructions.remove(elytra);

	//Get RPConfig.SpeedLimits#maxPlayerSpeed
	instructions.insert(normal, new FieldInsnNode(
		Opcodes.GETSTATIC,
		"com/therandomlabs/randompatches/RPConfig$SpeedLimits",
		"maxPlayerSpeed",
		"F"
	));

	instructions.remove(normal);
}

function patchProcessVehicleMove(instructions) {
	var speed;

	for (var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if (instruction.getOpcode() == Opcodes.LDC && instruction.cst == 100.0) {
			speed = instruction;
			break;
		}
	}

	//Get RPConfig.SpeedLimits#maxPlayerVehicleSpeed
	instructions.insert(speed, new FieldInsnNode(
		Opcodes.GETSTATIC,
		"com/therandomlabs/randompatches/RPConfig$SpeedLimits",
		"maxPlayerVehicleSpeed",
		"D"
	));

	instructions.remove(speed);
}
