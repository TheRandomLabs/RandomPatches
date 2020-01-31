var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

var INIT = ASMAPI.mapMethod("func_71384_a");
var LOAD_ICON = ASMAPI.mapMethod("func_216529_a");

var GET_TITLE = ASMAPI.mapMethod("func_230149_ax_");

var initPatched;
var getTitlePatched;

function log(message) {
	print("[RandomPatches Minecraft Transformer]: " + message);
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
		"RandomPatches Minecraft Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.client.Minecraft"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for (var i in methods) {
					if (initPatched && getTitlePatched) {
						break;
					}

					if (patch(methods[i], INIT, patchInit)) {
						initPatched = true;
						continue;
					}

					if (patch(methods[i], GET_TITLE, patchGetTitle)) {
						getTitlePatched = true;
						continue;
					}
				}

				return classNode;
			}
		}
	};
}

function patchInit(instructions) {
	var loadIcon;

	for (var i = 0; i < instructions.size(); i++) {
		var instruction = instructions.get(i);

		if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL && instruction.name == LOAD_ICON) {
			loadIcon = instruction;
			break;
		}
	}

	var icon32 = loadIcon.getPrevious();
	var icon16 = icon32.getPrevious();
	var getMainWindow = icon16.getPrevious();

	//getMainWindow.getPrevious() is ALOAD 0 (gets Minecraft (this))
	instructions.remove(getMainWindow.getPrevious());
	instructions.remove(getMainWindow);
	instructions.remove(icon16);
	instructions.remove(icon32);

	//Call WindowIconHandler#setWindowIcon
	loadIcon.opcode = Opcodes.INVOKESTATIC;
	loadIcon.owner = "com/therandomlabs/randompatches/client/WindowIconHandler";
	loadIcon.name = "setWindowIcon";
	loadIcon.desc = "()V";
}

function patchGetTitle(instructions) {
	instructions.clear();

	//Load Minecraft (this)
	instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

	//Call MinecraftHook#getTitle(Minecraft)
	instructions.add(new MethodInsnNode(
		Opcodes.INVOKESTATIC,
		"com/therandomlabs/randompatches/hook/client/MinecraftHook",
		"getTitle",
		"(Lnet/minecraft/client/Minecraft;)Ljava/lang/String;",
		false
	));

	//Return MinecraftHook#getTitle(Minecraft)
	instructions.add(new InsnNode(Opcodes.ARETURN));
}
