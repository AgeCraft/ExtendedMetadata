package org.agecraft.extendedmetadata.asm;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import codechicken.lib.asm.ModularASMTransformer.MethodTransformer;
import codechicken.lib.asm.ObfMapping;

public class MethodRemoveTransformer extends MethodTransformer {

	private ClassNode node;
	
	public MethodRemoveTransformer(ObfMapping method) {
		super(method);
	}
	
	@Override
	public void transform(ClassNode node) {
		this.node = node;
		super.transform(node);
	}
	
	@Override
	public void transform(MethodNode mv) {
		node.methods.remove(mv);
	}
}
