var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var TRY_PLACE_CONTAINED_LIQUID = ASMAPI.mapMethod("func_180616_a");
var IS_SOLID = ASMAPI.mapMethod("func_76220_a");

function log(message) {
	print("[RandomPatches ItemBucket Transformer]: " + message);
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
		"RandomPatches ItemBucket Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.item.ItemBucket"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(
							methods[i], TRY_PLACE_CONTAINED_LIQUID, patchTryPlaceContainedLiquid
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

		if(instruction.getOpcode() == Opcodes.INVOKEVIRTUAL && instruction.name == IS_SOLID) {
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
