var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

var DISMOUNT_ENTITY = ASMAPI.mapMethod("func_110145_l");

function log(message) {
	print("[RandomPatches LivingEntity Transformer]: " + message);
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
		"RandomPatches LivingEntity Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.entity.LivingEntity"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for (var i in methods) {
					if (patch(methods[i], DISMOUNT_ENTITY, patchDismountEntity)) {
						methods[i].localVariables.clear();
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchDismountEntity(instructions) {
	instructions.clear();

	instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
	instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
	instructions.add(new MethodInsnNode(
		Opcodes.INVOKESTATIC,
		"com/therandomlabs/randompatches/hook/LivingEntityHook",
		"dismountEntity",
		"(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/Entity;)V",
		false
	));
	instructions.add(new InsnNode(Opcodes.RETURN));
}
