package org.agecraft.extendedmetadata.asm;

import java.util.ListIterator;
import java.util.Map;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

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

import codechicken.lib.asm.ASMBlock;
import codechicken.lib.asm.ASMReader;
import codechicken.lib.asm.InsnComparator;
import codechicken.lib.asm.InsnListSection;
import codechicken.lib.asm.ModularASMTransformer;
import codechicken.lib.asm.ModularASMTransformer.ClassNodeTransformer;
import codechicken.lib.asm.ModularASMTransformer.MethodReplacer;
import codechicken.lib.asm.ModularASMTransformer.MethodWriter;
import codechicken.lib.asm.ObfMapping;

public class EMTransformer implements IClassTransformer {
	
	private ModularASMTransformer transformer = new ModularASMTransformer();
	private Map<String, ASMBlock> asmblocks = ASMReader.loadResource("/assets/extendedmetadata/asm/blocks.asm");
	
	public EMTransformer() {
		transformer.add(new FieldTypeChanger(new ObfMapping("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "field_177488_d", "[C"), "[I"));
		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "<init>", "(IZ)V")) {
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
						if((fieldInsn.name.equals("data") || fieldInsn.name.equals("field_177488_d")) && fieldInsn.desc.equals("[C")) {
							fieldInsn.desc = "[I";
						}
					}
				}
			}
		});
		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "func_177485_a", "(III)Lnet/minecraft/block/state/IBlockState;")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.GETFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if((fieldInsn.name.equals("data") || fieldInsn.name.equals("field_177488_d")) && fieldInsn.desc.equals("[C")) {
							fieldInsn.desc = "[I";
						}
					} else if(insn.getOpcode() == Opcodes.CALOAD) {
						methodNode.instructions.set(insn, new InsnNode(Opcodes.IALOAD));
					}
				}
			}
		});
		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "func_177484_a", "(IIILnet/minecraft/block/state/IBlockState;)V")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.GETFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if((fieldInsn.name.equals("data") || fieldInsn.name.equals("field_177488_d")) && fieldInsn.desc.equals("[C")) {
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
		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "func_177487_g", "()[C")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				methodNode.desc = "()[I";
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.GETFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if((fieldInsn.name.equals("data") || fieldInsn.name.equals("field_177488_d")) && fieldInsn.desc.equals("[C")) {
							fieldInsn.desc = "[I";
						}
					}
				}
			}
		});
		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "func_177486_a", "([C)V")) {
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
						if((fieldInsn.name.equals("data") || fieldInsn.name.equals("field_177488_d")) && fieldInsn.desc.equals("[C")) {
							fieldInsn.desc = "[I";
						}
					} 
				}
			}
		});
		
		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/stats/StatList", "<clinit>", "()V")) {
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
		
		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/util/ObjectIntIdentityMap", "<init>", "()V")) {
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
		
		// ChunkPrimer.data
		transformer.add(new FieldTypeChanger(new ObfMapping("net/minecraft/world/chunk/ChunkPrimer", "field_177860_a", "[S"), "[I"));
		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/world/chunk/ChunkPrimer", "<init>", "()V")) {
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
						if((fieldInsn.name.equals("data") || fieldInsn.name.equals("field_177860_a")) && fieldInsn.desc.equals("[S")) {
							fieldInsn.desc = "[I";
						}
					}
				}
			}
		});
		// ChunkPrimer.getBlockState
		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/world/chunk/ChunkPrimer", "func_177858_a", "(I)Lnet/minecraft/block/state/IBlockState;")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.GETFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if((fieldInsn.name.equals("data") || fieldInsn.name.equals("field_177860_a")) && fieldInsn.desc.equals("[S")) {
							fieldInsn.desc = "[I";
						}
					} else if(insn.getOpcode() == Opcodes.SALOAD) {
						methodNode.instructions.set(insn, new InsnNode(Opcodes.IALOAD));
					}
				}
			}
		});
		// ChunkPrimer.setBlockState
		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/world/chunk/ChunkPrimer", "func_177858_a", "(ILnet/minecraft/block/state/IBlockState;)V")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.GETFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if((fieldInsn.name.equals("data") || fieldInsn.name.equals("field_177860_a")) && fieldInsn.desc.equals("[S")) {
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
		final ASMBlock needle = asmblocks.get("old_registerBlock");
		final ASMBlock replacement = asmblocks.get("registerBlock");
		transformer.add(new ClassNodeTransformer() {
			@Override
			public String className() {
				return "net.minecraftforge.fml.common.registry.GameData";
			}

			@Override
			public void transform(ClassNode node) {
				for(MethodNode methodNode : node.methods) {
					if(methodNode.name.equals("registerBlock") && methodNode.desc.equals("(Lnet/minecraft/block/Block;Ljava/lang/String;I)I")) {
						for(InsnListSection key : InsnComparator.findN(methodNode.instructions, needle.list)) {
			                ASMBlock replaceBlock = replacement.copy().pullLabels(needle.applyLabels(key));
			                key.insert(replaceBlock.list.list);
			            }
					}
				}
			}
		});

		transformer.add(new MethodWriter(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, new ObfMapping("net/minecraft/block/Block", "func_176210_f", "(Lnet/minecraft/block/state/IBlockState;)I"), asmblocks.get("getStateId")));
		transformer.add(new MethodWriter(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, new ObfMapping("net/minecraft/block/Block", "func_176220_d", "(I)Lnet/minecraft/block/state/IBlockState;"), asmblocks.get("getStateById")));
		
		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/network/play/server/S21PacketChunkData", "func_180737_a", "(IZZ)I")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.ICONST_2) {
						methodNode.instructions.set(insn, new InsnNode(Opcodes.ICONST_4));
						break;
					}
				}
			}
		});
		transformer.add(new MethodWriter(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, new ObfMapping("net/minecraft/network/play/server/S21PacketChunkData", "func_179756_a", "(Lnet/minecraft/world/chunk/Chunk;ZZI)Lnet/minecraft/network/play/server/S21PacketChunkData$Extracted;"), asmblocks.get("writeChunkToPacket")));
		
		transformer.add(new MethodWriter(Opcodes.ACC_PUBLIC, new ObfMapping("net/minecraft/world/chunk/storage/AnvilChunkLoader", "func_75823_a", "(Lnet/minecraft/world/World;Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/world/chunk/Chunk;"), asmblocks.get("readChunkFromNBT")));
		transformer.add(new MethodWriter(Opcodes.ACC_PUBLIC, new ObfMapping("net/minecraft/world/chunk/storage/AnvilChunkLoader", "func_75820_a", "(Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/world/World;Lnet/minecraft/nbt/NBTTagCompound;)V"), asmblocks.get("writeChunkToNBT")));

		transformer.add(new MethodReplacer(new ObfMapping("net/minecraft/block/Block", "func_149671_p", "()V"), asmblocks.get("old_registerBlocks"), asmblocks.get("registerBlocks")));
		
		if(FMLLaunchHandler.side().isClient()) {
			transformer.add(new MethodWriter(Opcodes.ACC_PUBLIC, new ObfMapping("net/minecraft/world/chunk/Chunk", "func_177439_a", "([BIZ)V"), asmblocks.get("readChunkFromPacket")));
			
			transformer.add(new MethodReplacer(new ObfMapping("net/minecraft/client/renderer/RenderGlobal", "func_180439_a", "(Lnet/minecraft/entity/player/EntityPlayer;ILnet/minecraft/util/BlockPos;I)V"), asmblocks.get("old_playAusSFX_1"), asmblocks.get("playAusSFX_1")));
			transformer.add(new MethodReplacer(new ObfMapping("net/minecraft/client/renderer/RenderGlobal", "func_180439_a", "(Lnet/minecraft/entity/player/EntityPlayer;ILnet/minecraft/util/BlockPos;I)V"), asmblocks.get("old_playAusSFX_2"), asmblocks.get("playAusSFX_2")));
		}
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
