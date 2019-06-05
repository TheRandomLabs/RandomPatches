var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");

var READ_COMPOUND_TAG = ASMAPI.mapMethod("func_150793_b");

function log(message) {
	print("[RandomPatches PacketBuffer Transformer]: " + message);
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
		"RandomPatches PacketBuffer Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.network.PacketBuffer"
			},
			"transformer": function(classNode) {
				log("Transforming class: " + classNode.name);

				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(methods[i], READ_COMPOUND_TAG, patchReadCompoundTag)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchReadCompoundTag(instructions) {
	var limit = null;

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(instruction.getOpcode() == Opcodes.LDC) {
			limit = instruction;
			break;
		}
	}

	//Get RPConfig.Misc#packetSizeLimit
	instructions.insert(limit, new FieldInsnNode(
			Opcodes.GETSTATIC,
			"com/therandomlabs/randompatches/RPConfig$Misc",
			"packetSizeLimitLong",
			"J"
	));

	instructions.remove(limit);
}
