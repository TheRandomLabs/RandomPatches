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

package com.therandomlabs.randompatches.mixin.client.datafixerupper;

import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.screen.ConfirmBackupScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ConfirmBackupScreen.class)
public final class ConfirmBackupScreenMixin extends Screen {
	@SuppressWarnings("PMD.AvoidProtectedFieldInFinalClass")
	@Shadow
	@Final
	protected ConfirmBackupScreen.ICallback callback;

	@Shadow
	@Final
	private Screen parentScreen;

	@Mutable
	@Shadow
	@Final
	private ITextComponent message;

	//CHECKSTYLE IGNORE MemberName FOR NEXT 10 LINES
	@Shadow
	@Final
	private boolean field_212994_d;

	@SuppressWarnings("PMD.SingularField")
	@Shadow
	private IBidiRenderer wrappedText;

	@Shadow
	private CheckboxButton field_212996_j;

	@Unique
	private ITextComponent modifiedTitle;

	//CHECKSTYLE IGNORE MissingJavadocMethod FOR NEXT 1 LINES
	protected ConfirmBackupScreenMixin(ITextComponent title) {
		super(title);
	}

	/**
	 * @author TheRandomLabs
	 * @reason this is the least convoluted way to implement this.
	 */
	@SuppressWarnings("ConstantConditions")
	@Override
	@Overwrite
	public void init() {
		super.init();

		boolean cancel = false;

		if (message instanceof TranslationTextComponent) {
			final String messageKey = ((TranslationTextComponent) message).getKey();
			cancel = "selectWorld.versionWarning".equals(messageKey) ||
					"selectWorld.backupWarning".equals(messageKey) ||
					"selectWorld.dataFixerUpperDisabled".equals(messageKey);
			modifiedTitle = new TranslationTextComponent("selectWorld.unableToLoad");
			message = new TranslationTextComponent("selectWorld.dataFixerUpperDisabled");
		} else {
			modifiedTitle = title;
		}

		wrappedText = IBidiRenderer.method_30890(textRenderer, message, width - 50);

		final int yOffset = (wrappedText.method_30887() + 1) * 9;

		if (cancel) {
			addButton(new Button(
					width / 2 - 155 + 80, 124 + yOffset, 150, 20, DialogTexts.BACK,
					button -> client.displayGuiScreen(parentScreen)
			));
			return;
		}

		addButton(new Button(
				width / 2 - 155, 100 + yOffset, 150, 20,
				new TranslationTextComponent("selectWorld.backupJoinConfirmButton"),
				button -> callback.proceed(true, field_212996_j.isChecked())
		));

		addButton(new Button(
				width / 2 - 155 + 160, 100 + yOffset, 150, 20,
				new TranslationTextComponent("selectWorld.backupJoinSkipButton"),
				button -> callback.proceed(false, field_212996_j.isChecked())
		));

		addButton(new Button(
				width / 2 - 155 + 80, 124 + yOffset, 150, 20, DialogTexts.CANCEL,
				button -> client.displayGuiScreen(parentScreen)
		));

		field_212996_j = new CheckboxButton(
				width / 2 - 155 + 80, 76 + yOffset, 150, 20,
				new TranslationTextComponent("selectWorld.backupEraseCache"), false
		);

		if (field_212994_d) {
			addButton(field_212996_j);
		}
	}

	@ModifyArg(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "net/minecraft/client/gui/screen/ConfirmBackupScreen." +
							"drawCenteredText(Lcom/mojang/blaze3d/matrix/MatrixStack;" +
							"Lnet/minecraft/client/gui/FontRenderer;" +
							"Lnet/minecraft/util/text/ITextComponent;III)V"
			),
			index = 2
	)
	private ITextComponent getTitle(ITextComponent title) {
		return modifiedTitle;
	}
}
