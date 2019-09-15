var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FUNC_220080_A = ASMAPI.mapMethod("func_220080_a");

function log(message) {
	print("[RandomPatches BambooBlock Transformer]: " + message);
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
		"RandomPatches BambooBlock Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.block.BambooBlock"
			},
			"transformer": function(classNode) {
				var func_220080_a = classNode.visitMethod(
						Opcodes.ACC_PUBLIC, FUNC_220080_A,
						"(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IBlockReader;" +
						"Lnet/minecraft/util/math/BlockPos;)F",
						null, null
				);

				func_220080_a.visitCode();

				//Load 1.0F
				func_220080_a.visitInsn(Opcodes.FCONST_1);

				//Return 1.0F
				func_220080_a.visitInsn(Opcodes.FRETURN);

				func_220080_a.visitEnd();

				return classNode;
			}
		}
	};
}
