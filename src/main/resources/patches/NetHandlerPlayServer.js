var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");
var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var JumpInsnNode = Java.type("org.objectweb.asm.tree.JumpInsnNode");
var LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

var deobfuscated;

var tickPatched;
var processPlayerPatched;
var processVehicleMovePatched;

function log(message) {
	print("[RandomPatches NetHandlerPlayServer Transformer]: " + message);
}

function patch(method, name, srgName, patchFunction) {
	if(method.name != name && method.name != srgName) {
		return false;
	}

	deobfuscated = method.name == name;
	log("Patching method: " + name + " (" + method.name + ")");
	patchFunction(method.instructions);
	return true;
}

function initializeCoreMod() {
	return {
		"RandomPatches NetHandlerPlayServer Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.network.NetHandlerPlayServer"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for(var i in methods) {
					var method = methods[i];

					if(tickPatched && processPlayerPatched && processVehicleMovePatched) {
						break;
					}

					if(patch(method, "tick", "func_73660_a", patchTick)) {
						tickPatched = true;
						continue;
					}

					if(patch(method, "processPlayer", "func_147347_a", patchProcessPlayer)) {
						processPlayerPatched = true;
						continue;
					}

					if(patch(
							method, "processVehicleMove", "func_184338_a", patchProcessVehicleMove
					)) {
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
	var jumpIfShouldNotDisconnect;
	var sendPacket;

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(keepAliveInterval == null) {
			if(instruction.getOpcode() == Opcodes.LDC && instruction.cst == 15000) {
				keepAliveInterval = instruction;
			}

			continue;
		}

		if(jumpIfShouldNotDisconnect == null) {
			if(instruction.getOpcode() == Opcodes.IFEQ &&
					instruction.getPrevious().getOpcode() == Opcodes.GETFIELD) {
				jumpIfShouldNotDisconnect = instruction;
			}

			continue;
		}

		if(instruction.getOpcode() == Opcodes.INVOKEVIRTUAL &&
				(instruction.name == "sendPacket" || instruction.name == "func_147359_a")) {
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

	//Get NetHandlerPlayServer (this)
	newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

	//Get NetHandlerPlayServer#keepAliveTime
	newInstructions.add(new FieldInsnNode(
			Opcodes.GETFIELD,
			"net/minecraft/network/NetHandlerPlayServer",
			deobfuscated ? "keepAliveTime" : "field_194402_f",
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

	instructions.insert(jumpIfShouldNotDisconnect, newInstructions);

	//Break out of the if(i - keepAliveTime >= 15000L) statement
	instructions.insert(sendPacket, label);
}

function patchProcessPlayer(instructions) {
	var elytra;
	var normal;

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(instruction.getOpcode() != Opcodes.LDC) {
			continue;
		}

		if(elytra == null) {
			if(instruction.cst == 300.0) {
				elytra = instruction;
			}

			continue;
		}

		if(instruction.cst == 100.0) {
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

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(instruction.getOpcode() == Opcodes.LDC && instruction.cst == 100.0) {
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
