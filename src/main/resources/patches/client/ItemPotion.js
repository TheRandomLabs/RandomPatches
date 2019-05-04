var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

function log(message) {
	print("[RandomPatches ItemPotion Transformer]: " + message);
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
		"RandomPatches ItemPotion Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.item.ItemPotion"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(methods[i], "hasEffect", "func_77962_s", patchHasEffect)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchHasEffect(instructions) {
	instructions.clear();

	instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
	instructions.add(new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			"com/therandomlabs/randompatches/patch/client/ItemPotionPatch",
			"hasEffect",
			"(Lnet/minecraft/item/ItemStack;)Z",
			false
	));
	instructions.add(new InsnNode(Opcodes.IRETURN));
}
