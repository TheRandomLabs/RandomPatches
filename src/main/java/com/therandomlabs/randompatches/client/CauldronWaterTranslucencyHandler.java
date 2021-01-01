/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2021 TheRandomLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.therandomlabs.randompatches.client;

import com.therandomlabs.randompatches.RandomPatches;
import com.therandomlabs.randompatches.mixin.client.RenderLayersMixin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;

/**
 * Handles the fix for water in cauldrons rendering as opaque.
 */
@Environment(EnvType.CLIENT)
public final class CauldronWaterTranslucencyHandler {
	private static boolean enabled;

	private CauldronWaterTranslucencyHandler() {}

	/**
	 * Enables this class's functionality if it has not already been enabled.
	 */
	public static void enable() {
		if (!RandomPatches.config().misc.mixinBlacklist.contains("RenderLayers")) {
			enabled = true;
			onConfigReload();
		}
	}

	/**
	 * Called by {@link com.therandomlabs.randompatches.RPConfig.ClientBugFixes} when the
	 * RandomPatches configuration is reloaded.
	 */
	public static void onConfigReload() {
		if (enabled) {
			RenderLayersMixin.getBlocks().put(
					Blocks.CAULDRON,
					RandomPatches.config().client.bugFixes.fixWaterInCauldronsRenderingAsOpaque ?
							RenderLayer.getTranslucent() : RenderLayer.getSolid()
			);
		}
	}
}
