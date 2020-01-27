package com.therandomlabs.randompatches.hook.client;

//Thanks Fuzs_!
public final class PlayerRendererHook {
	private PlayerRendererHook() {}

	//In RenderPlayer#applyRotations, Math#acos is sometimes called with a value larger than 1.0,
	//making the rotation angle NaN and causing the player model to disappear
	//This issue is noticeable when flying with elytra in a straight line in third-person mode
	public static double acos(double a) {
		return Math.acos(Math.min(a, 1.0));
	}
}
