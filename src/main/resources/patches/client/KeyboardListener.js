var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

var ON_KEY_EVENT = ASMAPI.mapMethod("func_197961_a");

var KEY_B = 0x42;
var KEY_UNUSED = 0x54;

function log(message) {
	print("[RandomPatches KeyboardListener Transformer]: " + message);
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
		"RandomPatches KeyboardListener Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.client.KeyboardListener"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(methods[i], ON_KEY_EVENT, patchOnKeyEvent)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchOnKeyEvent(instructions) {
	var isB;

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(instruction.getOpcode() == Opcodes.BIPUSH && instruction.operand == KEY_B) {
			isB = instruction;
			break;
		}
	}

	//Get key
	var getKey = new VarInsnNode(Opcodes.ILOAD, 3);

	instructions.insertBefore(isB.getPrevious(), getKey);

	//Call KeyboardListenerPatch#handleKeypress
	instructions.insert(getKey, new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			"com/therandomlabs/randompatches/patch/client/KeyboardListenerPatch",
			"handleKeypress",
			"(I)V",
			false
	));

	isB.operand = KEY_UNUSED;
}
