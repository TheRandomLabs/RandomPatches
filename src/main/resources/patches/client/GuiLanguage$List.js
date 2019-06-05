var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");

function log(message) {
	print("[RandomPatches GuiLanguage$List Transformer]: " + message);
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
		"RandomPatches GuiLanguage$List Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.client.gui.GuiLanguage$List"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(methods[i], "elementClicked", "func_148144_a", patchElementClicked)) {
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

	//Call GuiLanguageListPatch#reloadLanguage
	instructions.insert(refreshResources, new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			"com/therandomlabs/randompatches/patch/client/GuiLanguageListPatch",
			"reloadLanguage",
			"()V",
			false
	));

	var previous = refreshResources.getPrevious();

	instructions.remove(previous.getPrevious());
	instructions.remove(previous);
	instructions.remove(refreshResources);
}
