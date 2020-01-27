var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

var HAS_EFFECT = ASMAPI.mapMethod("func_77962_s");

function log(message) {
	print("[RandomPatches PotionItem Transformer]: " + message);
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
		"RandomPatches PotionItem Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.item.PotionItem"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for (var i in methods) {
					if (patch(methods[i], HAS_EFFECT, patchHasEffect)) {
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

	//Get ItemStack
	instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));

	//Call PotionItemHook#hasEffect
	instructions.add(new MethodInsnNode(
		Opcodes.INVOKESTATIC,
		"com/therandomlabs/randompatches/hook/client/PotionItemPatch",
		"hasEffect",
		"(Lnet/minecraft/item/ItemStack;)Z",
		false
	));

	//Return PotionItemHook#hasEffect
	instructions.add(new InsnNode(Opcodes.IRETURN));
}
