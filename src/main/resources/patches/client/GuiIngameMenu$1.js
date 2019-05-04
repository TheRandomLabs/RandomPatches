var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");
var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var JumpInsnNode = Java.type("org.objectweb.asm.tree.JumpInsnNode");
var LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

function log(message) {
	print("[RandomPatches GuiIngameMenu$1 Transformer]: " + message);
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
		"RandomPatches GuiIngameMenu$1 Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.client.gui.GuiIngameMenu$1"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(methods[i], "onClick", "func_194829_a", patchOnClick)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchOnClick(instructions) {
	var storeIsIntegratedServerRunning;

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(instruction.getOpcode() == Opcodes.ISTORE) {
			storeIsIntegratedServerRunning = instruction;
			break;
		}
	}

	var label = new LabelNode();
	var getEnabled = new FieldInsnNode(
			Opcodes.GETSTATIC,
			"com/therandomlabs/randompatches/RPConfig$Client",
			"forceTitleScreenOnDisconnect",
			"Z"
	);
	var jumpIfNotEnabled = new JumpInsnNode(Opcodes.IFEQ, label);
	var loadTrue = new InsnNode(Opcodes.ICONST_1);
	var storeTrue = new VarInsnNode(Opcodes.ISTORE, 5);

	instructions.insert(storeIsIntegratedServerRunning, getEnabled);
	instructions.insert(getEnabled, jumpIfNotEnabled);
	instructions.insert(jumpIfNotEnabled, loadTrue);
	instructions.insert(loadTrue, storeTrue);
	instructions.insert(storeTrue, label);
}
