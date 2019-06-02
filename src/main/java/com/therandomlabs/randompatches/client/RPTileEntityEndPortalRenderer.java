package com.therandomlabs.randompatches.client;

import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class RPTileEntityEndPortalRenderer extends
		TileEntitySpecialRenderer<TileEntityEndPortal> {
	private static final ResourceLocation END_SKY_TEXTURE =
			new ResourceLocation("textures/environment/end_sky.png");
	private static final ResourceLocation END_PORTAL_TEXTURE =
			new ResourceLocation("textures/entity/end_portal.png");

	private static final Random RANDOM = new Random(31100L);

	private static final FloatBuffer MODEL_VIEW = GLAllocation.createDirectFloatBuffer(16);
	private static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);

	private static final Minecraft mc = Minecraft.getMinecraft();

	private final FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
	private final boolean upsideDown;

	public RPTileEntityEndPortalRenderer() {
		this(false);
	}

	public RPTileEntityEndPortalRenderer(boolean upsideDown) {
		this.upsideDown = upsideDown;
	}

	@Override
	public void render(TileEntityEndPortal tileEntity, double x, double y, double z,
			float partialTicks, int destroyStage, float alpha) {
		GlStateManager.disableLighting();

		RANDOM.setSeed(31100L);

		GlStateManager.getFloat(2982, MODEL_VIEW);
		GlStateManager.getFloat(2983, PROJECTION);

		final int passes = getPasses(x * x + y * y + z * z);
		final float offset = getOffset();
		boolean flag = false;

		for(int j = 0; j < passes; j++) {
			GlStateManager.pushMatrix();
			float f1 = 2.0F / (18 - j);

			if(j == 0) {
				bindTexture(END_SKY_TEXTURE);
				f1 = 0.15F;
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(
						GlStateManager.SourceFactor.SRC_ALPHA,
						GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
				);
			}

			if(j >= 1) {
				bindTexture(END_PORTAL_TEXTURE);
				flag = true;
				mc.entityRenderer.setupFogColor(true);
			}

			if(j == 1) {
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(
						GlStateManager.SourceFactor.ONE,
						GlStateManager.DestFactor.ONE
				);
			}

			GlStateManager.texGen(GlStateManager.TexGen.S, 9216);
			GlStateManager.texGen(GlStateManager.TexGen.T, 9216);
			GlStateManager.texGen(GlStateManager.TexGen.R, 9216);

			GlStateManager.texGen(
					GlStateManager.TexGen.S, 9474, getBuffer(1.0F, 0.0F, 0.0F, 0.0F)
			);
			GlStateManager.texGen(
					GlStateManager.TexGen.T, 9474, getBuffer(0.0F, 1.0F, 0.0F, 0.0F)
			);
			GlStateManager.texGen(
					GlStateManager.TexGen.R, 9474, getBuffer(0.0F, 0.0F, 1.0F, 0.0F)
			);

			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);

			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();

			GlStateManager.translate(0.5F, 0.5F, 0.0F);
			GlStateManager.scale(0.5F, 0.5F, 1.0F);

			final float f2 = j + 1.0F;

			GlStateManager.translate(
					17.0F / f2,
					(2.0F + f2 / 1.5F) * (Minecraft.getSystemTime() % 800000.0F / 800000.0F),
					0.0F
			);
			GlStateManager.rotate((f2 * f2 * 4321.0F + f2 * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.scale(4.5F - f2 / 4.0F, 4.5F - f2 / 4.0F, 1.0F);

			GlStateManager.multMatrix(PROJECTION);
			GlStateManager.multMatrix(MODEL_VIEW);

			final Tessellator tessellator = Tessellator.getInstance();
			final BufferBuilder builder = tessellator.getBuffer();

			builder.begin(7, DefaultVertexFormats.POSITION_COLOR);

			final float f3 = (RANDOM.nextFloat() * 0.5F + 0.1F) * f1;
			final float f4 = (RANDOM.nextFloat() * 0.5F + 0.4F) * f1;
			final float f5 = (RANDOM.nextFloat() * 0.5F + 0.5F) * f1;

			if(upsideDown) {
				if(tileEntity.shouldRenderFace(EnumFacing.DOWN)) {
					builder.pos(x, y + 0.25, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y + 0.25, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y + 0.25, z).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x, y + 0.25, z).color(f3, f4, f5, 1.0F).endVertex();
				}

				if(tileEntity.shouldRenderFace(EnumFacing.UP)) {
					builder.pos(x, y + 0.25, z).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y + 0.25, z).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y + 0.25, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x, y + 0.25, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
				}
			} else {
				if(tileEntity.shouldRenderFace(EnumFacing.SOUTH)) {
					builder.pos(x, y, z + 0.5).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y, z + 0.5).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y + 1.0, z + 0.5).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x, y + 1.0, z + 0.5).color(f3, f4, f5, 1.0F).endVertex();
				}

				if(tileEntity.shouldRenderFace(EnumFacing.NORTH)) {
					builder.pos(x, y + 1.0, z + 0.5).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y + 1.0, z + 0.5).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y, z + 0.5).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x, y, z + 0.5).color(f3, f4, f5, 1.0F).endVertex();
				}

				if(tileEntity.shouldRenderFace(EnumFacing.EAST)) {
					builder.pos(x + 0.5, y + 1.0, z).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 0.5, y + 1.0, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 0.5, y, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 0.5, y, z).color(f3, f4, f5, 1.0F).endVertex();
				}

				if(tileEntity.shouldRenderFace(EnumFacing.WEST)) {
					builder.pos(x + 0.5, y, z).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 0.5, y, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 0.5, y + 1.0, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 0.5, y + 1.0, z).color(f3, f4, f5, 1.0F).endVertex();
				}

				if(tileEntity.shouldRenderFace(EnumFacing.DOWN)) {
					builder.pos(x, y + offset, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y + offset, z + 1.0).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x + 1.0, y + offset, z).color(f3, f4, f5, 1.0F).endVertex();
					builder.pos(x, y + offset, z).color(f3, f4, f5, 1.0F).endVertex();
				}

				if(tileEntity.shouldRenderFace(EnumFacing.UP)) {
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
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
		GlStateManager.enableLighting();

		if(flag) {
			mc.entityRenderer.setupFogColor(false);
		}
	}

	//From 1.13+
	private int getPasses(double d) {
		if(d > 36864.0) {
			return 1;
		}

		if(d > 25600.0) {
			return 3;
		}

		if(d > 16384.0) {
			return 5;
		}

		if(d > 9216.0) {
			return 7;
		}

		if(d > 4096.0) {
			return 9;
		}

		if(d > 1024.0) {
			return 11;
		}

		if(d > 576.0) {
			return 13;
		}

		if(d > 256.0) {
			return 14;
		}
		return 15;
	}

	private float getOffset() {
		return 0.75F;
	}

	private FloatBuffer getBuffer(float f1, float f2, float f3, float f4) {
		buffer.clear();
		buffer.put(f1).put(f2).put(f3).put(f4);
		buffer.flip();
		return buffer;
	}
}
