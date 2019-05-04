var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

function log(message) {
	print("[RandomPatches KeyboardListener Transformer]: " + message);
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
		"RandomPatches KeyboardListener Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.client.KeyboardListener"
			},
			"transformer": function(classNode) {
				log("Transforming class: " + classNode.name);

				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(methods[i], "onKeyEvent", "func_197961_a", patchOnKeyEvent)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchOnKeyEvent(instructions) {
	var KEY_B = 0x42;
	var KEY_UNUSED = 0x54;

	var isB;

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(instruction.getOpcode() == Opcodes.BIPUSH && instruction.operand == KEY_B) {
			isB = instruction;
			break;
		}
	}

	var getKey = new VarInsnNode(Opcodes.ILOAD, 3);

	var handleKeypress = new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			"com/therandomlabs/randompatches/patch/client/KeyboardListenerPatch",
			"handleKeypress",
			"(I)V",
			false
	);

	instructions.insertBefore(isB.getPrevious(), getKey);
	instructions.insert(getKey, handleKeypress)

	isB.operand = KEY_UNUSED;
}
