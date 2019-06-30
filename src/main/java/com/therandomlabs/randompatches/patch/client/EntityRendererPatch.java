package com.therandomlabs.randompatches.patch.client;

import com.therandomlabs.randompatches.core.Patch;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class EntityRendererPatch extends Patch {
	public static final class EyeHeightHandler {
		private static final Minecraft mc = Minecraft.getMinecraft();

		private static float lastEyeHeight;
		private static float eyeHeight;

		public static void updateRenderer() {
			lastEyeHeight = eyeHeight;
			eyeHeight += (mc.getRenderViewEntity().getEyeHeight() - eyeHeight) * 0.5F;
		}

		public static float getEyeHeight(float partialTicks) {
			final Entity entity = mc.getRenderViewEntity();
			final float height = lastEyeHeight + (eyeHeight - lastEyeHeight) * partialTicks;

			if(entity instanceof EntityLivingBase &&
					((EntityLivingBase) entity).isPlayerSleeping()) {
				return height + 1.0F;
			}

			return height;
		}
	}

	public static final String SET_RENDER_VIEW_ENTITY =
			getName("setRenderViewEntity", "func_175607_a");
	public static final String TRANSLATE = getName("translate", "func_179109_b");
	public static final String EYE_HEIGHT_HANDLER =
			getName(EntityRendererPatch.class) + "$EyeHeightHandler";

	@Override
	public boolean apply(ClassNode node) {
		patchUpdateRenderer(findInstructions(node, "updateRenderer", "func_78464_a"));
		patchOrientCamera(findInstructions(node, "orientCamera", "func_78467_g"));
		return true;
	}

	private static void patchUpdateRenderer(InsnList instructions) {
		AbstractInsnNode label = null;

		for(int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if(instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				final MethodInsnNode method = (MethodInsnNode) instruction;

				if(SET_RENDER_VIEW_ENTITY.equals(method.name)) {
					label = method.getNext();
					break;
				}
			}
		}

		//Call EntityRendererPatch$EyeHeightHandler#updateRenderer
		instructions.insert(label, new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				EYE_HEIGHT_HANDLER,
				"updateRenderer",
				"()V",
				false
		));
	}

	private static void patchOrientCamera(InsnList instructions) {
		VarInsnNode getEyeHeight = null;

		for(int i = instructions.size() - 1; i >= 0; i--) {
			final AbstractInsnNode instruction = instructions.get(i);

			if(instruction.getOpcode() == Opcodes.INVOKESTATIC) {
				final MethodInsnNode method = (MethodInsnNode) instruction;

				if(TRANSLATE.equals(method.name)) {
					getEyeHeight = (VarInsnNode) method.getPrevious().getPrevious().getPrevious();
					break;
				}
			}
		}

		//Get partialTicks instead
		getEyeHeight.var = 1;

		//Call EntityRendererPatch$EyeHeightHandler#getEyeHeight
		instructions.insert(getEyeHeight, new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				EYE_HEIGHT_HANDLER,
				"getEyeHeight",
				"(F)F",
				false
		));
	}
}
