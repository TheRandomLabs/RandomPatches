var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");

function log(message) {
	print("[RandomPatches MainWindow Transformer]: " + message);
}

function patch(method, name, srgName, patchFunction) {
	if(method.name != name && method.name != srgName) {
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
					if(patch(methods[i], "<init>", "<init>", patchConstructor)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchConstructor(instructions) {
	var title;
	var loadIcon;

	var deobfuscated;

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(title == null) {
			if(instruction.getOpcode() == Opcodes.LDC && instruction.cst == "Minecraft 1.13.2") {
				title = instruction;
			}

			continue;
		}

		if(instruction.getOpcode() == Opcodes.INVOKESPECIAL &&
				(instruction.name == "loadIcon" || instruction.name == "func_198110_t")) {
			loadIcon = instruction;
			deobfuscated = loadIcon.name == "loadIcon";
			break;
		}
	}

	instructions.insert(title, new FieldInsnNode(
			Opcodes.GETSTATIC,
			"com/therandomlabs/randompatches/RPConfig$Window",
			"title",
			"Ljava/lang/String;"
	));
	instructions.remove(title);

	//loadIcon.getPrevious() is ALOAD 0
	instructions.insert(loadIcon.getPrevious(), new FieldInsnNode(
			Opcodes.GETFIELD,
			"net/minecraft/client/MainWindow",
			deobfuscated ? "handle" : "field_198119_f",
			"J"
	));

	loadIcon.opcode = Opcodes.INVOKESTATIC;
	loadIcon.owner = "com/therandomlabs/randompatches/client/WindowIconHandler";
	loadIcon.name = "setWindowIcon";
	loadIcon.desc = "(J)V"
}
