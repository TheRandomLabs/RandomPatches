var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");

var TICK = ASMAPI.mapMethod("func_73660_a");

function log(message) {
	print("[RandomPatches NetHandlerLoginServer Transformer]: " + message);
}

function patch(method, name, patchFunction) {
	if(method.name != name) {
		return false;
	}

	log("Patching method: " + name + " (" + method.name + ")");
	patchFunction(method.instructions);
	return true;
}

function initializeCoreMod() {
	return {
		"RandomPatches NetHandlerLoginServer Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.network.NetHandlerLoginServer"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(methods[i], TICK, patchTick)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchTick(instructions) {
	var loginTimeout;

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(instruction.getOpcode() == Opcodes.SIPUSH && instruction.operand == 600) {
			loginTimeout = instruction;
			break;
		}
	}

	//Get RPConfig.Timeouts#loginTimeout
	instructions.insert(loginTimeout, new FieldInsnNode(
			Opcodes.GETSTATIC,
			"com/therandomlabs/randompatches/RPConfig$Timeouts",
			"loginTimeout",
			"I"
	));

	instructions.remove(loginTimeout);
}
