var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

var deobfuscated;

var writeWithoutTypeIdPatched;
var readPatched;

function log(message) {
	print("[RandomPatches Entity Transformer]: " + message);
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
		"RandomPatches Entity Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.entity.Entity"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for(var i in methods) {
					var method = methods[i];

					if(writeWithoutTypeIdPatched && readPatched) {
						break;
					}

					if(patch(
							method, "writeWithoutTypeId", "func_189511_e", patchWriteWithoutTypeId
					)) {
						writeWithoutTypeIdPatched = true;
						continue;
					}

					if(patch(method, "read", "func_70020_e", patchRead)) {
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

	for(var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if(instruction.getOpcode() == Opcodes.INVOKEVIRTUAL &&
				instruction.owner == "net/minecraft/nbt/NBTTagCompound") {
			setTag = instruction;
			break;
		}
	}

	var loadThis = new VarInsnNode(Opcodes.ALOAD, 0);

	var loadCompound = new VarInsnNode(Opcodes.ALOAD, 1);

	var writeAABBTag = new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			"com/therandomlabs/randompatches/patch/EntityPatch",
			"writeAABBTag",
			"(Lnet/minecraft/entity/Entity;Lnet/minecraft/nbt/NBTTagCompound;)V",
			false
	);

	instructions.insert(setTag, loadThis);
	instructions.insert(loadThis, loadCompound);
	instructions.insert(loadCompound, writeAABBTag);
}

function patchRead(instructions) {
	var jumpIfShouldNotSetPosition;
	var setPosition;

	for(var i = instructions.size() - 1; i >= 0; i--) {
		var instruction = instructions.get(i);

		if(setPosition == null) {
			if(instruction.getOpcode() == Opcodes.INVOKEVIRTUAL &&
					(instruction.name == "setPosition" || instruction.name == "func_70107_b")) {
				setPosition = instruction;
			}

			continue;
		}

		if(instruction.getOpcode() == Opcodes.IFEQ) {
			jumpIfShouldNotSetPosition = instruction;
			break;
		}
	}

	var jumpTo = new LabelNode();

	jumpIfShouldNotSetPosition.label = jumpTo;

	var loadThis = new VarInsnNode(Opcodes.ALOAD, 0);

	var loadCompound = new VarInsnNode(Opcodes.ALOAD, 1);

	var readAABBTag = new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			"com/therandomlabs/randompatches/patch/EntityPatch",
			"readAABBTag",
			"(Lnet/minecraft/entity/Entity;Lnet/minecraft/nbt/NBTTagCompound;)V",
			false
	);

	instructions.insert(setPosition, jumpTo);
	instructions.insert(jumpTo, loadThis);
	instructions.insert(loadThis, loadCompound);
	instructions.insert(loadCompound, readAABBTag);
}
