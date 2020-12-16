package com.therandomlabs.randompatches.mixin.client;

import com.therandomlabs.randompatches.RandomPatches;
import net.minecraft.tileentity.EndPortalTileEntity;
import net.minecraft.util.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndPortalTileEntity.class)
public final class EndPortalTileEntityMixin {
	@Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
	private void shouldRenderFace(Direction face, CallbackInfoReturnable<Boolean> info) {
		if (RandomPatches.config().client.bugFixes.fixEndPortalRendering) {
			info.setReturnValue(face == Direction.UP || face == Direction.DOWN);
			info.cancel();
		}
	}
}
