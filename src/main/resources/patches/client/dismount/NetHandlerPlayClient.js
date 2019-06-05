var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");

function log(message) {
	print("[RandomPatches NetHandlerPlayClient Transformer]: " + message);
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
		"RandomPatches NetHandlerPlayClient Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.client.network.NetHandlerPlayClient"
			},
			"transformer": function(classNode) {
				log("Transforming class: " + classNode.name);

				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(methods[i], "handleSetPassengers", "func_184328_a", patchHandleSetPassengers)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchHandleSetPassengers(instructions) {
	var getSneakKeybind = null;

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(instruction.getOpcode() == Opcodes.GETFIELD &&
				(instruction.name == "keyBindSneak" || instruction.name == "field_74311_E")) {
			getSneakKeybind = instruction;
			break;
		}
	}

	//Get EntityPlayerSPPatch.DismountKeybind#keybind
	//We do this so the dismount key is shown instead of the sneak key in
	//"Press <key> to dismount"
	instructions.insert(getSneakKeybind, new FieldInsnNode(
			Opcodes.GETSTATIC,
			"com/therandomlabs/randompatches/patch/client/dismount/" +
					"EntityPlayerSPPatch$DismountKeybind",
			"keybind",
			"Lnet/minecraft/client/settings/KeyBinding;"
	));

	var getGameSettings = getSneakKeybind.getPrevious();
	var getMinecraft = getGameSettings.getPrevious();

	instructions.remove(getMinecraft.getPrevious());
	instructions.remove(getMinecraft);
	instructions.remove(getGameSettings);
	instructions.remove(getSneakKeybind);
}
