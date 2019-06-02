package com.therandomlabs.randompatches.patch;

import java.util.Objects;
import com.therandomlabs.randompatches.core.Patch;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
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

		//Spawn particle
		instructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				getName(ServerWorldEventHandlerPatch.class),
				"spawnParticle",
				"(Lnet/minecraft/world/WorldServer;IZDDDDDD[I)V",
				false
		));

		//Return
		instructions.add(new InsnNode(Opcodes.RETURN));

		//TODO patch other spawnParticle?

		return true;
	}

	public static void spawnParticle(WorldServer world, int particleID, boolean ignoreRange,
			double x, double y, double z, double xSpeed, double ySpeed, double zSpeed,
			int... parameters) {
		final EnumParticleTypes particleType =
				Objects.requireNonNull(EnumParticleTypes.getParticleFromId(particleID));

		//In SPacketParticles, there are always as many ints written to the PacketBuffer as there
		//are arguments defined for that particle type, however, there is no check in vanilla to
		//ensure there are as many parameters as required, leading to an
		//ArrayIndexOutOfBoundsException.
		//This exception would also occur in vanilla when calling EntityLivingBase#updateItemUse
		//on the server when there are no subtypes for an item if not for MC-10369, which this
		//patch fixes.
		if(parameters.length == particleType.getArgumentCount()) {
			world.spawnParticle(
					particleType,
					x, y, z,
					1,
					0.0, 0.0, 0.0,
					(xSpeed * ySpeed * zSpeed) / 3.0,
					parameters
			);
		}
	}
}
