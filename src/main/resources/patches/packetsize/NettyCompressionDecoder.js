var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");

var VANILLA_LIMIT = 0x200000;

function log(message) {
	print("[RandomPatches NettyCompressionDecoder Transformer]: " + message);
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
		"RandomPatches NettyCompressionDecoder Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.network.NettyCompressionDecoder"
			},
			"transformer": function(classNode) {
				log("Transforming class: " + classNode.name);

				var methods = classNode.methods;

				for (var i in methods) {
					if (patch(methods[i], "decode", patchDecode)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchDecode(instructions) {
	var limit1;
	var limit2;

	for (var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if (instruction.getOpcode() == Opcodes.LDC) {
			if (instruction.cst == VANILLA_LIMIT) {
				if (limit1 == null) {
					limit1 = instruction;
				} else {
					limit2 = instruction;
					break;
				}
			}
		}
	}

	//Get RPConfig.Misc#packetSizeLimit
	instructions.insert(limit1, new FieldInsnNode(
		Opcodes.GETSTATIC,
		"com/therandomlabs/randompatches/RPConfig$Misc",
		"packetSizeLimit",
		"I"
	));

	instructions.remove(limit1);

	//Get RPConfig.Misc#packetSizeLimit
	instructions.insert(limit2, new FieldInsnNode(
		Opcodes.GETSTATIC,
		"com/therandomlabs/randompatches/RPConfig$Misc",
		"packetSizeLimit",
		"I"
	));

	instructions.remove(limit2);
}
