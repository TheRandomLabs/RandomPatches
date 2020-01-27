var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
var LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

var WRITE_WITHOUT_TYPE_ID = ASMAPI.mapMethod("func_189511_e");
var READ = ASMAPI.mapMethod("func_70020_e");
var SET_POSITION = ASMAPI.mapMethod("func_70107_b");

var writeWithoutTypeIdPatched;
var readPatched;

function log(message) {
	print("[RandomPatches Entity Transformer]: " + message);
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
		"RandomPatches Entity Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.entity.Entity"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for (var i in methods) {
					var method = methods[i];

					if (writeWithoutTypeIdPatched && readPatched) {
						break;
					}

					if (patch(method, WRITE_WITHOUT_TYPE_ID, patchWriteWithoutTypeId)) {
						writeWithoutTypeIdPatched = true;
						continue;
					}

					if (patch(method, READ, patchRead)) {
						readPatched = true;
					}
				}

				return classNode;
			}
		}
	};
}

function patchWriteWithoutTypeId(instructions) {
	var setTag;

	for (var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL &&
			instruction.owner == "net/minecraft/nbt/CompoundNBT") {
			setTag = instruction;
			break;
		}
	}

	var newInstructions = new InsnList();

	//Get Entity (this)
	newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

	//Get CompoundNBT
	newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));

	//Call EntityHook#writeAABBTag
	newInstructions.add(new MethodInsnNode(
		Opcodes.INVOKESTATIC,
		"com/therandomlabs/randompatches/hook/EntityHook",
		"writeAABBTag",
		"(Lnet/minecraft/entity/Entity;Lnet/minecraft/nbt/CompoundNBT;)V",
		false
	));

	instructions.insert(setTag, newInstructions);
}

function patchRead(instructions) {
	var jumpIfShouldNotSetPosition;
	var setPosition;

	for (var i = instructions.size() - 1; i >= 0; i--) {
		var instruction = instructions.get(i);

		if (setPosition == null) {
			if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL &&
				instruction.name == SET_POSITION) {
				setPosition = instruction;
			}

			continue;
		}

		if (instruction.getOpcode() == Opcodes.IFEQ) {
			jumpIfShouldNotSetPosition = instruction;
			break;
		}
	}

	var newInstructions = new InsnList();

	var jumpTo = new LabelNode();

	jumpIfShouldNotSetPosition.label = jumpTo;

	newInstructions.add(jumpTo);

	//Get Entity (this)
	newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

	//Get CompoundNBT
	newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));

	//Call EntityHook#readAABBTag
	newInstructions.add(new MethodInsnNode(
		Opcodes.INVOKESTATIC,
		"com/therandomlabs/randompatches/hook/EntityHook",
		"readAABBTag",
		"(Lnet/minecraft/entity/Entity;Lnet/minecraft/nbt/CompoundNBT;)V",
		false
	));

	instructions.insert(setPosition, newInstructions);
}
