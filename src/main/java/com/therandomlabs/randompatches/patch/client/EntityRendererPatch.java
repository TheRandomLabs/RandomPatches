package com.therandomlabs.randompatches.patch.client;

import java.lang.reflect.Field;
import com.therandomlabs.randomlib.TRLUtils;
import com.therandomlabs.randompatches.config.RPConfig;
import com.therandomlabs.randompatches.core.Patch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class EntityRendererPatch extends Patch {
	public static final class EyeHeightHandler {
		private static final Minecraft mc = Minecraft.getMinecraft();
		private static final Field EYE_HEIGHT_HANDLER =
				TRLUtils.findField(EntityRenderer.class, "eyeHeightHandler");

		private float lastEyeHeight;
		private float eyeHeight;

		public static void updateRenderer(EntityRenderer renderer, EyeHeightHandler handler) {
			handler = get(renderer, handler);
			handler.lastEyeHeight = handler.eyeHeight;
			handler.eyeHeight +=
					(mc.getRenderViewEntity().getEyeHeight() - handler.eyeHeight) * 0.5F;
		}

		public static float getEyeHeight(
				float partialTicks, EntityRenderer renderer, EyeHeightHandler handler
		) {
			final Entity entity = mc.getRenderViewEntity();

			if(!RPConfig.Client.smoothEyeLevelChanges) {
				return entity.getEyeHeight();
			}

			handler = get(renderer, handler);
			final float height = handler.lastEyeHeight +
					(handler.eyeHeight - handler.lastEyeHeight) * partialTicks;

			if(entity instanceof EntityLivingBase &&
					((EntityLivingBase) entity).isPlayerSleeping()) {
				return height + 1.0F;
			}

			return height;
		}

		public static EyeHeightHandler get(EntityRenderer renderer, EyeHeightHandler handler) {
			if(handler == null) {
				handler = new EyeHeightHandler();

				try {
					EYE_HEIGHT_HANDLER.set(renderer, handler);
				} catch(IllegalAccessException ex) {
					TRLUtils.crashReport("Failed to set EntityRenderer#eyeHeightHandler", ex);
				}
			}

			return handler;
		}
	}

	public static final String SET_RENDER_VIEW_ENTITY =
			getName("setRenderViewEntity", "func_175607_a");
	public static final String TRANSLATE = getName("translate", "func_179109_b");
	public static final String EYE_HEIGHT_HANDLER =
			getName(EntityRendererPatch.class) + "$EyeHeightHandler";

	@Override
	public boolean apply(ClassNode node) {
		node.fields.add(new FieldNode(
				Opcodes.ACC_PUBLIC, "eyeHeightHandler", "L" + EYE_HEIGHT_HANDLER + ";", null, null
		));
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

		final InsnList newInstructions = new InsnList();

		//Get EntityRenderer (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get EntityRenderer (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get EntityRenderer#eyeHeightHandler
		newInstructions.add(new FieldInsnNode(
				Opcodes.GETFIELD,
				"net/minecraft/client/renderer/EntityRenderer",
				"eyeHeightHandler",
				"L" + EYE_HEIGHT_HANDLER + ";"
		));

		//Call EntityRendererPatch$EyeHeightHandler#updateRenderer
		newInstructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				EYE_HEIGHT_HANDLER,
				"updateRenderer",
				"(Lnet/minecraft/client/renderer/EntityRenderer;L" + EYE_HEIGHT_HANDLER + ";)V",
				false
		));

		instructions.insert(label, newInstructions);
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

		final InsnList newInstructions = new InsnList();

		//Get EntityRenderer (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get EntityRenderer (this)
		newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

		//Get EntityRenderer#eyeHeightHandler
		newInstructions.add(new FieldInsnNode(
				Opcodes.GETFIELD,
				"net/minecraft/client/renderer/EntityRenderer",
				"eyeHeightHandler",
				"L" + EYE_HEIGHT_HANDLER + ";"
		));

		//Call EntityRendererPatch$EyeHeightHandler#getEyeHeight
		newInstructions.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				EYE_HEIGHT_HANDLER,
				"getEyeHeight",
				"(FLnet/minecraft/client/renderer/EntityRenderer;L" + EYE_HEIGHT_HANDLER + ";)F",
				false
		));

		instructions.insert(getEyeHeight, newInstructions);
	}
}
