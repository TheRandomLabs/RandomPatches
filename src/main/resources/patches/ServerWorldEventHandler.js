var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");
var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

var ADD_PARTICLE = ASMAPI.mapMethod("func_195461_a");
var WORLD = ASMAPI.mapField("field_72782_b");

function log(message) {
	print("[RandomPatches ServerWorldEventHandler Transformer]: " + message);
}

function patch(method, name, desc, patchFunction) {
	if(method.name != name || method.desc != desc) {
		return false;
	}

	log("Patching method: " + name + " (" + method.name + ")");
	patchFunction(method.instructions);
	return true;
}

function initializeCoreMod() {
	return {
		"RandomPatches ServerWorldEventHandler Transformer": {
			"target": {
				"type": "CLASS",
				"name": "net.minecraft.world.ServerWorldEventHandler"
			},
			"transformer": function(classNode) {
				var methods = classNode.methods;

				for(var i in methods) {
					if(patch(
							methods[i], ADD_PARTICLE,
							"(Lnet/minecraft/particles/IParticleData;ZDDDDDD)V", patchAddParticle
					)) {
						break;
					}
				}

				return classNode;
			}
		}
	};
}

function patchAddParticle(instructions) {
	instructions.clear();

	//Get WorldServerEventHandler (this)
	instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

	//Get WorldServer
	instructions.add(new FieldInsnNode(
			Opcodes.GETFIELD,
			"net/minecraft/world/ServerWorldEventHandler",
			WORLD,
			"Lnet/minecraft/world/WorldServer;"
	));

	//Get particleData
	instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));

	//Get alwaysRender
	instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));

	//Get x
	instructions.add(new VarInsnNode(Opcodes.DLOAD, 3));

	//Get y
	instructions.add(new VarInsnNode(Opcodes.DLOAD, 5));

	//Get z
	instructions.add(new VarInsnNode(Opcodes.DLOAD, 7));

	//Get xSpeed
	instructions.add(new VarInsnNode(Opcodes.DLOAD, 9));

	//Get ySpeed
	instructions.add(new VarInsnNode(Opcodes.DLOAD, 11));

	//Get zSpeed
	instructions.add(new VarInsnNode(Opcodes.DLOAD, 13));

	//Spawn particle
	instructions.add(new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			"com/therandomlabs/randompatches/patch/ServerWorldEventHandlerPatch",
			"spawnParticle",
			"(Lnet/minecraft/world/WorldServer;Lnet/minecraft/particles/IParticleData;ZDDDDDD)V",
			false
	));

	//Return
	instructions.add(new InsnNode(Opcodes.RETURN));
}
