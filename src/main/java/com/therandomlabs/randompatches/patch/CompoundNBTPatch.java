package com.therandomlabs.randompatches.patch;

import java.util.Map;
import java.util.UUID;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.therandomlabs.randompatches.RPConfig;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import org.apache.commons.codec.binary.Base64;

public final class CompoundNBTPatch {
	public static final Gson GSON =
			new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

	private CompoundNBTPatch() {}

	public static boolean areTagMapsEqual(Map<String, INBT> tagMap1, Map<String, INBT> tagMap2) {
		if(tagMap1.equals(tagMap2)) {
			return true;
		}

		final INBT skullOwner1 = tagMap1.get("SkullOwner");

		if(!(skullOwner1 instanceof CompoundNBT)) {
			return false;
		}

		final INBT skullOwner2 = tagMap2.get("SkullOwner");

		if(!(skullOwner2 instanceof CompoundNBT)) {
			return false;
		}

		final GameProfile profile1 = NBTUtil.readGameProfile((CompoundNBT) skullOwner1);
		final GameProfile profile2 = NBTUtil.readGameProfile((CompoundNBT) skullOwner2);

		if(!profile1.equals(profile2)) {
			return false;
		}

		if(!RPConfig.Misc.skullStackingRequiresSameTextures) {
			return true;
		}

		final MinecraftProfileTexture texture1 = getSkin(profile1);
		final MinecraftProfileTexture texture2 = getSkin(profile2);

		return texture1 != null && texture2 != null && texture1.getUrl().equals(texture2.getUrl());
	}

	public static MinecraftTexturesPayload getTextures(GameProfile profile) {
		final Property textureProperty =
				Iterables.getFirst(profile.getProperties().get("textures"), null);

		if(textureProperty == null) {
			return null;
		}

		final String json =
				new String(Base64.decodeBase64(textureProperty.getValue()), Charsets.UTF_8);
		return GSON.fromJson(json, MinecraftTexturesPayload.class);
	}

	public static MinecraftProfileTexture getSkin(GameProfile profile) {
		final MinecraftTexturesPayload payload = getTextures(profile);
		return payload == null ?
				null : payload.getTextures().get(MinecraftProfileTexture.Type.SKIN);
	}
}
