var Opcodes = Java.type("org.objectweb.asm.Opcodes");

function log(message) {
	print("[RandomPatches LanguageScreen Transformer]: " + message);
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
		"RandomPatches LanguageScreen Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.client.gui.screen.LanguageScreen"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for (var i in methods) {
					if (patch(methods[i], "lambda$init$1", init)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function init(instructions) {
	for (var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if (instruction.getOpcode() == Opcodes.INVOKESTATIC &&
			instruction.name == "refreshResources") {
			instruction.owner =
				"com/therandomlabs/randompatches/hook/client/LanguageScreenListPatch";
			instruction.name = "reloadLanguage";
			return;
		}
	}
}
