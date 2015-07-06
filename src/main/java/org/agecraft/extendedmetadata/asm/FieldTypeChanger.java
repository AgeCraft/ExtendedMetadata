package org.agecraft.extendedmetadata.asm;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import codechicken.lib.asm.ASMHelper;
import codechicken.lib.asm.ModularASMTransformer.ClassNodeTransformer;
import codechicken.lib.asm.ObfMapping;

public class FieldTypeChanger extends ClassNodeTransformer {

	public final ObfMapping field;
	public final String desc;

	public FieldTypeChanger(ObfMapping field, String desc) {
		this.field = field.toClassloading();
		this.desc = desc;
	}

	@Override
	public String className() {
		return field.javaClass();
	}

	@Override
	public void transform(ClassNode node) {
		FieldNode fieldNode = ASMHelper.findField(field, node);
		if(fieldNode == null) {
			throw new RuntimeException("Field not found: " + field);
		}
		fieldNode.desc = desc;
	}
}
