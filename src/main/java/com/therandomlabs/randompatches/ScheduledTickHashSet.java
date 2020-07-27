package com.therandomlabs.randompatches;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.ScheduledTick;
import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings({"NullAway", "rawtypes", "serial"})
public final class ScheduledTickHashSet extends HashSet<ScheduledTick> {
	private static final class ScheduledTickWrapper {
		Object entry;

		public ScheduledTickWrapper() {}

		public ScheduledTickWrapper(Object entry) {
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

	@Override
	public int size() {
		return backingSet.size();
	}

	@Override
	public boolean contains(Object entry) {
		wrapper.entry = entry;
		return backingSet.contains(wrapper);
	}

	@Override
	public boolean add(ScheduledTick entry) {
		return backingSet.add(new ScheduledTickWrapper(entry));
	}

	@Override
	public boolean remove(Object entry) {
		wrapper.entry = entry;
		return backingSet.remove(wrapper);
	}

	@Override
	public boolean removeAll(Collection<?> entries) {
		for (Object entry : entries) {
			remove(entry);
		}

		//Return value doesn't matter
		return true;
	}
}
