/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 TheRandomLabs
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

package com.therandomlabs.randompatches.mixin;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.therandomlabs.randompatches.RPConfig;
import com.therandomlabs.randompatches.RandomPatches;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import org.apache.commons.codec.binary.Base64;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CompoundNBT.class)
public final class CompoundNBTMixin {
	@Unique
	private static final Gson gson =
			new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

	@SuppressWarnings("unchecked")
	@Redirect(method = "equals", at = @At(
			value = "INVOKE",
			target = "java/util/Objects.equals(Ljava/lang/Object;Ljava/lang/Object;)Z"
	))
	private boolean areTagMapsEqual(Object object1, Object object2) {
		final RPConfig.PlayerHeadStackingFixMode mode =
				RandomPatches.config().misc.bugFixes.fixPlayerHeadStacking;

		if (mode == RPConfig.PlayerHeadStackingFixMode.DISABLED) {
			return Objects.equals(object1, object2);
		}

		if (Objects.equals(object1, object2)) {
			return true;
		}

		final INBT skullOwner1 = ((Map<String, INBT>) object1).get("SkullOwner");

		if (!(skullOwner1 instanceof CompoundNBT)) {
			return false;
		}

		final INBT skullOwner2 = ((Map<String, INBT>) object2).get("SkullOwner");

		if (!(skullOwner2 instanceof CompoundNBT)) {
			return false;
		}

		final GameProfile profile1 = NBTUtil.readGameProfile((CompoundNBT) skullOwner1);
		final GameProfile profile2 = NBTUtil.readGameProfile((CompoundNBT) skullOwner2);

		if (profile1 == null || !profile1.equals(profile2)) {
			return false;
		}

		if (mode == RPConfig.PlayerHeadStackingFixMode.REQUIRE_SAME_PLAYER) {
			return true;
		}

		final MinecraftProfileTexture texture1 = getSkin(profile1);
		final MinecraftProfileTexture texture2 = getSkin(profile2);

		return texture1 != null && texture2 != null && texture1.getUrl().equals(texture2.getUrl());
	}

	@Unique
	@Nullable
	private static MinecraftProfileTexture getSkin(GameProfile profile) {
		final MinecraftTexturesPayload payload = getTexturesPayload(profile);
		return payload == null ?
				null : payload.getTextures().get(MinecraftProfileTexture.Type.SKIN);
	}

	@Unique
	@Nullable
	private static MinecraftTexturesPayload getTexturesPayload(GameProfile profile) {
		final Property textureProperty =
				Iterables.getFirst(profile.getProperties().get("textures"), null);

		if (textureProperty == null) {
			return null;
		}

		return gson.fromJson(new String(
				Base64.decodeBase64(textureProperty.getValue()), StandardCharsets.UTF_8
		), MinecraftTexturesPayload.class);
	}
}
