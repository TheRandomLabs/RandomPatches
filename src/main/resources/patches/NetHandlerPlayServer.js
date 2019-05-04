var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");
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
				log("Transforming class: " + classNode.name);

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
	var ifeq;
	var sendPacket;

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(keepAliveInterval == null) {
			if(instruction.getOpcode() == Opcodes.LDC && instruction.cst == 15000) {
				keepAliveInterval = instruction;
			}

			continue;
		}

		if(ifeq == null) {
			if(instruction.getOpcode() == Opcodes.IFEQ &&
					instruction.getPrevious().getOpcode() == Opcodes.GETFIELD) {
				ifeq = instruction;
			}

			continue;
		}

		if(instruction.getOpcode() == Opcodes.INVOKEVIRTUAL &&
				(instruction.name == "sendPacket" || instruction.name == "func_147359_a")) {
			sendPacket = instruction;
			break;
		}
	}

	var getKeepAliveInterval = new FieldInsnNode(
			Opcodes.GETSTATIC,
			"com/therandomlabs/randompatches/RPConfig$Timeouts",
			"keepAlivePacketIntervalMillis",
			"J"
	);

	instructions.insert(keepAliveInterval, getKeepAliveInterval);
	instructions.remove(keepAliveInterval);

	var label = new LabelNode();

	var loadCurrentTime = new VarInsnNode(Opcodes.LLOAD, 1);

	var loadThis = new VarInsnNode(Opcodes.ALOAD, 0);
	var getKeepAliveTime = new FieldInsnNode(
			Opcodes.GETFIELD,
			"net/minecraft/network/NetHandlerPlayServer",
			deobfuscated ? "keepAliveTime" : "field_194402_f",
			"J"
	);

	//i - field_194402_f
	//currentTime - keepAliveTime
	var subtract = new InsnNode(Opcodes.LSUB);

	var getReadTimeoutMillis = new FieldInsnNode(
			Opcodes.GETSTATIC,
			"com/therandomlabs/randompatches/RPConfig$Timeouts",
			"readTimeoutMillis",
			"J"
	);

	//if(currentTime - keepAliveTime >= RPStaticConfig.readTimeoutMillis)
	var compare = new InsnNode(Opcodes.LCMP);
	var jumpIfNotLarger = new JumpInsnNode(Opcodes.IFLT, label);

	instructions.insert(ifeq, loadCurrentTime);
	instructions.insert(loadCurrentTime, loadThis);
	instructions.insert(loadThis, getKeepAliveTime);
	instructions.insert(getKeepAliveTime, subtract);
	instructions.insert(subtract, getReadTimeoutMillis);
	instructions.insert(getReadTimeoutMillis, compare);
	instructions.insert(compare, jumpIfNotLarger);

	//Break out of the if(i - field_194402_f >= 15000L) statement
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

	var getElytraMaxSpeed = new FieldInsnNode(
			Opcodes.GETSTATIC,
			"com/therandomlabs/randompatches/RPConfig$SpeedLimits",
			"maxPlayerElytraSpeed",
			"F"
	);

	var getNormalMaxSpeed = new FieldInsnNode(
			Opcodes.GETSTATIC,
			"com/therandomlabs/randompatches/RPConfig$SpeedLimits",
			"maxPlayerSpeed",
			"F"
	);

	instructions.insert(elytra, getElytraMaxSpeed);
	instructions.remove(elytra);

	instructions.insert(normal, getNormalMaxSpeed);
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

	var getVehicleMaxSpeed = new FieldInsnNode(
			Opcodes.GETSTATIC,
			"com/therandomlabs/randompatches/RPConfig$SpeedLimits",
			"maxPlayerVehicleSpeed",
			"D"
	);

	instructions.insert(speed, getVehicleMaxSpeed);
	instructions.remove(speed);
}
