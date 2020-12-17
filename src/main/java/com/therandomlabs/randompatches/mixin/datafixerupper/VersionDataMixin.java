package com.therandomlabs.randompatches.mixin.datafixerupper;

import net.minecraft.world.storage.VersionData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VersionData.class)
public interface VersionDataMixin {
	//This is necessary because Version#getVersionId() is annotated with @OnlyIn(Dist.CLIENT).
	@Accessor("versionId")
	int getVersionID();
}
