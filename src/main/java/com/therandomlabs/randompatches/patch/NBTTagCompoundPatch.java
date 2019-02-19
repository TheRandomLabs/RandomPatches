package com.therandomlabs.randompatches.patch;

import java.util.Map;
import java.util.Set;
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
import com.therandomlabs.randompatches.config.RPStaticConfig;
import com.therandomlabs.randompatches.core.Patch;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import org.apache.commons.codec.binary.Base64;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class NBTTagCompoundPatch extends Patch {
	public static final Gson GSON =
			new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

	@Override
	public boolean apply(ClassNode node) {
		final MethodNode method = findMethod(node, "equals");
		MethodInsnNode equals = null;

		for(int i = method.instructions.size() - 1; i >= 0; i--) {
			final AbstractInsnNode instruction = method.instructions.get(i);

			//On 1.11 and below, it's an invokeinterface (Set.equals)
			//On 1.12 and above, it's an invokestatic (Objects.equals)
			if(instruction.getOpcode() == Opcodes.INVOKESTATIC ||
					instruction.getOpcode() == Opcodes.INVOKEINTERFACE) {
				equals = (MethodInsnNode) instruction;

				if("equals".equals(equals.name)) {
					break;
				}

				equals = null;
			}
		}

		equals.setOpcode(Opcodes.INVOKESTATIC);
		equals.owner = getName(NBTTagCompoundPatch.class);
		equals.name = "areTagMapsEqual";
		equals.desc = "(Ljava/lang/Object;Ljava/lang/Object;)Z";
		equals.itf = false;

		return true;
	}

	@SuppressWarnings({"unchecked", "Duplicates"})
	public static boolean areTagMapsEqual(Object tagMapEntries1, Object tagMapEntries2) {
		if(tagMapEntries1 == tagMapEntries2) {
			return true;
		}

		if(tagMapEntries1 == null || tagMapEntries2 == null) {
			return false;
		}

		if(tagMapEntries1.equals(tagMapEntries2)) {
			return true;
		}

		final Set<Map.Entry<String, NBTBase>> entries1 =
				(Set<Map.Entry<String, NBTBase>>) tagMapEntries1;
		final Set<Map.Entry<String, NBTBase>> entries2 =
				(Set<Map.Entry<String, NBTBase>>) tagMapEntries2;

		NBTTagCompound skullOwner1 = null;

		for(Map.Entry<String, NBTBase> entry : entries1) {
			if("SkullOwner".equals(entry.getKey())) {
				final NBTBase base = entry.getValue();

				if(base instanceof NBTTagCompound) {
					skullOwner1 = (NBTTagCompound) base;
					break;
				}
			}
		}

		if(skullOwner1 == null) {
			return false;
		}

		NBTTagCompound skullOwner2 = null;

		for(Map.Entry<String, NBTBase> entry : entries2) {
			if("SkullOwner".equals(entry.getKey())) {
				final NBTBase base = entry.getValue();

				if(base instanceof NBTTagCompound) {
					skullOwner2 = (NBTTagCompound) base;
					break;
				}
			}
		}

		if(skullOwner2 == null) {
			return false;
		}

		final GameProfile profile1 = NBTUtil.readGameProfileFromNBT(skullOwner1);
		final GameProfile profile2 = NBTUtil.readGameProfileFromNBT(skullOwner2);

		if(!profile1.equals(profile2)) {
			return false;
		}

		if(!RPStaticConfig.skullStackingRequiresSameTextures) {
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
