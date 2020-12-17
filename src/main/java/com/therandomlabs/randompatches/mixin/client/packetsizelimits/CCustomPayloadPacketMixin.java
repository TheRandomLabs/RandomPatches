/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 TheRandomLabs
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

package com.therandomlabs.randompatches.mixin.client.packetsizelimits;

import com.therandomlabs.randompatches.RandomPatches;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CCustomPayloadPacket.class)
public final class CCustomPayloadPacketMixin {
	@ModifyConstant(method = "readPacketData", constant = @Constant(intValue = Short.MAX_VALUE))
	private int getMaxClientCustomPayloadPacketSize(int size) {
		return RandomPatches.config().packetSizeLimits.maxClientCustomPayloadPacketSize;
	}

	@ModifyConstant(
			method = "readPacketData",
			constant = @Constant(
					stringValue = "Payload may not be larger than " + Short.MAX_VALUE + " bytes"
			)
	)
	private String getPayloadTooLargeErrorMessage(String message) {
		return "Payload may not be larger than " +
				RandomPatches.config().packetSizeLimits.maxClientCustomPayloadPacketSize + " bytes";
	}
}
