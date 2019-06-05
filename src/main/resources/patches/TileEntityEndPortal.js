var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");
var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var JumpInsnNode = Java.type("org.objectweb.asm.tree.JumpInsnNode");
var LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

function log(message) {
	print("[RandomPatches TileEntityEndPortal Transformer]: " + message);
}

function patch(method, name, srgName, patchFunction) {
	if(method.name != name && method.name != srgName) {
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
					if(patch(
							methods[i], "shouldRenderFace", "func_184313_a ", patchShouldRenderFace
					)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchShouldRenderFace(instructions) {
	instructions.clear();

	//Get face
	instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));

	//Call TileEntityEndPortalPatch#shouldRenderFace
	instructions.add(new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			"com.therandomlabs.randompatches.patch.TileEntityEndPortalPatch",
			"shouldRenderFace",
			"(Lnet/minecraft/util/EnumFacing;)Z",
			false
	));

	//Return TileEntityEndPortalPatch#shouldRenderFace
	instructions.add(new InsnNode(Opcodes.IRETURN));
}
