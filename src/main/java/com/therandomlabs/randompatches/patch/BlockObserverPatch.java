package com.therandomlabs.randompatches.patch;

import com.therandomlabs.randompatches.core.Patch;
import org.objectweb.asm.tree.ClassNode;

public final class BlockObserverPatch extends Patch {
	@Override
	public boolean apply(ClassNode node) {
		node.methods.remove(findMethod(node, "onBlockAdded", "func_176213_c"));
		return true;
	}
}
