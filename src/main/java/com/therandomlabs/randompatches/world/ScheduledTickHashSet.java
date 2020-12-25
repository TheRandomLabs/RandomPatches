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

package com.therandomlabs.randompatches.world;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.therandomlabs.randompatches.mixin.ServerTickSchedulerMixin;
import net.minecraft.world.ScheduledTick;

/**
 * An implementation of {@link HashSet} designed to prevent the
 * {@code "TickNextTick list out of synch"} {@link IllegalStateException}.
 * This class should only be used by
 * {@link ServerTickSchedulerMixin}.
 * <p>
 * An explanation can be found
 * <a href="https://github.com/SleepyTrousers/EnderCore/issues/105">here</a>.
 */
@SuppressWarnings({"rawtypes", "serial"})
public final class ScheduledTickHashSet extends HashSet<ScheduledTick> {
	private static final class ScheduledTickWrapper {
		Object entry;

		ScheduledTickWrapper() {
			//Empty constructor.
		}

		ScheduledTickWrapper(Object entry) {
			this.entry = entry;
		}

		@Override
		public int hashCode() {
			return entry.hashCode();
		}

		@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
		@Override
		public boolean equals(Object entry) {
			return this.entry.equals(((ScheduledTickWrapper) entry).entry);
		}
	}

	private final transient Set<ScheduledTickWrapper> backingSet = new HashSet<>();
	private final transient ScheduledTickWrapper wrapper = new ScheduledTickWrapper();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return backingSet.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Object entry) {
		wrapper.entry = entry;
		return backingSet.contains(wrapper);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(ScheduledTick entry) {
		return backingSet.add(new ScheduledTickWrapper(entry));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(Object entry) {
		wrapper.entry = entry;
		return backingSet.remove(wrapper);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeAll(Collection<?> entries) {
		for (Object entry : entries) {
			remove(entry);
		}

		//Return value doesn't matter, as it is never used.
		return true;
	}
}
