package com.therandomlabs.randompatches.patch.client;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class EntityRendererPatch extends Patch {
	public static final String SET_RENDER_VIEW_ENTITY =
			getName("setRenderViewEntity", "func_175607_a");
	public static final String ORIENT_CAMERA = getName("orientCamera", "func_78467_g");

	@Override
	public boolean apply(ClassNode node) {
		node.fields.add(new FieldNode(
				Opcodes.ACC_PUBLIC, "entityRendererHook", "L" + hookClass + ";", null, null
		));

		patchUpdateRenderer(findInstructions(node, "updateRenderer", "func_78464_a"));

		//We don't patch EntityRenderer#orientCamera directly because it is overwritten by
		//Valkyrien Skies
		patchSetupCameraTransform(findInstructions(node, "setupCameraTransform", "func_78479_a"));

		return true;
	}

	@Override
	public boolean computeFrames() {
		return true;
	}

	private void patchUpdateRenderer(InsnList instructions) {
		AbstractInsnNode label = null;

		for (int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				final MethodInsnNode method = (MethodInsnNode) instruction;

				if (SET_RENDER_VIEW_ENTITY.equals(method.name)) {
					label = method.getNext();
					break;
				}
			}
		}

		final InsnList newInstructions = new InsnList();

		//Get EntityRenderer (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get EntityRenderer (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get EntityRenderer#entityRendererHook
		newInstructions.add(new FieldInsnNode(
				Opcodes.GETFIELD,
				"net/minecraft/client/renderer/EntityRenderer",
				"entityRendererHook",
				"L" + hookClass + ";"
		));

		//Call EntityRendererHook#updateRenderer
		newInstructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				hookClass,
				"updateRenderer",
				"(Lnet/minecraft/client/renderer/EntityRenderer;L" + hookClass + ";)V",
				false
		));

		instructions.insert(label, newInstructions);
	}

	private void patchSetupCameraTransform(InsnList instructions) {
		AbstractInsnNode orientCamera = null;

		for (int i = instructions.size() - 1; i >= 0; i--) {
			orientCamera = instructions.get(i);

			//Valkyrien Skies changes EntityRenderer#orientCamera to be public, which means it is
			//called using INVOKEVIRTUAL rather than INVOKESPECIAL
			if (orientCamera.getOpcode() == Opcodes.INVOKESPECIAL ||
					orientCamera.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				final MethodInsnNode method = (MethodInsnNode) orientCamera;

				if (ORIENT_CAMERA.equals(method.name)) {
					break;
				}
			}

			orientCamera = null;
		}

		final InsnList newInstructions = new InsnList();

		//Get EntityRenderer (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get partialTicks
		newInstructions.add(new VarInsnNode(Opcodes.FLOAD, 1));

		//Get EntityRenderer (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get EntityRenderer (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get EntityRenderer#entityRendererHook
		newInstructions.add(new FieldInsnNode(
				Opcodes.GETFIELD,
				"net/minecraft/client/renderer/EntityRenderer",
				"entityRendererHook",
				"L" + hookClass + ";"
		));

		//Call EntityRendererHook#orientCamera
		newInstructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				hookClass,
				"orientCamera",
				"(FLnet/minecraft/client/renderer/EntityRenderer;L" + hookClass + ";)V",
				false
		));

		instructions.insert(orientCamera, newInstructions);
	}
}
