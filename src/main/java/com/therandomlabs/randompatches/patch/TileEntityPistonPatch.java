package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.core.Patch;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

//Fix taken from
//https://github.com/gnembon/carpetmod112/blob/staging/patches/net/minecraft/tileentity/
//TileEntityPiston.java.patch
//Thanks, gnembon!
public final class TileEntityPistonPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "update", "func_73660_a");
		AbstractInsnNode jumpIfNotPistonExtension = null;

		for (int i = 0; i < instructions.size(); i++) {
			jumpIfNotPistonExtension = instructions.get(i);

			if (jumpIfNotPistonExtension.getOpcode() == Opcodes.IF_ACMPNE) {
				break;
			}

			jumpIfNotPistonExtension = null;
		}

		final InsnList newInstructions = new InsnList();

		//Get TileEntityPiston (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get TileEntity#world
		newInstructions.add(new FieldInsnNode(
				Opcodes.GETFIELD,
				"net/minecraft/tileentity/TileEntity",
				getName("world", "field_145850_b"),
				"Lnet/minecraft/world/World;"
		));

		//Get TileEntityPiston (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get TileEntity#pos
		newInstructions.add(new FieldInsnNode(
				Opcodes.GETFIELD,
				"net/minecraft/tileentity/TileEntity",
				getName("pos", "field_174879_c"),
				"Lnet/minecraft/util/math/BlockPos;"
		));

		//Call TileEntityPistonPatch#updatePistonExtension
		newInstructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				getName(TileEntityPistonPatch.class),
				"updatePistonExtension",
				"(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V",
				false
		));

		instructions.insert(jumpIfNotPistonExtension, newInstructions);

		return true;
	}

	public static void updatePistonExtension(World world, BlockPos pos) {
		final IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(
				pos.offset(state.getValue(BlockDirectional.FACING).getOpposite()), state, state, 0
		);
	}
}
