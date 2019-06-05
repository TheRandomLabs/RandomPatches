var Opcodes = Java.type("org.objectweb.asm.Opcodes");

function log(message) {
	print("[RandomPatches ServerRecipePlacer Transformer]: " + message);
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
		"RandomPatches ServerRecipePlacer Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.item.crafting.ServerRecipePlacer"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(
							methods[i], "consumeIngredient", "func_194325_a",
							patchConsumeIngredient
					)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchConsumeIngredient(instructions) {
	var findSlotMatchingUnusedItem;

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
			findSlotMatchingUnusedItem = instruction;
			break;
		}
	}

	//Call ServerRecipeBookHelper#findSlotMatchingUnusedItem
	findSlotMatchingUnusedItem.setOpcode(Opcodes.INVOKESTATIC);
	findSlotMatchingUnusedItem.owner =
			"com/therandomlabs/randompatches/patch/ServerRecipePlacerPatch";
	findSlotMatchingUnusedItem.name = "findSlotMatchingUnusedItem";
	findSlotMatchingUnusedItem.desc =
			"(Lnet/minecraft/entity/player/InventoryPlayer;Lnet/minecraft/item/ItemStack;)I";
}
