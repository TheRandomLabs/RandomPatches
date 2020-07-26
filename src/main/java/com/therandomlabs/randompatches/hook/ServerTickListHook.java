package com.therandomlabs.randompatches.hook;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.NextTickListEntry;

public final class ServerTickListHook {
	public static final class NextTickListEntryWrapper {
		Object entry;

		public NextTickListEntryWrapper() {}

		public NextTickListEntryWrapper(Object entry) {
			this.entry = entry;
		}

		@Override
		public int hashCode() {
			return entry.hashCode();
		}

		@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
		@Override
		public boolean equals(Object entry) {
			return this.entry.equals(((NextTickListEntryWrapper) entry).entry);
		}
	}

	@SuppressWarnings({"rawtypes", "serial"})
	public static final class NextTickListEntryHashSet extends HashSet<NextTickListEntry> {
		private final transient Set<NextTickListEntryWrapper> backingSet = new HashSet<>();
		private final transient NextTickListEntryWrapper wrapper = new NextTickListEntryWrapper();

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
		public boolean add(NextTickListEntry entry) {
			return backingSet.add(new NextTickListEntryWrapper(entry));
		}

		@Override
		public boolean remove(Object entry) {
			wrapper.entry = entry;
			return backingSet.remove(wrapper);
		}

		@SuppressWarnings("rawtypes")
		public static HashSet newHashSet() {
			return new NextTickListEntryHashSet();
		}
	}

	private ServerTickListHook() {}
}
