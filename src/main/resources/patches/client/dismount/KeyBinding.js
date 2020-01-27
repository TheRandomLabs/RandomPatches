var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var JumpInsnNode = Java.type("org.objectweb.asm.tree.JumpInsnNode");
var LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

var CONFLICTS = ASMAPI.mapMethod("func_197983_b");

function log(message) {
	print("[RandomPatches KeyBinding Transformer]: " + message);
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
		"RandomPatches KeyBinding Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.client.settings.KeyBinding"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for (var i in methods) {
					if (patch(methods[i], CONFLICTS, patchConflicts)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchConflicts(instructions) {
	var label = new LabelNode();

	var newInstructions = new InsnList();

	//Get KeyBinding (this)
	newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

	//Get binding (other KeyBinding)
	newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));

	//Call ClientPlayerEntityHook$DismountKeybind#isDismountAndSneak
	newInstructions.add(new MethodInsnNode(
		Opcodes.INVOKESTATIC,
		"com/therandomlabs/randompatches/hook/client/dismount/ClientPlayerEntityPatch$" +
		"DismountKeybind",
		"isDismountAndSneak",
		"(Lnet/minecraft/client/settings/KeyBinding;" +
		"Lnet/minecraft/client/settings/KeyBinding;)Z",
		false
	));

	//If false, continue
	newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, label));

	//Otherwise, load false
	newInstructions.add(new InsnNode(Opcodes.ICONST_0));

	//Then return
	newInstructions.add(new InsnNode(Opcodes.IRETURN));

	newInstructions.add(label);

	instructions.insertBefore(instructions.getFirst(), newInstructions);
}
