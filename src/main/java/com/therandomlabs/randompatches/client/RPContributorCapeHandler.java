/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 TheRandomLabs
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

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.therandomlabs.randompatches.RandomPatches;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

/**
 * Handles contributor capes for RandomPatches.
 */
public final class RPContributorCapeHandler {
	private static final URI CONTRIBUTORS = URI.create(
			"https://raw.githubusercontent.com/TheRandomLabs/RandomPatches/misc/contributors.txt"
	);
	private static final Identifier CAPE =
			new Identifier(RandomPatches.MOD_ID, "textures/contributor_cape.png");

	private static final Set<UUID> temporaryPlayerInfos = new HashSet<>();

	private static final List<String> contributors = new ArrayList<>();
	private static boolean attemptingDownload;
	private static int tries;

	private RPContributorCapeHandler() {}
/*
	@SubscribeEvent
	public static void onPreRenderPlayer(RenderPlayerEvent.Pre event) {
		if (!RandomPatches.config().client.contributorCapes) {
			return;
		}

		if (contributors.isEmpty()) {
			downloadContributorList();
			return;
		}

		final AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) event.getPlayer();

		if (FMLEnvironment.production && !contributors.contains(player.getCachedUniqueIdString())) {
			return;
		}

		//If AbstractClientPlayerEntity#playerInfo is null, this method attempts to retrieve it.
		player.getPlayerInfo();

		if (player.playerInfo == null) {
			player.playerInfo = new NetworkPlayerInfo(new SPlayerListItemPacket().new AddPlayerData(
					player.getGameProfile(), 0, null, null
			));
			temporaryPlayerInfos.add(player.getUniqueID());
		}

		player.playerInfo.playerTextures.putIfAbsent(MinecraftProfileTexture.Type.CAPE, CAPE);
		player.playerInfo.playerTextures.putIfAbsent(MinecraftProfileTexture.Type.ELYTRA, CAPE);
	}

	@SubscribeEvent
	public static void onPostRenderPlayer(RenderPlayerEvent.Post event) {
		final AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) event.getPlayer();
		final UUID uniqueID = player.getUniqueID();

		if (temporaryPlayerInfos.contains(uniqueID)) {
			player.playerInfo = null;
			temporaryPlayerInfos.remove(uniqueID);
		}
	}*/

	/**
	 * Attempts to download the RandomPatches contributor list.
	 */
	public static void downloadContributorList() {
		if (attemptingDownload || tries > 5) {
			return;
		}

		attemptingDownload = true;

		new Thread(() -> {
			try {
				contributors.clear();
				contributors.addAll(IOUtils.readLines(new StringReader(
						IOUtils.toString(CONTRIBUTORS, StandardCharsets.UTF_8)
				)));
			} catch (IOException ex) {
				RandomPatches.logger.error("Failed to download contributor list", ex);
				tries++;

				try {
					Thread.sleep(10000L);
				} catch (InterruptedException ex2) {
					RandomPatches.logger.error("Failed to sleep", ex2);
				}
			}

			attemptingDownload = false;
		}).start();
	}
}
