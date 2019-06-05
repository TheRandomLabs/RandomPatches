var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");

var LOAD_ICON = ASMAPI.mapMethod("func_198110_t");
var HANDLE = ASMAPI.mapField("field_198119_f");

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
	var loadIcon;

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(title == null) {
			if(instruction.getOpcode() == Opcodes.LDC && instruction.cst == "Minecraft 1.13.2") {
				title = instruction;
			}

			continue;
		}

		if(instruction.getOpcode() == Opcodes.INVOKESPECIAL && instruction.name == LOAD_ICON) {
			loadIcon = instruction;
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

	//loadIcon.getPrevious() is ALOAD 0 (gets MainWindow (this))
	//Get MainWindow#handle
	instructions.insert(loadIcon.getPrevious(), new FieldInsnNode(
			Opcodes.GETFIELD,
			"net/minecraft/client/MainWindow",
			HANDLE,
			"J"
	));

	//Call WindowIconHandler#setWindowIcon
	loadIcon.opcode = Opcodes.INVOKESTATIC;
	loadIcon.owner = "com/therandomlabs/randompatches/client/WindowIconHandler";
	loadIcon.name = "setWindowIcon";
	loadIcon.desc = "(J)V"
}
