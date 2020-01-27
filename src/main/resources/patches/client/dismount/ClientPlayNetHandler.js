var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");

var HANDLE_SET_PASSENGERS = ASMAPI.mapMethod("func_184328_a");
var KEY_BIND_SNEAK = ASMAPI.mapField("field_74311_E");

function log(message) {
	print("[RandomPatches ClientPlayNetHandler Transformer]: " + message);
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
		"RandomPatches ClientPlayNetHandler Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.client.network.play.ClientPlayNetHandler"
			},
			"transformer": function(classNode) {
				log("Transforming class: " + classNode.name);

				var methods = classNode.methods;

				for (var i in methods) {
					if (patch(methods[i], HANDLE_SET_PASSENGERS, patchHandleSetPassengers)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchHandleSetPassengers(instructions) {
	var getSneakKeybind = null;

	for (var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if (instruction.getOpcode() == Opcodes.GETFIELD && instruction.name == KEY_BIND_SNEAK) {
			getSneakKeybind = instruction;
			break;
		}
	}

	//Get ClientPlayerEntityHook.DismountKeybind#keybind
	//We do this so the dismount key is shown instead of the sneak key in
	//"Press <key> to dismount"
	instructions.insert(getSneakKeybind, new FieldInsnNode(
		Opcodes.GETSTATIC,
		"com/therandomlabs/randompatches/hook/client/dismount/" +
		"ClientPlayerEntityHook$DismountKeybind",
		"keybind",
		"Lnet/minecraft/client/settings/KeyBinding;"
	));

	var getGameSettings = getSneakKeybind.getPrevious();
	var getMinecraft = getGameSettings.getPrevious();

	instructions.remove(getMinecraft.getPrevious());
	instructions.remove(getMinecraft);
	instructions.remove(getGameSettings);
	instructions.remove(getSneakKeybind);
}
