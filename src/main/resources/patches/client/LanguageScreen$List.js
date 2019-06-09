var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");

var ELEMENT_CLICKED = ASMAPI.mapMethod("func_148144_a");

function log(message) {
	print("[RandomPatches LanguageScreen$List Transformer]: " + message);
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
		"RandomPatches LanguageScreen$List Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.client.gui.LanguageScreen$List"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(methods[i], ELEMENT_CLICKED, patchElementClicked)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchElementClicked(instructions) {
	var refreshResources;

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(instruction.getOpcode() == Opcodes.INVOKESTATIC &&
				instruction.name == "refreshResources") {
			refreshResources = instruction;
			break;
		}
	}

	//Call LanguageScreenListPatch#reloadLanguage
	instructions.insert(refreshResources, new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			"com/therandomlabs/randompatches/patch/client/LanguageScreenListPatch",
			"reloadLanguage",
			"()V",
			false
	));

	var previous = refreshResources.getPrevious();

	instructions.remove(previous.getPrevious());
	instructions.remove(previous);
	instructions.remove(refreshResources);
}
