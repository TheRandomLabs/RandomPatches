var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");
var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var JumpInsnNode = Java.type("org.objectweb.asm.tree.JumpInsnNode");
var LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

var TICK = ASMAPI.mapMethod("func_70071_h_");
var STATUS = ASMAPI.mapField("field_184469_aF");
var OUT_OF_CONTROL_TICKS = ASMAPI.mapField("field_184474_h");

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

	var returnLabel = new LabelNode();

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

	//Get RPConfig.Boats#preventUnderwaterBoatPassengerEjection
	newInstructions.add(new FieldInsnNode(
			Opcodes.GETSTATIC,
			"com/therandomlabs/randompatches/RPConfig$Boats",
			"preventUnderwaterBoatPassengerEjection",
			"Z"
	));

	//Return if false
	newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, returnLabel));

	//Get EntityBoat (this)
	newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

	//Load 0.0F
	newInstructions.add(new InsnNode(Opcodes.FCONST_0));

	//Set EntityBoat#outOfControlTicks to 0.0F
	newInstructions.add(new FieldInsnNode(
			Opcodes.PUTFIELD,
			"net/minecraft/entity/item/EntityBoat",
			OUT_OF_CONTROL_TICKS,
			"F"
	));

	newInstructions.add(returnLabel);

	instructions.insertBefore(returnVoid, newInstructions);
}
