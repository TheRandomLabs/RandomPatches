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

package com.therandomlabs.randompatches.mixin;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.therandomlabs.randompatches.RandomPatches;
import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * The RandomPatches mixin config plugin.
 */
@SuppressWarnings("UnstableApiUsage")
public final class RPMixinConfig implements IMixinConfigPlugin {
	private static final ImmutableSet<ClassPath.ClassInfo> mixinClasses;

	static {
		ClassPath classPath;

		try {
			classPath = ClassPath.from(FMLLoader.getLaunchClassLoader());
		} catch (IOException ex) {
			throw new RuntimeException("Failed to list RandomPatches mixins", ex);
		}

		final String mixinPackage = RPMixinConfig.class.getPackage().getName();
		mixinClasses = classPath.getTopLevelClassesRecursive(mixinPackage).stream().
				filter(info -> info.getSimpleName().endsWith("Mixin")).
				collect(ImmutableSet.toImmutableSet());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onLoad(String mixinPackage) {
		//No-op.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRefMapperConfig() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return RandomPatches.config().misc.isMixinClassEnabled(mixinClassName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
		//No-op.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getMixins() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preApply(
			String targetClassName, ClassNode targetClass, String mixinClassName,
			IMixinInfo mixinInfo
	) {
		//No-op.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postApply(
			String targetClassName, ClassNode targetClass, String mixinClassName,
			IMixinInfo mixinInfo
	) {
		//No-op.
	}

	/**
	 * Returns a set of {@link ClassPath.ClassInfo} instances representing all RandomPatches
	 * mixin classes.
	 *
	 * @return an {@link ImmutableSet} of {@link ClassPath.ClassInfo} instances representing all
	 * RandomPatches mixin classes.
	 */
	public static ImmutableSet<ClassPath.ClassInfo> getMixinClasses() {
		return mixinClasses;
	}
}
