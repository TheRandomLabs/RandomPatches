var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
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

	isB.operand = KEY_UNUSED;

	var newInstructions = new InsnList();

	//Get key
	newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 3));

	//Get scanCode
	newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 4));

	//Call KeyboardListenerPatch#handleKeypress
	newInstructions.add(new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			"com/therandomlabs/randompatches/patch/client/KeyboardListenerPatch",
			"handleKeypress",
			"(II)V",
			false
	));

	//Insert before "key == 66"
	instructions.insertBefore(isB.getPrevious(), newInstructions);
}
