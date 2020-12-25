package com.therandomlabs.randompatches.mixin.client.keybindings;

import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameOptions.class)
public interface GameOptionsMixin {
	@Mutable
	@Accessor
	void setKeysAll(KeyBinding[] keyBindings);
}
