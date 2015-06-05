package org.agecraft.extendedmetadata.asm;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import codechicken.lib.asm.ASMHelper;
import codechicken.lib.asm.ModularASMTransformer.ClassNodeTransformer;
import codechicken.lib.asm.ObfMapping;

public abstract class MethodEditTransformer extends ClassNodeTransformer {

	public final ObfMapping method;

	public MethodEditTransformer(ObfMapping method) {
		this.method = method.toClassloading();
	}

	@Override
	public String className() {
		return method.javaClass();
	}

	@Override
	public void transform(ClassNode node) {
		MethodNode methodNode = ASMHelper.findMethod(method, node);
		if(methodNode == null) {
			throw new RuntimeException("Method not found: " + method);
		}
		transformMethod(node, methodNode);
	}
	
	public abstract void transformMethod(ClassNode node, MethodNode method);
}
