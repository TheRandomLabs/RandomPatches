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
import com.therandomlabs.randompatches.config.RPConfig;
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

		MethodInsnNode entrySet1 = null;
		MethodInsnNode entrySet2 = null;
		MethodInsnNode equals = null;

		for(int i = 0; i < method.instructions.size(); i++) {
			final AbstractInsnNode instruction = method.instructions.get(i);
			final int opcode = instruction.getOpcode();

			//On 1.11 and below, it's an invokeinterface (Set.equals)
			//On 1.12 and above, it's an invokestatic (Objects.equals)
			if(opcode != Opcodes.INVOKESTATIC && opcode != Opcodes.INVOKEINTERFACE) {
				continue;
			}

			final MethodInsnNode methodInsnNode = (MethodInsnNode) instruction;

			if(entrySet1 == null) {
				if("entrySet".equals(methodInsnNode.name)) {
					entrySet1 = methodInsnNode;
				}

				continue;
			}

			if(entrySet2 == null) {
				if("entrySet".equals(methodInsnNode.name)) {
					entrySet2 = methodInsnNode;
				}

				continue;
			}

			if("equals".equals(methodInsnNode.name)) {
				equals = methodInsnNode;
				break;
			}
		}

		method.instructions.remove(entrySet1);
		method.instructions.remove(entrySet2);

		equals.setOpcode(Opcodes.INVOKESTATIC);
		equals.owner = getName(NBTTagCompoundPatch.class);
		equals.name = "areTagMapsEqual";
		equals.desc = "(Ljava/util/Map;Ljava/util/Map;)Z";
		equals.itf = false;

		return true;
	}

	public static boolean areTagMapsEqual(Map<String, NBTBase> tagMap1,
			Map<String, NBTBase> tagMap2) {
		if(tagMap1.entrySet().equals(tagMap2.entrySet())) {
			return true;
		}

		final NBTBase skullOwner1 = tagMap1.get("SkullOwner");

		if(!(skullOwner1 instanceof NBTTagCompound)) {
			return false;
		}

		final NBTBase skullOwner2 = tagMap2.get("SkullOwner");

		if(!(skullOwner2 instanceof NBTTagCompound)) {
			return false;
		}

		final GameProfile profile1 = NBTUtil.readGameProfileFromNBT((NBTTagCompound) skullOwner1);
		final GameProfile profile2 = NBTUtil.readGameProfileFromNBT((NBTTagCompound) skullOwner2);

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
