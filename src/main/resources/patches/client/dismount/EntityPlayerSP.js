var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");

var TICK = ASMAPI.mapMethod("func_70071_h_");
var SNEAK = ASMAPI.mapField("field_78899_d");

function log(message) {
	print("[RandomPatches EntityPlayerSP Transformer]: " + message);
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
		"RandomPatches EntityPlayerSP Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.client.entity.EntityPlayerSP"
			},
			"transformer": function(classNode) {
				log("Transforming class: " + classNode.name);

				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(methods[i], TICK, patchTick)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchTick(instructions) {
	var shouldSneak = null;

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(instruction.getOpcode() == Opcodes.GETFIELD) {
			if(instruction.name == SNEAK) {
				shouldSneak = instruction;
				break;
			}
		}
	}

	//Call EntityPlayerSPPPatch#shouldDismount
	instructions.insert(shouldSneak, new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			"com/therandomlabs/randompatches/patch/client/dismount/EntityPlayerSPPatch",
			"shouldDismount",
			"()Z",
			false
	));

	var getMovementInput = shouldSneak.getPrevious();

	instructions.remove(getMovementInput.getPrevious());
	instructions.remove(getMovementInput);
	instructions.remove(shouldSneak);
}
