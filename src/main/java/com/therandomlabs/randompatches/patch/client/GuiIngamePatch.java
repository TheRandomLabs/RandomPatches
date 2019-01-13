package com.therandomlabs.randompatches.patch.client;

import com.therandomlabs.randompatches.core.Patch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class GuiIngamePatch extends Patch {
	@FunctionalInterface
	public interface PortalRenderer {
		void render(float timeInPortal, ScaledResolution resolution);
	}

	private static PortalRenderer renderer = null;

	@SuppressWarnings("Duplicates")
	@Override
	public boolean apply(ClassNode node) {
		final MethodNode method = findMethod(node, "renderPortal", "func_180474_b");
		final InsnList instructions = new InsnList();

		final VarInsnNode loadTimeInPortal = new VarInsnNode(
				Opcodes.FLOAD,
				1
		);

		final VarInsnNode loadResolution = new VarInsnNode(
				Opcodes.ALOAD,
				2
		);

		final MethodInsnNode callRenderPortal = new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				getName(GuiIngamePatch.class),
				"renderPortal",
				"(FLnet/minecraft/client/gui/ScaledResolution;)V",
				false
		);

		final InsnNode returnNothing = new InsnNode(Opcodes.RETURN);

		instructions.add(loadTimeInPortal);
		instructions.add(loadResolution);
		instructions.add(callRenderPortal);
		instructions.add(returnNothing);

		method.instructions = instructions;

		return true;
	}

	public static void renderPortal(float timeInPortal, ScaledResolution resolution) {
		if(renderer != null) {
			renderer.render(timeInPortal, resolution);
			return;
		}

		final Minecraft mc = Minecraft.getMinecraft();

		if(timeInPortal < 1.0F) {
			timeInPortal *= timeInPortal * timeInPortal * 0.8F;
			timeInPortal += 0.2F;
		}

		GlStateManager.disableAlpha();
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);

		GlStateManager.tryBlendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO
		);
		GlStateManager.color(1.0F, 1.0F, 1.0F, timeInPortal);

		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		final TextureAtlasSprite sprite = mc.getBlockRendererDispatcher().getBlockModelShapes().
				getTexture(Blocks.PORTAL.getDefaultState());

		float minU = sprite.getMinU();
		float minV = sprite.getMinV();
		float maxU = sprite.getMaxU();
		float maxV = sprite.getMaxV();

		final int width = resolution.getScaledWidth();
		final int height = resolution.getScaledHeight();

		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(0.0, height, -90.0).tex(minU, maxV).endVertex();
		buffer.pos(width, height, -90.0).tex(maxU, maxV).endVertex();
		buffer.pos(width, 0.0, -90.0).tex(maxU, minV).endVertex();
		buffer.pos(0.0, 0.0, -90.0).tex(minU, minV).endVertex();

		tessellator.draw();

		GlStateManager.depthMask(true);
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void setPortalRenderer(PortalRenderer renderer) {
		GuiIngamePatch.renderer = renderer;
	}
}
