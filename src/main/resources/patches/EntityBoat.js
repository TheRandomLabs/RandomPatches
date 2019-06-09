var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");
var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

var TICK = ASMAPI.mapMethod("func_70071_h_");
var STATUS = ASMAPI.mapField("field_184469_aF");

function log(message) {
	print("[RandomPatches EntityBoat Transformer]: " + message);
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
		"RandomPatches EntityBoat Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.entity.item.EntityBoat"
			},
			"transformer": function(classNode) {
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
	var returnVoid;

	for(var i = instructions.size() - 1; i >= 0; i--) {
		var instruction = instructions.get(i);

		if(instruction.getOpcode() == Opcodes.RETURN) {
			returnVoid = instruction;
			break;
		}
	}

	var newInstructions = new InsnList();

	//Get EntityBoat (this)
	newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

	//Get EntityBoat (this)
	newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

	//Get EntityBoat#status
	newInstructions.add(new FieldInsnNode(
			Opcodes.GETFIELD,
			"net/minecraft/entity/item/EntityBoat",
			STATUS,
			"Lnet/minecraft/entity/item/EntityBoat$Status;"
	));

	//Call EntityBoatPatch#tick
	newInstructions.add(new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			"com/therandomlabs/randompatches/patch/EntityBoatPatch",
			"tick",
			"(Lnet/minecraft/entity/item/EntityBoat;" +
			"Lnet/minecraft/entity/item/EntityBoat$Status;)V",
			false
	));

	instructions.insertBefore(returnVoid, newInstructions);
}
