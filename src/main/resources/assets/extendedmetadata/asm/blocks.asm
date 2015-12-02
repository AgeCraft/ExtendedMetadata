# net.minecraft.block.Block
list getStateId
ALOAD 0
INVOKESTATIC org/agecraft/extendedmetadata/ExtendedMetadata.getIDFromState (Lnet/minecraft/block/state/IBlockState;)I
IRETURN

list getStateById
ILOAD 0
INVOKESTATIC org/agecraft/extendedmetadata/ExtendedMetadata.getStateFromID (I)Lnet/minecraft/block/state/IBlockState;
ARETURN

# net.minecraftforge.fml.common.registry.GameData
list old_onAdd
ALOAD 4
ILOAD 2
ICONST_4
ISHL
ALOAD 1
ALOAD 4
INVOKEVIRTUAL net/minecraft/block/Block.func_176201_c (Lnet/minecraft/block/state/IBlockState;)I
IOR

list onAdd
ALOAD 4
ILOAD 2
ALOAD 4
INVOKESTATIC org/agecraft/extendedmetadata/ExtendedMetadata.getIDFromState (ILnet/minecraft/block/state/IBlockState;)I

# net.minecraft.world.Chunk
list readChunkFromPacket
ALOAD 0
ALOAD 1
ILOAD 2
ILOAD 3
INVOKESTATIC org/agecraft/extendedmetadata/ExtendedMetadata.readChunkFromPacket (Lnet/minecraft/world/chunk/Chunk;[BIZ)V
RETURN

# net.minecraft.network.play.server.S21PacketChunkData
list writeChunkToPacket
ALOAD 0
ILOAD 1
ILOAD 2
ILOAD 3
INVOKESTATIC org/agecraft/extendedmetadata/ExtendedMetadata.writeChunkToPacket (Lnet/minecraft/world/chunk/Chunk;ZZI)Lnet/minecraft/network/play/server/S21PacketChunkData$Extracted;
ARETURN

# net.minecraft.world.chunk.storage.AnvilChunkLoader
list readChunkFromNBT
ALOAD 1
ALOAD 2
INVOKESTATIC org/agecraft/extendedmetadata/ExtendedMetadata.readChunkFromNBT (Lnet/minecraft/world/World;Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/world/chunk/Chunk;
ARETURN

list writeChunkToNBT
ALOAD 1
ALOAD 2
ALOAD 3
INVOKESTATIC org/agecraft/extendedmetadata/ExtendedMetadata.writeChunkToNBT (Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/world/World;Lnet/minecraft/nbt/NBTTagCompound;)V
RETURN

# net.minecraft.block.Block
list old_registerBlocks
GETSTATIC net/minecraft/block/Block.field_149771_c : Lnet/minecraft/util/RegistryNamespacedDefaultedByKey;
ALOAD 14
INVOKEVIRTUAL net/minecraft/util/RegistryNamespacedDefaultedByKey.func_148757_b (Ljava/lang/Object;)I
ICONST_4
ISHL
ALOAD 14
ALOAD 16
INVOKEVIRTUAL net/minecraft/block/Block.func_176201_c (Lnet/minecraft/block/state/IBlockState;)I
IOR
ISTORE 17

list registerBlocks
ALOAD 14
ALOAD 16
INVOKESTATIC org/agecraft/extendedmetadata/ExtendedMetadata.getIDFromState (Lnet/minecraft/block/Block;Lnet/minecraft/block/state/IBlockState;)I
ISTORE 17

# net.minecraft.client.renderer.RenderGlobal
list old_playAusSFX_1
ILOAD 4
SIPUSH 4095
IAND
INVOKESTATIC net/minecraft/block/Block.func_149729_e (I)Lnet/minecraft/block/Block;

list playAusSFX_1
ILOAD 4
BIPUSH 16
ISHR 16
LDC 32767
IAND
INVOKESTATIC net/minecraft/block/Block.func_149729_e (I)Lnet/minecraft/block/Block;

list old_playAusSFX_2
ILOAD 4
BIPUSH 12
ISHR
SIPUSH 255
IAND
INVOKEVIRTUAL net/minecraft/block/Block.func_176203_a (I)Lnet/minecraft/block/state/IBlockState;

list playAusSFX_2
ILOAD 4
LDC 65535
IAND
INVOKEVIRTUAL net/minecraft/block/Block.func_176203_a (I)Lnet/minecraft/block/state/IBlockState;

# net.minecraftforge.client.model.ModelLoader
list setupModelRegistry
ALOAD 0
INVOKESTATIC org/agecraft/extendedmetadata/client/EMModelLoader.load (Lnet/minecraftforge/client/model/ModelLoader;)V

# net.minecraft.block.state.BlockState
list old_iteratorBuildPropertyValueTable
ALOAD 6
INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object;
CHECKCAST net/minecraft/block/state/BlockState$StateImplementation
ASTORE 7
ALOAD 7
ALOAD 4
INVOKEVIRTUAL net/minecraft/block/state/BlockState$StateImplementation.buildPropertyValueTable (Ljava/util/Map;)V
GOTO L14

list old_iteratorBuildPropertyValueTable2
ALOAD 0
ALOAD 5
INVOKESTATIC com/google/common/collect/ImmutableList.copyOf (Ljava/util/Collection;)Lcom/google/common/collect/ImmutableList;
PUTFIELD net/minecraft/block/state/BlockState.validStates : Lcom/google/common/collect/ImmutableList;

list iteratorBuildPropertyValueTable
GOTO L14

list iteratorBuildPropertyValueTable2
ALOAD 0
ALOAD 4
INVOKESTATIC com/google/common/collect/ImmutableMap.copyOf (Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;
PUTFIELD net/minecraft/block/state/BlockState.stateMap : Ljava/util/Map;
ALOAD 0
ALOAD 4
INVOKEVIRTUAL java/util/LinkedHashMap.values ()Ljava/util/Collection;
INVOKESTATIC com/google/common/collect/ImmutableList.copyOf (Ljava/util/Collection;)Lcom/google/common/collect/ImmutableList;
PUTFIELD net/minecraft/block/state/BlockState.field_177625_e  : Lcom/google/common/collect/ImmutableList;

list getStateMap
ALOAD 0
GETFIELD net/minecraft/block/state/BlockState.stateMap : Ljava/util/Map;
INVOKESTATIC org/agecraft/extendedmetadata/ExtendedMetadata.getStateMap (Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;
ARETURN

# net.minecraft.block.state.BlockState$StateImplementation
list old_setPropertyValueTable
ALOAD 0
ALOAD 3
PUTFIELD net/minecraft/block/state/BlockState$StateImplementation.propertyValueTable : Lcom/google/common/collect/ImmutableTable;

list setPropertyValueTable
ALOAD 3
INVOKESTATIC org/agecraft/extendedmetadata/ExtendedMetadata.checkPropertyValueTable (Lcom/google/common/collect/ImmutableTable;)V

list deprecatedMethod
NEW org/agecraft/extendedmetadata/DeprecatedMethodException
DUP
ICONST_0
INVOKESPECIAL org/agecraft/extendedmetadata/DeprecatedMethodException.<init> (I)V
ATHROW

list old_withProperty
ALOAD 0
GETFIELD net/minecraft/block/state/BlockState$StateImplementation.propertyValueTable : Lcom/google/common/collect/ImmutableTable;
ALOAD 1
ALOAD 2
INVOKEVIRTUAL com/google/common/collect/ImmutableTable.get (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
CHECKCAST net/minecraft/block/state/IBlockState

list withProperty
ALOAD 0
GETFIELD net/minecraft/block/state/BlockState$StateImplementation.field_177239_a : Lnet/minecraft/block/Block;
INVOKEVIRTUAL net/minecraft/block/Block.func_176194_O ()Lnet/minecraft/block/state/BlockState;
INVOKEVIRTUAL net/minecraft/block/state/BlockState.getStateMap ()Lcom/google/common/collect/ImmutableMap;
ALOAD 0
ALOAD 1
ALOAD 2
INVOKESPECIAL net/minecraft/block/state/BlockState$StateImplementation.func_177236_b (Lnet/minecraft/block/properties/IProperty;Ljava/lang/Comparable;)Ljava/util/Map;
INVOKEINTERFACE java/util/Map.get (Ljava/lang/Object;)Ljava/lang/Object;
CHECKCAST net/minecraft/block/state/IBlockState

# net.minecraftforge.common.property.ExtendedBlockState$ExtendedStateImplementation
list old_setExtendedPropertyValueTable
ALOAD 0
ALOAD 4
PUTFIELD net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.propertyValueTable : Lcom/google/common/collect/ImmutableTable;

list setExtendedPropertyValueTable
ALOAD 4
INVOKESTATIC org/agecraft/extendedmetadata/ExtendedMetadata.checkPropertyValueTable (Lcom/google/common/collect/ImmutableTable;)V

list old_extendedWithProperty
ALOAD 0
GETFIELD net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.normalMap : Ljava/util/Map;
ALOAD 3
INVOKEINTERFACE java/util/Map.get (Ljava/lang/Object;)Ljava/lang/Object;
CHECKCAST net/minecraftforge/common/property/IExtendedBlockState
ARETURN

list extendedWithProperty
ALOAD 0
INVOKEVIRTUAL net/minecraft/block/state/IBlockState.func_177622_c ()Lnet/minecraft/block/Block;
INVOKEVIRTUAL net/minecraft/block/Block.func_176194_O ()Lnet/minecraft/block/state/BlockState;
INVOKEVIRTUAL net/minecraft/block/state/BlockState.getStateMap ()Lcom/google/common/collect/ImmutableMap;
ALOAD 0
INVOKEVIRTUAL net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.getProperties ()Lcom/google/common/collect/ImmutableMap;
INVOKEINTERFACE java/util/Map.get (Ljava/lang/Object;)Ljava/lang/Object;
CHECKCAST net/minecraftforge/common/property/IExtendedBlockState
ARETURN

list old_extendedWithProperty2
ALOAD 0
GETFIELD net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.propertyValueTable : Lcom/google/common/collect/ImmutableTable;
ASTORE 4
ALOAD 4
ALOAD 1
ALOAD 2
INVOKEVIRTUAL com/google/common/collect/ImmutableTable.get (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
CHECKCAST net/minecraft/block/state/BlockState$StateImplementation
INVOKEVIRTUAL net/minecraft/block/state/BlockState$StateImplementation.getPropertyValueTable ()Lcom/google/common/collect/ImmutableTable;
ASTORE 4
NEW net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation
DUP
ALOAD 0
INVOKEVIRTUAL net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.getBlock ()Lnet/minecraft/block/Block;
ALOAD 3
INVOKESTATIC com/google/common/collect/ImmutableMap.copyOf (Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;
ALOAD 0
GETFIELD net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.unlistedProperties : Lcom/google/common/collect/ImmutableMap;
ALOAD 4
INVOKESPECIAL net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.<init> (Lnet/minecraft/block/Block;Lcom/google/common/collect/ImmutableMap;Lcom/google/common/collect/ImmutableMap;Lcom/google/common/collect/ImmutableTable;)V
ALOAD 0
GETFIELD net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.normalMap : Ljava/util/Map;
INVOKESPECIAL net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.setMap (Ljava/util/Map;)Lnet/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation;
ARETURN

list extendedWithProperty2
NEW net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation
DUP
ALOAD 0
INVOKEVIRTUAL net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.getBlock ()Lnet/minecraft/block/Block;
ALOAD 3
INVOKESTATIC com/google/common/collect/ImmutableMap.copyOf (Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;
ALOAD 0
GETFIELD net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.unlistedProperties : Lcom/google/common/collect/ImmutableMap;
ACONST_NULL
INVOKESPECIAL net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.<init> (Lnet/minecraft/block/Block;Lcom/google/common/collect/ImmutableMap;Lcom/google/common/collect/ImmutableMap;Lcom/google/common/collect/ImmutableTable;)V
ARETURN

list old_withUnlistedProperty
ALOAD 0
GETFIELD net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.normalMap : Ljava/util/Map;
ALOAD 0
INVOKEVIRTUAL net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.getProperties ()Lcom/google/common/collect/ImmutableMap;
INVOKEINTERFACE java/util/Map.get (Ljava/lang/Object;)Ljava/lang/Object;
CHECKCAST net/minecraftforge/common/property/IExtendedBlockState
ARETURN

list old_withUnlistedProperty2
NEW net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation
DUP
ALOAD 0
INVOKEVIRTUAL net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.getBlock ()Lnet/minecraft/block/Block;
ALOAD 0
INVOKEVIRTUAL net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.getProperties ()Lcom/google/common/collect/ImmutableMap;
ALOAD 3
INVOKESTATIC com/google/common/collect/ImmutableMap.copyOf (Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;
ALOAD 0
GETFIELD net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.propertyValueTable : Lcom/google/common/collect/ImmutableTable;
INVOKESPECIAL net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.<init> (Lnet/minecraft/block/Block;Lcom/google/common/collect/ImmutableMap;Lcom/google/common/collect/ImmutableMap;Lcom/google/common/collect/ImmutableTable;)V
ALOAD 0
GETFIELD net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.normalMap : Ljava/util/Map;
INVOKESPECIAL net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.setMap (Ljava/util/Map;)Lnet/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation;
ARETURN

list withUnlistedProperty2
NEW net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation
DUP
ALOAD 0
INVOKEVIRTUAL net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.getBlock ()Lnet/minecraft/block/Block;
ALOAD 0
INVOKEVIRTUAL net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.getProperties ()Lcom/google/common/collect/ImmutableMap;
ALOAD 3
INVOKESTATIC com/google/common/collect/ImmutableMap.copyOf (Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;
ACONST_NULL
INVOKESPECIAL net/minecraftforge/common/property/ExtendedBlockState$ExtendedStateImplementation.<init> (Lnet/minecraft/block/Block;Lcom/google/common/collect/ImmutableMap;Lcom/google/common/collect/ImmutableMap;Lcom/google/common/collect/ImmutableTable;)V
ARETURN
