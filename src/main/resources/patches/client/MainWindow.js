var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");

function log(message) {
	print("[RandomPatches MainWindow Transformer]: " + message);
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
		"RandomPatches MainWindow Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.client.MainWindow"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(methods[i], "<init>", patchInit)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchInit(instructions) {
	var title;

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(instruction.getOpcode() == Opcodes.ALOAD && instruction.var == 5) {
			title = instruction;
			break;
		}
	}

	//Get RPConfig.Window#title
	instructions.insert(title, new FieldInsnNode(
			Opcodes.GETSTATIC,
			"com/therandomlabs/randompatches/RPConfig$Window",
			"title",
			"Ljava/lang/String;"
	));

	instructions.remove(title);
}
