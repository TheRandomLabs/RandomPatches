package com.therandomlabs.randompatches.client;

import java.nio.FloatBuffer;
import java.util.Random;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.EndPortalTileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.EndPortalTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public class RPEndPortalTileEntityRenderer extends EndPortalTileEntityRenderer {
	private static final ResourceLocation END_SKY_TEXTURE =
			new ResourceLocation("textures/environment/end_sky.png");
	private static final ResourceLocation END_PORTAL_TEXTURE =
			new ResourceLocation("textures/entity/end_portal.png");

	private static final Random RANDOM = new Random(31100L);

	private static final FloatBuffer MODEL_VIEW = GLAllocation.createDirectFloatBuffer(16);
	private static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);

	private static final Minecraft mc = Minecraft.getInstance();

	private final FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
	private final boolean upsideDown;

	public RPEndPortalTileEntityRenderer() {
		this(false);
	}

	public RPEndPortalTileEntityRenderer(boolean upsideDown) {
		this.upsideDown = upsideDown;
	}

	@Override
	public void render(
			EndPortalTileEntity tileEntity, double x, double y, double z, float partialTicks,
			int destroyStage
	) {
		GlStateManager.disableLighting();

		RANDOM.setSeed(31100L);

		GlStateManager.getMatrix(2982, MODEL_VIEW);
		GlStateManager.getMatrix(2983, PROJECTION);

		final int passes = getPasses(x * x + y * y + z * z);
		final float offset = getOffset();
		boolean flag = false;

		for (int j = 0; j < passes; j++) {
			GlStateManager.pushMatrix();
			float f1 = 2.0F / (18 - j);

			if (j == 0) {
				bindTexture(END_SKY_TEXTURE);
				f1 = 0.15F;
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(
						GlStateManager.SourceFactor.SRC_ALPHA,
						GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
				);
			}

			if (j >= 1) {
				bindTexture(END_PORTAL_TEXTURE);
				flag = true;
				mc.gameRenderer.setupFogColor(true);
			}

			if (j == 1) {
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(
						GlStateManager.SourceFactor.ONE,
						GlStateManager.DestFactor.ONE
				);
			}

			GlStateManager.texGenMode(GlStateManager.TexGen.S, 9216);
			GlStateManager.texGenMode(GlStateManager.TexGen.T, 9216);
			GlStateManager.texGenMode(GlStateManager.TexGen.R, 9216);

			GlStateManager.texGenParam(
					GlStateManager.TexGen.S, 9474, getBuffer(1.0F, 0.0F, 0.0F, 0.0F)
			);
			GlStateManager.texGenParam(
					GlStateManager.TexGen.T, 9474, getBuffer(0.0F, 1.0F, 0.0F, 0.0F)
			);
			GlStateManager.texGenParam(
					GlStateManager.TexGen.R, 9474, getBuffer(0.0F, 0.0F, 1.0F, 0.0F)
			);

			GlStateManager.enableTexGen(GlStateManager.TexGen.S);
			GlStateManager.enableTexGen(GlStateManager.TexGen.T);
			GlStateManager.enableTexGen(GlStateManager.TexGen.R);

			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();

			GlStateManager.translatef(0.5F, 0.5F, 0.0F);
			GlStateManager.scalef(0.5F, 0.5F, 1.0F);

			final float f2 = j + 1.0F;

			GlStateManager.translatef(
					17.0F / f2,
					(2.0F + f2 / 1.5F) * (Util.milliTime() % 800000.0F / 800000.0F),
					0.0F
			);
			GlStateManager.rotatef((f2 * f2 * 4321.0F + f2 * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.scalef(4.5F - f2 / 4.0F, 4.5F - f2 / 4.0F, 1.0F);

			GlStateManager.multMatrix(PROJECTION);
			GlStateManager.multMatrix(MODEL_VIEW);

			final Tessellator tessellator = Tessellator.getInstance();
			final BufferBuilder builder = tessellator.getBuffer();

			builder.begin(7, DefaultVertexFormats.POSITION_COLOR);

			final float f3 = (RANDOM.nextFloat() * 0.5F + 0.1F) * f1;
			final float f4 = (RANDOM.nextFloat() * 0.5F + 0.4F) * f1;
			final float f5 = (RANDOM.nextFloat() * 0.5F + 0.5F) * f1;

			if (upsideDown) {
				if (tileEntity.shouldRenderFace(Direction.DOWN)) {
					builder.pos(x, y + 0.25, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y + 0.25, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y + 0.25, z).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x, y + 0.25, z).color(f3, f4, f5, 1.0F).endVertex();
				}

				if (tileEntity.shouldRenderFace(Direction.UP)) {
					builder.pos(x, y + 0.25, z).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y + 0.25, z).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y + 0.25, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x, y + 0.25, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
				}
			} else {
				if (tileEntity.shouldRenderFace(Direction.SOUTH)) {
					builder.pos(x, y, z + 0.5).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y, z + 0.5).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y + 1.0, z + 0.5).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x, y + 1.0, z + 0.5).color(f3, f4, f5, 1.0F).endVertex();
				}

				if (tileEntity.shouldRenderFace(Direction.NORTH)) {
					builder.pos(x, y + 1.0, z + 0.5).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y + 1.0, z + 0.5).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y, z + 0.5).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x, y, z + 0.5).color(f3, f4, f5, 1.0F).endVertex();
				}

				if (tileEntity.shouldRenderFace(Direction.EAST)) {
					builder.pos(x + 0.5, y + 1.0, z).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 0.5, y + 1.0, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 0.5, y, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 0.5, y, z).color(f3, f4, f5, 1.0F).endVertex();
				}

				if (tileEntity.shouldRenderFace(Direction.WEST)) {
					builder.pos(x + 0.5, y, z).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 0.5, y, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 0.5, y + 1.0, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 0.5, y + 1.0, z).color(f3, f4, f5, 1.0F).endVertex();
				}

				if (tileEntity.shouldRenderFace(Direction.DOWN)) {
					builder.pos(x, y + offset, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y + offset, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y + offset, z).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x, y + offset, z).color(f3, f4, f5, 1.0F).endVertex();
				}

				if (tileEntity.shouldRenderFace(Direction.UP)) {
					builder.pos(x, y + offset, z).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y + offset, z).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y + offset, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x, y + offset, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
				}
			}

			tessellator.draw();

			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);

			bindTexture(END_SKY_TEXTURE);
		}

		GlStateManager.disableBlend();
		GlStateManager.disableTexGen(GlStateManager.TexGen.S);
		GlStateManager.disableTexGen(GlStateManager.TexGen.T);
		GlStateManager.disableTexGen(GlStateManager.TexGen.R);
		GlStateManager.enableLighting();

		if (flag) {
			mc.gameRenderer.setupFogColor(false);
		}
	}

	private FloatBuffer getBuffer(float f1, float f2, float f3, float f4) {
		buffer.clear();
		buffer.put(f1).put(f2).put(f3).put(f4);
		buffer.flip();
		return buffer;
	}
}
