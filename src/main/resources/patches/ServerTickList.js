var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

function log(message) {
	print("[RandomPatches ServerTickList Transformer]: " + message);
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
		"RandomPatches ServerTickList Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.world.ServerTickList"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for (var i in methods) {
					if (patch(methods[i], "<init>", patchInit)) {
						methods[i].localVariables.clear();
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchInit(instructions) {
	for (var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if (instruction.getOpcode() == Opcodes.INVOKESTATIC) {
			if ("newHashSet".equals(instruction.name)) {
				instruction.owner = "com/therandomlabs/randompatches/hook/ServerTickListHook$" +
					"NextTickListEntryHashSet";
				break;
			}
		}
	}
}
