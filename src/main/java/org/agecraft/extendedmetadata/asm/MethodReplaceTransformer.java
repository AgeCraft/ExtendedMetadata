package org.agecraft.extendedmetadata.asm;

import java.util.Set;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import codechicken.lib.asm.ASMBlock;
import codechicken.lib.asm.ModularASMTransformer.MethodTransformer;
import codechicken.lib.asm.ObfMapping;

public class MethodReplaceTransformer extends MethodTransformer {

	public ASMBlock replacement;

	public MethodReplaceTransformer(ObfMapping method, ASMBlock replacement) {
		super(method);
		this.replacement = replacement;
	}

	public MethodReplaceTransformer(ObfMapping method, InsnList replacement) {
		this(method, new ASMBlock(replacement));
	}

	@Override
	public void addMethodsToSort(Set<ObfMapping> set) {
		set.add(method);
	}

	@Override
	public void transform(MethodNode methodNode) {
		methodNode.instructions.clear();
		methodNode.instructions.add(replacement.list.list);
		transformMethodLocals(methodNode);
	}
	
	public void transformMethodLocals(MethodNode methodNode) {
		
	}
}
