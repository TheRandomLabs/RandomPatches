var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");

function log(message) {
	print("[RandomPatches AbstractOption Transformer]: " + message);
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
		"RandomPatches AbstractOption Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.client.AbstractOption"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for (var i in methods) {
					if (patch(methods[i], "<clinit>", patchClinit)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchClinit(instructions) {
	var stepSize;

	for (var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if (instruction.getOpcode() == Opcodes.LDC && instruction.cst == "options.framerateLimit") {
			stepSize = instructions.get(i + 3);
			break;
		}
	}

	//Get RPConfig.Client#framerateLimitSliderStepSize
	instructions.insert(stepSize, new FieldInsnNode(
		Opcodes.GETSTATIC,
		"com/therandomlabs/randompatches/RPConfig$Client",
		"framerateLimitSliderStepSize",
		"F"
	));
	instructions.remove(stepSize);
}
