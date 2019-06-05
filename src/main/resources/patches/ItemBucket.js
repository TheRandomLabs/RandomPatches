var Opcodes = Java.type("org.objectweb.asm.Opcodes");

function log(message) {
	print("[RandomPatches ItemBucket Transformer]: " + message);
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
		"RandomPatches ItemBucket Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.item.ItemBucket"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(
							methods[i], "tryPlaceContainedLiquid", "func_180616_a",
							patchTryPlaceContainedLiquid
					)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchTryPlaceContainedLiquid(instructions) {
	var isSolid;

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(instruction.getOpcode() == Opcodes.INVOKEVIRTUAL &&
				(instruction.name == "isSolid" || instruction.name == "func_76220_a")) {
			isSolid = instruction;
			break;
		}
	}

	//Get IBlockState
	isSolid.getPrevious().var = 5;

	//Call ItemBucketPatch#isSolid
	isSolid.setOpcode(Opcodes.INVOKESTATIC);
	isSolid.owner = "com/therandomlabs/randompatches/patch/ItemBucketPatch";
	isSolid.name = "isSolid";
	isSolid.desc = "(Lnet/minecraft/block/state/IBlockState;)Z";
}
