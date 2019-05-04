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
	var labelReturnTrue = new LabelNode();

	var loadFacing = new VarInsnNode(Opcodes.ALOAD, 1);

	var getUp = new FieldInsnNode(
			Opcodes.GETSTATIC,
			"net/minecraft/util/EnumFacing",
			"UP",
			"Lnet/minecraft/util/EnumFacing;"
	);

	var returnTrueIfEqual = new JumpInsnNode(
			Opcodes.IF_ACMPEQ,
			labelReturnTrue
	);

	var loadFacing2 = new VarInsnNode(Opcodes.ALOAD, 1);

	var getDown = new FieldInsnNode(
			Opcodes.GETSTATIC,
			"net/minecraft/util/EnumFacing",
			"DOWN",
			"Lnet/minecraft/util/EnumFacing;"
	);

	var returnTrueIfEqual2 = new JumpInsnNode(
			Opcodes.IF_ACMPEQ,
			labelReturnTrue
	);

	var loadZero = new InsnNode(Opcodes.ICONST_0);

	var returnFalse = new InsnNode(Opcodes.IRETURN);

	var loadOne = new InsnNode(Opcodes.ICONST_1);

	var returnTrue = new InsnNode(Opcodes.IRETURN);

	instructions.clear();

	instructions.add(loadFacing);
	instructions.add(getUp);
	instructions.add(returnTrueIfEqual);
	instructions.add(loadFacing2);
	instructions.add(getDown);
	instructions.add(returnTrueIfEqual2);
	instructions.add(loadZero);
	instructions.add(returnFalse);
	instructions.add(labelReturnTrue);
	instructions.add(loadOne);
	instructions.add(returnTrue);
}
