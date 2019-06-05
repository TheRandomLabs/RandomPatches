var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

var SHOULD_RENDER_FACE = ASMAPI.mapMethod("func_184313_a");

function log(message) {
	print("[RandomPatches TileEntityEndPortal Transformer]: " + message);
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
		"RandomPatches TileEntityEndPortal Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.tileentity.TileEntityEndPortal"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(methods[i], SHOULD_RENDER_FACE, patchShouldRenderFace)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchShouldRenderFace(instructions) {
	var newInstructions = new InsnList();

	//Get face
	newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));

	//Call TileEntityEndPortalPatch#shouldRenderFace
	newInstructions.add(new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			"com/therandomlabs/randompatches/patch/TileEntityEndPortalPatch",
			"shouldRenderFace",
			"(Lnet/minecraft/util/EnumFacing;)Z",
			false
	));

	//Return TileEntityEndPortalPatch#shouldRenderFace
	newInstructions.add(new InsnNode(Opcodes.IRETURN));

	instructions.insertBefore(instructions.getFirst(), newInstructions);
}
