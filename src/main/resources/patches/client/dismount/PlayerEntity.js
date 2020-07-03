var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var UPDATE_RIDDEN = ASMAPI.mapMethod("func_70098_U");

function log(message) {
	print("[RandomPatches PlayerEntity Transformer]: " + message);
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
		"RandomPatches PlayerEntity Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.entity.player.PlayerEntity"
			},
			"transformer": function(classNode) {
				log("Transforming class: " + classNode.name);

				var methods = classNode.methods;

				for (var i in methods) {
					if (patch(methods[i], UPDATE_RIDDEN, patchUpdateRidden)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchUpdateRidden(instructions) {
	var wantsToStopRiding = null;

	for (var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
			wantsToStopRiding = instruction;
			break;
		}
	}

	wantsToStopRiding.setOpcode(Opcodes.INVOKESTATIC);
	wantsToStopRiding.owner =
		"com/therandomlabs/randompatches/hook/client/dismount/PlayerEntityHook";
	wantsToStopRiding.name = "wantsToStopRiding";
	wantsToStopRiding.desc = "(Lnet/minecraft/entity/player/PlayerEntity;)Z";
}
