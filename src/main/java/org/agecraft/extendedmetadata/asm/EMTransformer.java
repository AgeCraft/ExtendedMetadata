package org.agecraft.extendedmetadata.asm;

import java.util.ListIterator;
import java.util.Map;

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
import org.objectweb.asm.tree.VarInsnNode;

import net.ilexiconn.llibrary.asm.ASMBlock;
import net.ilexiconn.llibrary.asm.ASMReader;
import net.ilexiconn.llibrary.asm.InsnComparator;
import net.ilexiconn.llibrary.asm.InsnListSection;
import net.ilexiconn.llibrary.asm.ModularASMTransformer;
import net.ilexiconn.llibrary.asm.ModularASMTransformer.ClassNodeTransformer;
import net.ilexiconn.llibrary.asm.ModularASMTransformer.FieldTypeChanger;
import net.ilexiconn.llibrary.asm.ModularASMTransformer.FieldWriter;
import net.ilexiconn.llibrary.asm.ModularASMTransformer.MethodReplacer;
import net.ilexiconn.llibrary.asm.ModularASMTransformer.MethodWriter;
import net.ilexiconn.llibrary.asm.ObfMapping;
import net.ilexiconn.llibrary.common.config.LLibraryConfigHandler;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

public class EMTransformer implements IClassTransformer {

	public static boolean ENABLE_PERFORMANCE_TWEAKS = true;
	
	private ModularASMTransformer transformer = new ModularASMTransformer();
	private Map<String, ASMBlock> asmblocks = ASMReader.loadResource("/assets/extendedmetadata/asm/blocks.asm");

	public EMTransformer() {
		// Force enable LLibrary ASM dumping
		LLibraryConfigHandler.asmDump = true;
		LLibraryConfigHandler.asmTextify = true;
		
		transformer.add(new FieldTypeChanger(new ObfMapping("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "field_177488_d", "[C"), "[I"));
		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "<init>", "(IZ)V")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				int executed = 0;
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.NEWARRAY) {
						IntInsnNode intInsn = (IntInsnNode) insn;
						if(intInsn.operand == Opcodes.T_CHAR) {
							intInsn.operand = Opcodes.T_INT;
						}
						executed++;
					} else if(insn.getOpcode() == Opcodes.PUTFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if((fieldInsn.name.equals("data") || fieldInsn.name.equals("field_177488_d") || fieldInsn.name.equals("d")) && fieldInsn.desc.equals("[C")) {
							fieldInsn.desc = "[I";
							executed++;
						}
					}
				}
				if(executed != 2) {
					throw new RuntimeException("Failed to transform ExtendedBlockStorage.<init>");
				}
			}
		});
		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "func_177485_a", "(III)Lnet/minecraft/block/state/IBlockState;")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				int executed = 0;
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.GETFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if((fieldInsn.name.equals("data") || fieldInsn.name.equals("field_177488_d") || fieldInsn.name.equals("d")) && fieldInsn.desc.equals("[C")) {
							fieldInsn.desc = "[I";
							executed++;
						}
					} else if(insn.getOpcode() == Opcodes.CALOAD) {
						methodNode.instructions.set(insn, new InsnNode(Opcodes.IALOAD));
						executed++;
					}
				}
				if(executed != 2) {
					throw new RuntimeException("Failed to transform ExtendedBlockStorage.func_177485_a");
				}
			}
		});
		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "func_177484_a", "(IIILnet/minecraft/block/state/IBlockState;)V")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				int executed = 0;
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.GETFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if((fieldInsn.name.equals("data") || fieldInsn.name.equals("field_177488_d") || fieldInsn.name.equals("d")) && fieldInsn.desc.equals("[C")) {
							fieldInsn.desc = "[I";
							executed++;
						}
					} else if(insn.getOpcode() == Opcodes.I2C) {
						methodNode.instructions.remove(insn);
						executed++;
					} else if(insn.getOpcode() == Opcodes.CASTORE) {
						methodNode.instructions.set(insn, new InsnNode(Opcodes.IASTORE));
						executed++;
					}
				}
				if(executed != 3) {
					throw new RuntimeException("Failed to transform ExtendedBlockStorage.func_177484_a");
				}
			}
		});
		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "func_177487_g", "()[C")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				boolean executed = false;
				methodNode.desc = "()[I";
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.GETFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if((fieldInsn.name.equals("data") || fieldInsn.name.equals("field_177488_d") || fieldInsn.name.equals("d")) && fieldInsn.desc.equals("[C")) {
							fieldInsn.desc = "[I";
							executed = true;
						}
					}
				}
				if(!executed) {
					throw new RuntimeException("Failed to transform ExtendedBlockStorage.func_177487_g");
				}
			}
		});
		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "func_177486_a", "([C)V")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				boolean executed = false;
				methodNode.desc = "([I)V";
				for(LocalVariableNode var : methodNode.localVariables) {
					if(var.desc.equals("[C")) {
						var.desc = "[I";
						executed = true;
					}
				}
				if(!executed) {
					throw new RuntimeException("Failed to transform ExtendedBlockStorage.func_177486_a");
				}
				
				executed = false;
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.PUTFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if((fieldInsn.name.equals("data") || fieldInsn.name.equals("field_177488_d") || fieldInsn.name.equals("d")) && fieldInsn.desc.equals("[C")) {
							fieldInsn.desc = "[I";
							executed = true;
						}
					}
				}
				if(!executed) {
					throw new RuntimeException("Failed to transform ExtendedBlockStorage.func_177486_a");
				}
			}
		});

		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/stats/StatList", "<clinit>", "()V")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				int executed = 0;
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.SIPUSH) {
						IntInsnNode intInsn = (IntInsnNode) insn;
						if(intInsn.operand == 4096) {
							methodNode.instructions.set(insn, new LdcInsnNode(new Integer(32768)));
							executed++;
						} else if(intInsn.operand == 32000) { // executed three times
							methodNode.instructions.set(insn, new LdcInsnNode(new Integer(65536)));
							executed++;
						}
					}
				}
				if(executed != 4) {
					throw new RuntimeException("Failed to transform StatList.<clinit>");
				}
			}
		});

		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/util/ObjectIntIdentityMap", "<init>", "()V")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				int executed = 0;
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.SIPUSH) {
						IntInsnNode intInsn = (IntInsnNode) insn;
						if(intInsn.operand == 512) {
							methodNode.instructions.set(insn, new LdcInsnNode(new Integer(8192)));
							executed++;
						}
					} else if(insn.getOpcode() == Opcodes.INVOKESTATIC) {
						MethodInsnNode methodInsn = (MethodInsnNode) insn;
						if(methodInsn.name.equals("newArrayList") && methodInsn.desc.equals("()Ljava/util/ArrayList;")) {
							methodInsn.name = "newArrayListWithExpectedSize";
							methodInsn.desc = "(I)Ljava/util/ArrayList;";
							methodNode.instructions.insertBefore(insn, new LdcInsnNode(new Integer(8192)));
							executed++;
						}
					}
				}
				if(executed != 2) {
					throw new RuntimeException("Failed to transform ObjectIntIdentityMap.<init>");
				}
			}
		});

		transformer.add(new FieldTypeChanger(new ObfMapping("net/minecraft/world/chunk/ChunkPrimer", "field_177860_a", "[S"), "[I"));
		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/world/chunk/ChunkPrimer", "<init>", "()V")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				int executed = 0;
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.NEWARRAY) {
						IntInsnNode intInsn = (IntInsnNode) insn;
						if(intInsn.operand == Opcodes.T_SHORT) {
							intInsn.operand = Opcodes.T_INT;
							executed++;
						}
					} else if(insn.getOpcode() == Opcodes.PUTFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if((fieldInsn.name.equals("data") || fieldInsn.name.equals("field_177860_a") || fieldInsn.name.equals("a")) && fieldInsn.desc.equals("[S")) {
							fieldInsn.desc = "[I";
							executed++;
						}
					}
				}
				if(executed != 2) {
					throw new RuntimeException("Failed to transform ChunkPrimer.<init>");
				}
			}
		});
		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/world/chunk/ChunkPrimer", "func_177858_a", "(I)Lnet/minecraft/block/state/IBlockState;")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				int executed = 0;
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.GETFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if((fieldInsn.name.equals("data") || fieldInsn.name.equals("field_177860_a") || fieldInsn.name.equals("a")) && fieldInsn.desc.equals("[S")) {
							fieldInsn.desc = "[I";
							executed++;
						}
					} else if(insn.getOpcode() == Opcodes.SALOAD) {
						methodNode.instructions.set(insn, new InsnNode(Opcodes.IALOAD));
						executed++;
					}
				}
				if(executed != 2) {
					throw new RuntimeException("Failed to transform ChunkPrimer.func_177858_a");
				}
			}
		});
		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/world/chunk/ChunkPrimer", "func_177857_a", "(ILnet/minecraft/block/state/IBlockState;)V")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				int executed = 0;
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.GETFIELD) {
						FieldInsnNode fieldInsn = (FieldInsnNode) insn;
						if((fieldInsn.name.equals("data") || fieldInsn.name.equals("field_177860_a") || fieldInsn.name.equals("a")) && fieldInsn.desc.equals("[S")) {
							fieldInsn.desc = "[I";
							executed++;
						}
					} else if(insn.getOpcode() == Opcodes.I2S) {
						methodNode.instructions.remove(insn);
						executed++;
					} else if(insn.getOpcode() == Opcodes.SASTORE) {
						methodNode.instructions.set(insn, new InsnNode(Opcodes.IASTORE));
						executed++;
					}
				}
				if(executed != 3) {
					throw new RuntimeException("Failed to transform ChunkPrimer.func_177857_a");
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
				int executed = 0;
				for(FieldNode fieldNode : node.fields) {
					if(fieldNode.desc.equals("I")) {
						if(fieldNode.name.equals("MAX_BLOCK_ID")) {
							fieldNode.value = 32767;
							executed++;
						} else if(fieldNode.name.equals("MIN_ITEM_ID")) {
							fieldNode.value = 32768;
							executed++;
						} else if(fieldNode.name.equals("MAX_ITEM_ID")) {
							fieldNode.value = 65535;
							executed++;
						}
					}
				}
				if(executed != 3) {
					throw new RuntimeException("Failed to transform GameData fields");
				}
			}
		});
		final ASMBlock needle1 = asmblocks.get("old_registerBlock");
		final ASMBlock replacement1 = asmblocks.get("registerBlock");
		transformer.add(new ClassNodeTransformer() {
			@Override
			public String className() {
				return "net.minecraftforge.fml.common.registry.GameData";
			}

			@Override
			public void transform(ClassNode node) {
				boolean executed = false;
				for(MethodNode methodNode : node.methods) {
					if(methodNode.name.equals("registerBlock") && methodNode.desc.equals("(Lnet/minecraft/block/Block;Lnet/minecraft/util/ResourceLocation;I)I")) {
						for(InsnListSection key : InsnComparator.findN(methodNode.instructions, needle1.list)) {
							ASMBlock replaceBlock = replacement1.copy().pullLabels(needle1.applyLabels(key));
							key.insert(replaceBlock.list.list);
						}
						executed = true;
					}
				}
				if(!executed) {
					throw new RuntimeException("Failed to transform GameData.registerBlock");
				}
			}
		});

		transformer.add(new MethodWriter(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, new ObfMapping("net/minecraft/block/Block", "func_176210_f", "(Lnet/minecraft/block/state/IBlockState;)I"), asmblocks.get("getStateId")));
		transformer.add(new MethodWriter(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, new ObfMapping("net/minecraft/block/Block", "func_176220_d", "(I)Lnet/minecraft/block/state/IBlockState;"), asmblocks.get("getStateById")));

		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/network/play/server/S21PacketChunkData", "func_180737_a", "(IZZ)I")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				boolean executed = false;
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.ICONST_2) {
						methodNode.instructions.set(insn, new InsnNode(Opcodes.ICONST_4));
						executed = true;
						break;
					}
				}
				if(!executed) {
					throw new RuntimeException("Failed to transform S21PacketChunkData.func_180737_a");
				}
			}
		});
		transformer.add(new MethodWriter(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, new ObfMapping("net/minecraft/network/play/server/S21PacketChunkData", "func_179756_a", "(Lnet/minecraft/world/chunk/Chunk;ZZI)Lnet/minecraft/network/play/server/S21PacketChunkData$Extracted;"), asmblocks.get("writeChunkToPacket")));

		transformer.add(new MethodWriter(Opcodes.ACC_PUBLIC, new ObfMapping("net/minecraft/world/chunk/storage/AnvilChunkLoader", "func_75823_a", "(Lnet/minecraft/world/World;Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/world/chunk/Chunk;"), asmblocks.get("readChunkFromNBT")));
		transformer.add(new MethodWriter(Opcodes.ACC_PUBLIC, new ObfMapping("net/minecraft/world/chunk/storage/AnvilChunkLoader", "func_75820_a", "(Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/world/World;Lnet/minecraft/nbt/NBTTagCompound;)V"), asmblocks.get("writeChunkToNBT")));

		transformer.add(new MethodReplacer(new ObfMapping("net/minecraft/block/Block", "func_149671_p", "()V"), asmblocks.get("old_registerBlocks"), asmblocks.get("registerBlocks")));

		transformer.add(new MethodEditor(new ObfMapping("net/minecraft/block/properties/PropertyInteger", "<init>", "(Ljava/lang/String;II)V")) {
			@Override
			public void transformMethod(ClassNode node, MethodNode methodNode) {
				boolean executed = false;
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while(iterator.hasNext()) {
					AbstractInsnNode insn = iterator.next();
					if(insn.getOpcode() == Opcodes.INVOKESTATIC) {
						MethodInsnNode methodInsn = (MethodInsnNode) insn;
						if(methodInsn.name.equals("newHashSet") && methodInsn.desc.equals("()Ljava/util/HashSet;")) {
							methodInsn.name = "newHashSetWithExpectedSize";
							methodInsn.desc = "(I)Ljava/util/HashSet;";
							methodNode.instructions.insertBefore(insn, new VarInsnNode(Opcodes.ILOAD, 3));
							executed = true;
							break;
						}
					}
				}
				if(!executed) {
					throw new RuntimeException("Failed to transform PropertyInteger.<init>");
				}
			}
		});

		if(ENABLE_PERFORMANCE_TWEAKS) {
			transformer.add(new MethodReplacer(new ObfMapping("net/minecraft/block/state/BlockState$StateImplementation", "<init>", "(Lnet/minecraft/block/Block;Lcom/google/common/collect/ImmutableMap;Lcom/google/common/collect/ImmutableTable;)V"), asmblocks.get("old_setPropertyValueTable"), asmblocks.get("setPropertyValueTable")));
			transformer.add(new MethodReplacer(new ObfMapping("net/minecraft/block/state/BlockState$StateImplementation", "func_177226_a", "(Lnet/minecraft/block/properties/IProperty;Ljava/lang/Comparable;)Lnet/minecraft/block/state/IBlockState;"), asmblocks.get("old_withProperty"), asmblocks.get("withProperty")));
			transformer.add(new MethodWriter(Opcodes.ACC_PUBLIC, new ObfMapping("net/minecraft/block/state/BlockState$StateImplementation", "func_177235_a", "(Ljava/util/Map;)V"), asmblocks.get("deprecatedMethod")));
			transformer.add(new MethodWriter(Opcodes.ACC_PUBLIC, new ObfMapping("net/minecraft/block/state/BlockState$StateImplementation", "getPropertyValueTable", "()Lcom/google/common/collect/ImmutableTable;"), asmblocks.get("deprecatedMethod")));
	
			transformer.add(new FieldWriter(Opcodes.ACC_PRIVATE, new ObfMapping("net/minecraft/block/state/BlockState", "stateMap", "Ljava/util/Map;")));
			transformer.add(new MethodEditor(new ObfMapping("net/minecraft/block/state/BlockState", "<init>", "(Lnet/minecraft/block/Block;[Lnet/minecraft/block/properties/IProperty;Lcom/google/common/collect/ImmutableMap;)V")) {
				@Override
				public void transformMethod(ClassNode node, MethodNode methodNode) {
					boolean executed = false;
					ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
					while(iterator.hasNext()) {
						AbstractInsnNode insn = iterator.next();
						if(insn.getOpcode() == Opcodes.INVOKEINTERFACE) {
							MethodInsnNode methodInsn = (MethodInsnNode) insn;
							if(methodInsn.name.equals("add") && methodInsn.desc.equals("(Ljava/lang/Object;)Z")) {
								methodNode.instructions.set(insn, new InsnNode(Opcodes.POP));
								executed = true;
								break;
							}
						}
					}
					if(!executed) {
						throw new RuntimeException("Failed to transform BlockState.<init>");
					}
				}
			});
			transformer.add(new MethodReplacer(new ObfMapping("net/minecraft/block/state/BlockState", "<init>", "(Lnet/minecraft/block/Block;[Lnet/minecraft/block/properties/IProperty;Lcom/google/common/collect/ImmutableMap;)V"), asmblocks.get("old_iteratorBuildPropertyValueTable"), asmblocks.get("iteratorBuildPropertyValueTable")));
			transformer.add(new MethodReplacer(new ObfMapping("net/minecraft/block/state/BlockState", "<init>", "(Lnet/minecraft/block/Block;[Lnet/minecraft/block/properties/IProperty;Lcom/google/common/collect/ImmutableMap;)V"), asmblocks.get("old_iteratorBuildPropertyValueTable2"), asmblocks.get("iteratorBuildPropertyValueTable2")));
			transformer.add(new MethodWriter(Opcodes.ACC_PUBLIC, new ObfMapping("net/minecraft/block/state/BlockState", "getStateMap", "()Lcom/google/common/collect/ImmutableMap;"), asmblocks.get("getStateMap")));
			
			transformer.add(new ClassNodeTransformer() {
				private Map<String, ASMBlock> asmblocks;
				
				public ClassNodeTransformer setASMBlocks(Map<String, ASMBlock> asmblocks) {
					this.asmblocks = asmblocks;
					return this;
				}
				
				@Override
				public String className() {
					return "net.minecraftforge.common.property.ExtendedBlockState$ExtendedStateImplementation";
				}

				@Override
				public void transform(ClassNode node) {
					int executed = 0;
					for(MethodNode methodNode : node.methods) {
						if(methodNode.name.equals("<init>") && methodNode.desc.equals("(Lnet/minecraft/block/Block;Lcom/google/common/collect/ImmutableMap;Lcom/google/common/collect/ImmutableMap;Lcom/google/common/collect/ImmutableTable;)V")) {
							replace(methodNode, asmblocks.get("old_setExtendedPropertyValueTable"), asmblocks.get("setExtendedPropertyValueTable"));
							executed++;
						} else if((methodNode.name.equals("func_177226_a") || methodNode.name.equals("withProperty")) && methodNode.desc.equals("(Lnet/minecraft/block/properties/IProperty;Ljava/lang/Comparable;)Lnet/minecraft/block/state/IBlockState;")) {
							replace(methodNode, asmblocks.get("old_extendedWithProperty"), asmblocks.get("extendedWithProperty"));
							replace(methodNode, asmblocks.get("old_extendedWithProperty2"), asmblocks.get("extendedWithProperty2"));
							executed++;
						} else if(methodNode.name.equals("withProperty") && methodNode.desc.equals("(Lnet/minecraftforge/common/property/IUnlistedProperty;Ljava/lang/Object;)Lnet/minecraftforge/common/property/IExtendedBlockState;")) {
							replace(methodNode, asmblocks.get("old_withUnlistedProperty"), asmblocks.get("extendedWithProperty"));
							replace(methodNode, asmblocks.get("old_withUnlistedProperty2"), asmblocks.get("withUnlistedProperty2"));
							executed++;
						} else if((methodNode.name.equals("func_177235_a") || methodNode.name.equals("buildPropertyValueTable")) && methodNode.desc.equals("(Ljava/util/Map;)V")) {
							methodNode.instructions.clear();
							if(methodNode.localVariables != null) {
								methodNode.localVariables.clear();
							}
							if(methodNode.tryCatchBlocks != null) {
								methodNode.tryCatchBlocks.clear();
							}
							asmblocks.get("deprecatedMethod").rawListCopy().accept(methodNode);
							executed++;
						}
					}
					if(executed != 4) {
						throw new RuntimeException("Failed to transform ExtendedStateImplementation");
					}
				}
				
				private void replace(MethodNode methodNode, ASMBlock needle, ASMBlock replacement) {
					for(InsnListSection key : InsnComparator.findN(methodNode.instructions, needle.list)) {
						ASMBlock replaceBlock = replacement.copy().pullLabels(needle.applyLabels(key));
						key.insert(replaceBlock.list.list);
					}
				}
			}.setASMBlocks(asmblocks));
		}
			
		if(FMLLaunchHandler.side().isClient()) {
			transformer.add(new MethodWriter(Opcodes.ACC_PUBLIC, new ObfMapping("net/minecraft/world/chunk/Chunk", "func_177439_a", "([BIZ)V"), asmblocks.get("readChunkFromPacket")));

			transformer.add(new MethodReplacer(new ObfMapping("net/minecraft/client/renderer/RenderGlobal", "func_180439_a", "(Lnet/minecraft/entity/player/EntityPlayer;ILnet/minecraft/util/BlockPos;I)V"), asmblocks.get("old_playAusSFX_1"), asmblocks.get("playAusSFX_1")));
			transformer.add(new MethodReplacer(new ObfMapping("net/minecraft/client/renderer/RenderGlobal", "func_180439_a", "(Lnet/minecraft/entity/player/EntityPlayer;ILnet/minecraft/util/BlockPos;I)V"), asmblocks.get("old_playAusSFX_2"), asmblocks.get("playAusSFX_2")));

			final ASMBlock injection = asmblocks.get("setupModelRegistry");
			transformer.add(new ClassNodeTransformer() {
				@Override
				public String className() {
					return "net.minecraftforge.client.model.ModelLoader";
				}

				@Override
				public void transform(ClassNode node) {
					boolean executed = false;
					for(MethodNode methodNode : node.methods) {
						if(methodNode.name.equals("setupModelRegistry") && methodNode.desc.equals("()Lnet/minecraft/util/IRegistry;")) {
							methodNode.instructions.insert(injection.rawListCopy());
							executed = true;
							break;
						}
					}
					if(!executed) {
						throw new RuntimeException("Failed to transformer ModeLoader.setupModelRegistry");
					}
				}
			});
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
