package com.therandomlabs.randompatches.hook.client;

import java.lang.reflect.Field;
import com.therandomlabs.randomlib.TRLUtils;
import com.therandomlabs.randompatches.config.RPConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public final class EntityRendererHook {
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final Field ENTITY_RENDERER_HOOK =
			TRLUtils.findField(EntityRenderer.class, "entityRendererHook");

	private float lastEyeHeight;
	private float eyeHeight;

	public static void updateRenderer(EntityRenderer renderer, EntityRendererHook hook) {
		hook = get(renderer, hook);
		hook.lastEyeHeight = hook.eyeHeight;
		hook.eyeHeight +=
				(mc.getRenderViewEntity().getEyeHeight() - hook.eyeHeight) * 0.5F;
	}

	public static float getEyeHeight(
			Entity entity, float partialTicks, EntityRenderer renderer,
			EntityRendererHook hook
	) {
		hook = get(renderer, hook);
		return hook.lastEyeHeight +
				(hook.eyeHeight - hook.lastEyeHeight) * partialTicks;
	}

	public static void orientCamera(
			float partialTicks, EntityRenderer renderer, EntityRendererHook hook
	) {
		if (!RPConfig.Client.smoothEyeLevelChanges) {
			return;
		}

		final Entity entity = mc.getRenderViewEntity();

		//EntityRenderer#orientCamera translates y by -Entity#getEyeHeight so we undo that
		//Then we translate y by -EntityRendererHook#getEyeHeight
		//We don't directly replace GlStateManager.translate(0.0F, -f, 0.0F) in
		//EntityRenderer#orientCamera because the method is overwritten by Valkyrien Skies
		GlStateManager.translate(
				0.0F,
				entity.getEyeHeight() - getEyeHeight(entity, partialTicks, renderer, hook),
				0.0F
		);
	}

	public static EntityRendererHook get(EntityRenderer renderer, EntityRendererHook hook) {
		if (hook == null) {
			hook = new EntityRendererHook();

			try {
				ENTITY_RENDERER_HOOK.set(renderer, hook);
			} catch (IllegalAccessException ex) {
				TRLUtils.crashReport("Failed to set EntityRenderer#entityRendererHook", ex);
			}
		}

		return hook;
	}
}
