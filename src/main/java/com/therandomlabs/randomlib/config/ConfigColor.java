package com.therandomlabs.randomlib.config;

import net.minecraft.item.DyeColor;

public enum ConfigColor {
	WHITE("white"),
	ORANGE("orange"),
	MAGENTA("magenta"),
	LIGHT_BLUE("light_blue"),
	YELLOW("yellow"),
	LIME("lime"),
	PINK("pink"),
	GRAY("gray"),
	LIGHT_GRAY("light_gray"),
	CYAN("cyan"),
	PURPLE("purple"),
	BLUE("blue"),
	BROWN("brown"),
	GREEN("green"),
	RED("red"),
	BLACK("black");

	private static String translationKeyPrefix = "";

	private final String translationKey;
	private final DyeColor color;

	ConfigColor(String translationKey) {
		this.translationKey = translationKey;
		color = DyeColor.valueOf(name());
	}

	@Override
	public String toString() {
		return translationKeyPrefix + translationKey;
	}

	public DyeColor get() {
		return color;
	}

	public static void setTranslationKeyPrefix(String prefix) {
		translationKeyPrefix = prefix;
	}
}
