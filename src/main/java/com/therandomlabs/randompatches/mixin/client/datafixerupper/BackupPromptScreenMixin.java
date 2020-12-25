/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 TheRandomLabs
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

package com.therandomlabs.randompatches.mixin.client.datafixerupper;

import net.minecraft.class_5489;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackupPromptScreen.class)
public final class BackupPromptScreenMixin extends Screen {
	@SuppressWarnings({"PMD.AvoidProtectedFieldInFinalClass", "ProtectedMembersInFinalClass"})
	@Shadow
	@Final
	protected BackupPromptScreen.Callback callback;

	@Shadow
	@Final
	private Screen parent;

	@Shadow
	@Final
	@Mutable
	private Text subtitle;

	//CHECKSTYLE IGNORE MemberName FOR NEXT 9 LINES
	@Shadow
	@Final
	private boolean showEraseCacheCheckbox;

	@Shadow
	private class_5489 wrappedText;

	@Shadow
	private CheckboxWidget eraseCacheCheckbox;

	@Unique
	private Text modifiedTitle;

	//CHECKSTYLE IGNORE MissingJavadocMethod FOR NEXT 1 LINES
	protected BackupPromptScreenMixin(Text title) {
		super(title);
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "init", at = @At("HEAD"), cancellable = true)
	private void initialize(CallbackInfo info) {
		modifiedTitle = title;

		if (!(subtitle instanceof TranslatableText)) {
			return;
		}

		final String messageKey = ((TranslatableText) subtitle).getKey();

		if (!"selectWorld.versionWarning".equals(messageKey) &&
				!"selectWorld.backupWarning".equals(messageKey) &&
				!"selectWorld.dataFixerUpperDisabled".equals(messageKey)) {
			return;
		}

		info.cancel();

		super.init();

		modifiedTitle = new TranslatableText("selectWorld.unableToLoad");
		subtitle = new TranslatableText("selectWorld.dataFixerUpperDisabled");

		wrappedText = class_5489.method_30890(textRenderer, subtitle, width - 50);

		addButton(new ButtonWidget(
				width / 2 - 155 + 80, 124 + (wrappedText.method_30887() + 1) * 9, 150, 20,
				ScreenTexts.BACK, button -> client.openScreen(parent)
		));
	}

	@ModifyArg(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/BackupPromptScreen;" +
							"drawCenteredText(Lnet/minecraft/client/util/math/MatrixStack;" +
							"Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"
			),
			index = 2
	)
	private Text getTitle(Text title) {
		return modifiedTitle;
	}
}
