package org.agecraft.extendedmetadata.asm;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.ilexiconn.llibrary.asm.ASMHelper;
import net.ilexiconn.llibrary.asm.ModularASMTransformer.ClassNodeTransformer;
import net.ilexiconn.llibrary.asm.ObfMapping;

public abstract class MethodEditor extends ClassNodeTransformer {

	public final ObfMapping method;

	public MethodEditor(ObfMapping method) {
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

	public abstract void transformMethod(ClassNode node, MethodNode methodNode);
}
