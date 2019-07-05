var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var APPLY_ROTATIONS = ASMAPI.mapMethod("func_77043_a");

function log(message) {
	print("[RandomPatches RenderPlayer Transformer]: " + message);
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
		"RandomPatches RenderPlayer Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.client.renderer.entity.RenderPlayer"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(methods[i], APPLY_ROTATIONS, patchApplyRotations)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchApplyRotations(instructions) {
	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(instruction.getOpcode() == Opcodes.INVOKESTATIC && instruction.name == "acos") {
			instruction.owner = "com/therandomlabs/randompatches/patch/client/RenderPlayerPatch";
			return;
		}
	}
}
