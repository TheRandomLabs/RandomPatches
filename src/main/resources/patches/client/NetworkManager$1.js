var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");

function log(message) {
	print("[RandomPatches NetworkManager$1 Transformer]: " + message);
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
		"RandomPatches NetworkManager$1 Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.network.NetworkManager$1"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for (var i in methods) {
					if (patch(methods[i], "initChannel", patchInitChannel)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchInitChannel(instructions) {
	for (var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if (instruction.getOpcode() == Opcodes.BIPUSH) {
			instructions.insert(instruction, new FieldInsnNode(
				Opcodes.GETSTATIC,
				"com/therandomlabs/randompatches/RPConfig$Timeouts",
				"readTimeout",
				"I"
			));
			instructions.remove(instruction);
			return;
		}
	}
}
