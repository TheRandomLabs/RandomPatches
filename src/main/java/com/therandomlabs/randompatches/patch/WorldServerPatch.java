package com.therandomlabs.randompatches.patch;

import java.util.HashSet;
import java.util.Set;
import com.therandomlabs.randompatches.core.Patch;
import net.minecraft.world.NextTickListEntry;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

//https://github.com/SleepyTrousers/EnderCore/issues/105#issuecomment-475957390 and
//https://github.com/SleepyTrousers/EnderCore/issues/105#issuecomment-504779582 and
//https://github.com/SleepyTrousers/EnderCore/issues/105#issuecomment-506215102
//Thanks, malte0811!
public final class WorldServerPatch extends Patch {
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

	@SuppressWarnings("serial")
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

		public static HashSet newHashSet() {
			return new NextTickListEntryHashSet();
		}
	}

	@Override
	public boolean apply(ClassNode node) {
		final InsnList instructions = findInstructions(node, "<init>");

		for(int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if(instruction.getOpcode() == Opcodes.INVOKESTATIC) {
				final MethodInsnNode method = (MethodInsnNode) instruction;

				if("newHashSet".equals(method.name)) {
					method.owner = getName(WorldServerPatch.class) + "$NextTickListEntryHashSet";
					return true;
				}
			}
		}

		return false;
	}
}
