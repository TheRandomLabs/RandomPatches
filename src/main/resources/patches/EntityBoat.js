var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");
var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var JumpInsnNode = Java.type("org.objectweb.asm.tree.JumpInsnNode");
var LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

var deobfuscated;

function log(message) {
	print("[RandomPatches EntityBoat Transformer]: " + message);
}

function patch(method, name, srgName, patchFunction) {
	if(method.name != name && method.name != srgName) {
		return false;
	}

	deobfuscated = method.name == name;
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
					if(patch(methods[i], "tick", "func_70071_h_", patchTick)) {
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

	var returnLabel = new LabelNode();

	var loadThis = new VarInsnNode(
			Opcodes.ALOAD,
			0
	);

	var loadThis2 = new VarInsnNode(
			Opcodes.ALOAD,
			0
	);

	var getStatus = new FieldInsnNode(
			Opcodes.GETFIELD,
			"net/minecraft/entity/item/EntityBoat",
			deobfuscated ? "status" : "field_184469_aF",
			"Lnet/minecraft/entity/item/EntityBoat$Status;"
	);

	var onUpdate = new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			"com/therandomlabs/randompatches/patch/EntityBoatPatch",
			"onUpdate",
			"(Lnet/minecraft/entity/item/EntityBoat;" +
			"Lnet/minecraft/entity/item/EntityBoat$Status;)V",
			false
	);

	var getPreventEjection = new FieldInsnNode(
			Opcodes.GETSTATIC,
			"com/therandomlabs/randompatches/RPConfig$Boats",
			"preventUnderwaterBoatPassengerEjection",
			"Z"
	);

	var returnIfNotTrue = new JumpInsnNode(
			Opcodes.IFEQ,
			returnLabel
	);

	var loadThis3 = new VarInsnNode(
			Opcodes.ALOAD,
			0
	);

	var loadZero = new InsnNode(
			Opcodes.FCONST_0
	);

	var setOutOfControlTicks = new FieldInsnNode(
			Opcodes.PUTFIELD,
			"net/minecraft/entity/item/EntityBoat",
			deobfuscated ? "outOfControlTicks" : "field_184474_h",
			"F"
	);

	instructions.insertBefore(returnVoid, loadThis);
	instructions.insert(loadThis, loadThis2);
	instructions.insert(loadThis2, getStatus);
	instructions.insert(getStatus, onUpdate);
	instructions.insert(onUpdate, getPreventEjection);
	instructions.insert(getPreventEjection, returnIfNotTrue);
	instructions.insert(returnIfNotTrue, loadThis3);
	instructions.insert(loadThis3, loadZero);
	instructions.insert(loadZero, setOutOfControlTicks);
	instructions.insert(setOutOfControlTicks, returnLabel);
}
