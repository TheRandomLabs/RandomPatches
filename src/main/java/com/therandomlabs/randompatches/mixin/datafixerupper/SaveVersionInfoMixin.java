package com.therandomlabs.randompatches.mixin.datafixerupper;

import net.minecraft.world.level.storage.SaveVersionInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SaveVersionInfo.class)
public interface SaveVersionInfoMixin {
	@Accessor("versionId")
	int getVersionID();
}
