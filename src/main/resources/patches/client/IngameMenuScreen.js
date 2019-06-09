var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");
var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var JumpInsnNode = Java.type("org.objectweb.asm.tree.JumpInsnNode");
var LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

var ON_CLICK = ASMAPI.mapMethod("func_194829_a");

function log(message) {
	print("[RandomPatches IngameMenuScreen Transformer]: " + message);
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
		"RandomPatches IngameMenuScreen Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.client.gui.screen.IngameMenuScreen"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(methods[i], ON_CLICK, patchOnClick)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchOnClick(instructions) {
	var storeIsIntegratedServerRunning = null;

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(instruction.getOpcode() == Opcodes.ISTORE) {
			storeIsIntegratedServerRunning = instruction;
			break;
		}
	}

	var newInstructions = new InsnList();

	var label = new LabelNode();

	//Get RPConfig.Client#forceTitleScreenOnDisconnect
	newInstructions.add(new FieldInsnNode(
			Opcodes.GETSTATIC,
			"com/therandomlabs/randompatches/RPConfig$Client",
			"forceTitleScreenOnDisconnect",
			"Z"
	));

	//Jump if not enabled
	newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, label));

	//Load true
	newInstructions.add(new InsnNode(Opcodes.ICONST_1));

	//Store true to flag (isIntegratedServerRunning)
	newInstructions.add(new VarInsnNode(Opcodes.ISTORE, 5));

	newInstructions.add(label);

	instructions.insert(storeIsIntegratedServerRunning, newInstructions);
}
