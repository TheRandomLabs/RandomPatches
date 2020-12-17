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

package com.therandomlabs.randompatches.util;

import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.SharedConstants;

/**
 * A no-op {@link DataFixer}.
 */
public final class FakeDataFixer implements DataFixer {
	private static final class FakeSchema extends Schema {
		FakeSchema() {
			super(SharedConstants.getVersion().getWorldVersion(), null);
		}

		@Override
		protected Map<String, Type<?>> buildTypes() {
			return ImmutableMap.of();
		}

		@Override
		public Type<?> getTypeRaw(DSL.TypeReference type) {
			return null;
		}

		@Override
		public Type<?> getType(DSL.TypeReference type) {
			return null;
		}

		@Override
		public Type<?> getChoiceType(DSL.TypeReference type, String choiceName) {
			return null;
		}

		@Override
		public TaggedChoice.TaggedChoiceType<?> findChoiceType(DSL.TypeReference type) {
			return null;
		}

		@Override
		public void registerTypes(
				Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes,
				Map<String, Supplier<TypeTemplate>> blockEntityTypes
		) {
			//No-op.
		}

		@Override
		public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
			return ImmutableMap.of();
		}

		@Override
		public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
			return ImmutableMap.of();
		}
	}

	private static final FakeSchema FAKE_SCHEMA = new FakeSchema();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Dynamic<T> update(
			DSL.TypeReference type, Dynamic<T> input, int version, int newVersion
	) {
		return input;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Schema getSchema(int key) {
		return FAKE_SCHEMA;
	}
}
