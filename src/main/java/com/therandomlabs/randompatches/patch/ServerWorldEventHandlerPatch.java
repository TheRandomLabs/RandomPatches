package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

//Taken and adapted from https://github.com/Fuzss/particlefixes
public final class ServerWorldEventHandlerPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		//No need to patch the other spawnParticle method since it is only called by
		//World#spawnAlwaysVisibleParticle which is only used by the area effect cloud particles,
		//which are called client-side.
		final MethodNode method =
				findMethod(node, "spawnParticle", "func_180442_a", "(IZDDDDDD[I)V");

		final InsnList instructions = method.instructions;

		instructions.clear();

		//Get WorldServerEventHandler (this)
		instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get WorldServer
		instructions.add(new FieldInsnNode(
				Opcodes.GETFIELD,
				"net/minecraft/world/ServerWorldEventHandler",
				getName("world", "field_72782_b"),
				"Lnet/minecraft/world/WorldServer;"
		));

		//Get particleID
		instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));

		//Get ignoreRange
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

		//Get parameters
		instructions.add(new VarInsnNode(Opcodes.ALOAD, 15));

		//Call ServerWorldEventHandlerHook#spawnParticle
		instructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				hookClass,
				"spawnParticle",
				"(Lnet/minecraft/world/WorldServer;IZDDDDDD[I)V",
				false
		));

		//Return
		instructions.add(new InsnNode(Opcodes.RETURN));

		return true;
	}
}
