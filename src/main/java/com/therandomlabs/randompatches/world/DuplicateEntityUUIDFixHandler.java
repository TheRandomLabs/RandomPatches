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

package com.therandomlabs.randompatches.world;

import java.util.Random;
import java.util.UUID;

import com.therandomlabs.randompatches.RandomPatches;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.TypeFilterableList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.WorldChunk;

public final class DuplicateEntityUUIDFixHandler {
	private static final Random random = new Random();

	private DuplicateEntityUUIDFixHandler() {}

	/**
	 * Called when a chunk is loaded.
	 *
	 * @param world the world.
	 * @param chunk the chunk.
	 */
	@SuppressWarnings("ReferenceEquality")
	public static void onChunkLoad(ServerWorld world, WorldChunk chunk) {
		if (!RandomPatches.config().misc.bugFixes.fixDuplicateEntityUUIDs) {
			return;
		}

		//Fix found by CAS_ual_TY:
		//https://www.curseforge.com/minecraft/mc-mods/deuf-duplicate-entity-uuid-fix
		for (TypeFilterableList<Entity> entityList : chunk.getEntitySectionArray()) {
			for (Entity entity : entityList) {
				if (entity instanceof PlayerEntity) {
					continue;
				}

				final UUID uniqueID = entity.getUuid();

				if (world.getEntity(uniqueID) == entity) {
					continue;
				}

				UUID newUniqueID;

				do {
					newUniqueID = MathHelper.randomUuid(random);
				} while (world.getEntity(newUniqueID) != null);

				entity.setUuid(uniqueID);
				RandomPatches.logger.info(
						"Changing UUID of duplicate entity {} from {} to {}",
						entity.getType().getLootTableId(), uniqueID, newUniqueID
				);
			}
		}
	}
}
