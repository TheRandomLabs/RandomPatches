var Opcodes = Java.type("org.objectweb.asm.Opcodes");

function log(message) {
	print("[RandomPatches NBTTagCompound Transformer]: " + message);
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
		"RandomPatches NBTTagCompound Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.nbt.NBTTagCompound"
			},
			"transformer": function(classNode) {
				log("Transforming class: " + classNode.name);

				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(methods[i], "equals", "equals", patchEquals)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchEquals(instructions) {
	var equals;

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(instruction.getOpcode() == Opcodes.INVOKESTATIC && instruction.name == "equals") {
			equals = instruction;
		}
	}

	equals.owner = "com/therandomlabs/randompatches/patch/NBTTagCompoundPatch";
	equals.name = "areTagMapsEqual";
	equals.desc = "(Ljava/util/Map;Ljava/util/Map;)Z";
	equals.itf = false;
}
