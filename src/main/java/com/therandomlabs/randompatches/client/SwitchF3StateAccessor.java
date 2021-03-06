/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2021 TheRandomLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.therandomlabs.randompatches.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * An interface used to access the switch F3 state of {@link net.minecraft.client.Keyboard}.
 */
@Environment(EnvType.CLIENT)
public interface SwitchF3StateAccessor {
	/**
	 * Returns the switch F3 state of this {@link net.minecraft.client.Keyboard}.
	 *
	 * @return the switch F3 state of this {@link net.minecraft.client.Keyboard}.
	 */
	boolean getSwitchF3State();

	/**
	 * Sets the switch F3 state of this {@link net.minecraft.client.Keyboard}.
	 *
	 * @param state the new switch F3 state.
	 */
	void setSwitchF3State(boolean state);
}
