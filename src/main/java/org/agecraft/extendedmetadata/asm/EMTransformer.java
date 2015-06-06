package org.agecraft.extendedmetadata.asm;

import java.util.ListIterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import codechicken.lib.asm.ModularASMTransformer;
import codechicken.lib.asm.ModularASMTransformer.ClassNodeTransformer;
import codechicken.lib.asm.ObfMapping;

public class EMTransformer implements IClassTransformer {
	
	private ModularASMTransformer transformer = new ModularASMTransformer();
	
	public EMTransformer() {
		transformer.add(new FieldTypeTransformer(new ObfMapping("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "data", "[C"), "[I"));
		transformer.add(new MethodEditTransformer(new ObfMapping("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "<init>", "(IZ)V")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.NEWARRAY) {
						IntInsnNode intInsn = (IntInsnNode) insn;
						if(intInsn.operand == Opcodes.T_CHAR) {
							intInsn.operand = Opcodes.T_INT;
						}
					} else if(insn.getOpcode() == Opcodes.PUTFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if(fieldInsn.name.equals("data") && fieldInsn.desc.equals("[C")) {
							fieldInsn.desc = "[I";
						}
					}
				}
			}
		});
		transformer.add(new MethodEditTransformer(new ObfMapping("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "get", "(III)Lnet/minecraft/block/state/IBlockState;")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.GETFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if(fieldInsn.name.equals("data") && fieldInsn.desc.equals("[C")) {
							fieldInsn.desc = "[I";
						}
					} else if(insn.getOpcode() == Opcodes.CALOAD) {
						methodNode.instructions.set(insn, new InsnNode(Opcodes.IALOAD));
					}
				}
			}
		});
		transformer.add(new MethodEditTransformer(new ObfMapping("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "set", "(IIILnet/minecraft/block/state/IBlockState;)V")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.GETFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if(fieldInsn.name.equals("data") && fieldInsn.desc.equals("[C")) {
							fieldInsn.desc = "[I";
						}
					} else if(insn.getOpcode() == Opcodes.I2C) {
						methodNode.instructions.remove(insn);
					} else if(insn.getOpcode() == Opcodes.CASTORE) {
						methodNode.instructions.set(insn, new InsnNode(Opcodes.IASTORE));
					}
				}
			}
		});
		transformer.add(new MethodEditTransformer(new ObfMapping("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "getData", "()[C")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				methodNode.desc = "()[I";
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.GETFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if(fieldInsn.name.equals("data") && fieldInsn.desc.equals("[C")) {
							fieldInsn.desc = "[I";
						}
					}
				}
			}
		});
		transformer.add(new MethodEditTransformer(new ObfMapping("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "setData", "([C)V")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				methodNode.desc = "([I)V";
				for(LocalVariableNode var : methodNode.localVariables) {
					if(var.desc.equals("[C")) {
						var.desc = "[I";
					}
				}
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.PUTFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if(fieldInsn.name.equals("data") && fieldInsn.desc.equals("[C")) {
							fieldInsn.desc = "[I";
						}
					} 
				}
			}
		});
		
		transformer.add(new MethodEditTransformer(new ObfMapping("net/minecraft/stats/StatList", "<clinit>", "()V")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.SIPUSH) {
						IntInsnNode intInsn = (IntInsnNode) insn;
						if(intInsn.operand == 4096) {
							methodNode.instructions.set(insn, new LdcInsnNode(new Integer(32768)));
						} else if(intInsn.operand == 32000) {
							methodNode.instructions.set(insn, new LdcInsnNode(new Integer(65536)));
						}
					} 
				}
			}
		});
		
		transformer.add(new MethodEditTransformer(new ObfMapping("net/minecraft/util/ObjectIntIdentityMap", "<init>", "()V")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.SIPUSH) {
						IntInsnNode intInsn = (IntInsnNode) insn;
						if(intInsn.operand == 512) {
							methodNode.instructions.set(insn, new LdcInsnNode(new Integer(8192)));
						}
					} else if(insn.getOpcode() == Opcodes.INVOKESTATIC) {
						MethodInsnNode methodInsn = (MethodInsnNode) insn;
						if(methodInsn.name.equals("newArrayList") && methodInsn.desc.equals("()Ljava/util/ArrayList;")) {
							methodInsn.name = "newArrayListWithExpectedSize";
							methodInsn.desc = "(I)Ljava/util/ArrayList;";
							methodNode.instructions.insertBefore(insn, new LdcInsnNode(new Integer(8192)));
						}
					}
				}
			}
		});
		
		transformer.add(new MethodEditTransformer(new ObfMapping("net/minecraft/world/chunk/ChunkPrimer", "<init>", "()V")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.NEWARRAY) {
						IntInsnNode intInsn = (IntInsnNode) insn;
						if(intInsn.operand == Opcodes.T_SHORT) {
							intInsn.operand = Opcodes.T_INT;
						}
					} else if(insn.getOpcode() == Opcodes.PUTFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if(fieldInsn.name.equals("data") && fieldInsn.desc.equals("[S")) {
							fieldInsn.desc = "[I";
						}
					}
				}
			}
		});
		transformer.add(new MethodEditTransformer(new ObfMapping("net/minecraft/world/chunk/ChunkPrimer", "getBlockState", "(I)Lnet/minecraft/block/state/IBlockState;")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.GETFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if(fieldInsn.name.equals("data") && fieldInsn.desc.equals("[S")) {
							fieldInsn.desc = "[I";
						}
					} else if(insn.getOpcode() == Opcodes.SALOAD) {
						methodNode.instructions.set(insn, new InsnNode(Opcodes.IALOAD));
					}
				}
			}
		});
		transformer.add(new MethodEditTransformer(new ObfMapping("net/minecraft/world/chunk/ChunkPrimer", "setBlockState", "(ILnet/minecraft/block/state/IBlockState;)V")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.GETFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if(fieldInsn.name.equals("data") && fieldInsn.desc.equals("[S")) {
							fieldInsn.desc = "[I";
						}
					} else if(insn.getOpcode() == Opcodes.I2S) {
						methodNode.instructions.remove(insn);
					} else if(insn.getOpcode() == Opcodes.SASTORE) {
						methodNode.instructions.set(insn, new InsnNode(Opcodes.IASTORE));
					}
				}
			}
		});
		
		transformer.add(new ClassNodeTransformer() {
			@Override
			public String className() {
				return "net.minecraftforge.fml.common.registry.GameData";
			}

			@Override
			public void transform(ClassNode node) {
				for(FieldNode fieldNode : node.fields) {
					if(fieldNode.desc.equals("I")) {
						if(fieldNode.name.equals("MAX_BLOCK_ID")) {
							fieldNode.value = 32767;
						} else if(fieldNode.name.equals("MIN_ITEM_ID")) {
							fieldNode.value = 32768;
						} else if(fieldNode.name.equals("MAX_ITEM_ID")) {
							fieldNode.value = 65535;
						}
					}
				}
			}
		});
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if(bytes == null) {
			return null;
		}
		try {
			bytes = transformer.transform(name, bytes);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return bytes;
	}
}
