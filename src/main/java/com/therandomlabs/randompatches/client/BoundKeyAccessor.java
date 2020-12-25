package com.therandomlabs.randompatches.client;

import net.minecraft.client.util.InputUtil;

/**
 * An interface used to access the bound key of a {@link net.minecraft.client.options.KeyBinding}.
 */
public interface BoundKeyAccessor {
	/**
	 * Returns this {@link net.minecraft.client.options.KeyBinding}'s bound key.
	 *
	 * @return this {@link net.minecraft.client.options.KeyBinding}'s bound key.
	 */
	InputUtil.Key getBoundKey();
}
