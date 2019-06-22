var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var JumpInsnNode = Java.type("org.objectweb.asm.tree.JumpInsnNode");
var LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

function log(message) {
	print("[RandomPatches NextTickListEntry Transformer]: " + message);
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
		"RandomPatches NextTickListEntry Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.world.NextTickListEntry"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(methods[i], "compareTo", patchCompareTo)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchCompareTo(instructions) {
	var newInstructions = new InsnList();

	var continueLabel = new LabelNode();

	//Get NextTickListEntryPatch (this)
	newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

	//Get other NextTicklistEntryPatch (p_compareTo_1_)
	newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));

	//Call NextTickListEntryPatch#equals
	newInstructions.add(new MethodInsnNode(
			Opcodes.INVOKEVIRTUAL,
			"net/minecraft/world/NextTickListEntry",
			"equals",
			"(Ljava/lang/Object;)Z",
			false
	));

	//If NextTickListEntryPatch#equals returns false, continue
	newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, continueLabel));

	//Load 0
	newInstructions.add(new InsnNode(Opcodes.ICONST_0));

	//Return 0
	newInstructions.add(new InsnNode(Opcodes.IRETURN));

	newInstructions.add(continueLabel);

	instructions.insertBefore(instructions.getFirst(), newInstructions);
}
