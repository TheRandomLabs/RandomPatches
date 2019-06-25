package com.therandomlabs.randompatches.patch;

import java.util.Iterator;
import java.util.Set;
import com.therandomlabs.randompatches.core.Patch;
import net.minecraft.world.NextTickListEntry;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

//https://github.com/SleepyTrousers/EnderCore/issues/105#issuecomment-475957390 and
//https://github.com/SleepyTrousers/EnderCore/issues/105#issuecomment-504779582
public final class WorldServerPatch extends Patch {
	public static final String WORLD_SERVER_PATCH = getName(WorldServerPatch.class);

	@Override
	public boolean apply(ClassNode node) {
		patch(findInstructions(node, "isUpdateScheduled", "func_184145_b"), "contains");
		patch(findInstructions(node, "updateBlockTick", "func_175654_a"), "contains");
		patch(findInstructions(node, "scheduleBlockUpdate", "func_175684_a"), "contains");
		patch(findInstructions(node, "tickUpdates", "func_72955_a"), "remove");
		return true;
	}

	public static boolean contains(Set<NextTickListEntry> entries, Object object) {
		for(NextTickListEntry entry : entries) {
			if(object.equals(entry)) {
				return true;
			}
		}

		return false;
	}

	public static boolean remove(Set<NextTickListEntry> entries, Object object) {
		final Iterator it = entries.iterator();

		while(it.hasNext()) {
			if(object.equals(it.next())) {
				it.remove();
				return true;
			}
		}

		return false;
	}

	private static void patch(InsnList instructions, String methodName) {
		for(int i = 0; i < instructions.size(); i++) {
			final AbstractInsnNode instruction = instructions.get(i);

			if(instruction.getOpcode() == Opcodes.INVOKEINTERFACE) {
				final MethodInsnNode method = (MethodInsnNode) instruction;

				if("java/util/Set".equals(method.owner) && methodName.equals(method.name)) {
					method.setOpcode(Opcodes.INVOKESTATIC);
					method.owner = WORLD_SERVER_PATCH;
					method.desc = "(Ljava/util/Set;Ljava/lang/Object;)Z";
					return;
				}
			}
		}
	}
}
