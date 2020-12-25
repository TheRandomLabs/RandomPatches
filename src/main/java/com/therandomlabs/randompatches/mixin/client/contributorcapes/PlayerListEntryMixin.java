package com.therandomlabs.randompatches.mixin.client.contributorcapes;

import java.util.Map;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerListEntry.class)
public interface PlayerListEntryMixin {
	@Accessor
	Map<MinecraftProfileTexture.Type, Identifier> getTextures();
}
