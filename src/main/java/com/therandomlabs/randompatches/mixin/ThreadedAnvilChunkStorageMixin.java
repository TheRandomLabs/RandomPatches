package com.therandomlabs.randompatches.mixin;

import com.therandomlabs.randompatches.world.DuplicateEntityUUIDFixHandler;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ThreadedAnvilChunkStorage.class)
public final class ThreadedAnvilChunkStorageMixin {
	@Shadow
	@Final
	private ServerWorld world;

	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = "method_17227", at = @At("TAIL"))
	private void onChunkLoad(
			ChunkHolder chunkHolder, Chunk protoChunk, CallbackInfoReturnable<Chunk> info
	) {
		DuplicateEntityUUIDFixHandler.onChunkLoad(world, (WorldChunk) info.getReturnValue());
	}
}
