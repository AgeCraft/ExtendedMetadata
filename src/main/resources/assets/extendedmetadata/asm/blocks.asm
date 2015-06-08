# net.minecraft.block.Block
list old_getStateId
ALOAD 0
INVOKEINTERFACE net/minecraft/block/state/IBlockState.getBlock ()Lnet/minecraft/block/Block;
INVOKESTATIC net/minecraft/block/Block.getIdFromBlock (Lnet/minecraft/block/Block;)I
ALOAD 0
INVOKEINTERFACE net/minecraft/block/state/IBlockState.getBlock ()Lnet/minecraft/block/Block;
ALOAD 0
INVOKEVIRTUAL net/minecraft/block/Block.getMetaFromState (Lnet/minecraft/block/state/IBlockState;)I
BIPUSH 12
ISHL
IADD
IRETURN

list getStateId
ALOAD 0
INVOKESTATIC org/agecraft/extendedmetadata/ExtendedMetadata.getIDFromState (Lnet/minecraft/block/state/IBlockState;)I
IRETURN

list old_getStateById
ILOAD 0
SIPUSH 4095
IAND
ISTORE 1
ILOAD 0
BIPUSH 12
ISHR
BIPUSH 15
IAND
ISTORE 2
ILOAD 1
INVOKESTATIC net/minecraft/block/Block.getBlockById (I)Lnet/minecraft/block/Block;
ILOAD 2
INVOKEVIRTUAL net/minecraft/block/Block.getStateFromMeta (I)Lnet/minecraft/block/state/IBlockState;
ARETURN

list getStateById
ILOAD 0
INVOKESTATIC org/agecraft/extendedmetadata/ExtendedMetadata.getStateFromID (I)Lnet/minecraft/block/state/IBlockState;
ARETURN

# net.minecraftforge.fml.common.registry.GameData
list old_registerBlock
GETSTATIC net/minecraftforge/fml/common/registry/GameData.BLOCKSTATE_TO_ID : Lnet/minecraftforge/fml/common/registry/GameData$ClearableObjectIntIdentityMap;
ALOAD 7
ILOAD 5
ICONST_4
ISHL
ALOAD 1
ALOAD 7
INVOKEVIRTUAL net/minecraft/block/Block.getMetaFromState (Lnet/minecraft/block/state/IBlockState;)I
IOR
INVOKEVIRTUAL net/minecraftforge/fml/common/registry/GameData$ClearableObjectIntIdentityMap.put (Ljava/lang/Object;I)V

list registerBlock
GETSTATIC net/minecraftforge/fml/common/registry/GameData.BLOCKSTATE_TO_ID : Lnet/minecraftforge/fml/common/registry/GameData$ClearableObjectIntIdentityMap;
ALOAD 7
ILOAD 5
ALOAD 7
INVOKESTATIC org/agecraft/extendedmetadata/ExtendedMetadata.getIDFromState (ILnet/minecraft/block/state/IBlockState;)I
INVOKEVIRTUAL net/minecraftforge/fml/common/registry/GameData$ClearableObjectIntIdentityMap.put (Ljava/lang/Object;I)V

# net.minecraft.network.play.server.S21PacketChunkData
list old_func_179756_a
CALOAD
ISTORE 14
ALOAD 5
GETFIELD net/minecraft/network/play/server/S21PacketChunkData$Extracted.data : [B
ILOAD 7
IINC 7 1
ILOAD 14
SIPUSH 255
IAND
I2B
BASTORE
ALOAD 5
GETFIELD net/minecraft/network/play/server/S21PacketChunkData$Extracted.data : [B
ILOAD 7
IINC 7 1
ILOAD 14
BIPUSH 8
ISHR
SIPUSH 255
IAND
I2B
BASTORE

list func_179756_a
IALOAD
ISTORE 14
ALOAD 5
GETFIELD net/minecraft/network/play/server/S21PacketChunkData$Extracted.data : [B
ILOAD 7
IINC 7 1
ILOAD 14
BIPUSH 16
ISHR
SIPUSH 255
IAND
I2B
BASTORE
ALOAD 5
GETFIELD net/minecraft/network/play/server/S21PacketChunkData$Extracted.data : [B
ILOAD 7
IINC 7 1
ILOAD 14
BIPUSH 24
ISHR
BIPUSH 127
IAND
I2B
BASTORE
ALOAD 5
GETFIELD net/minecraft/network/play/server/S21PacketChunkData$Extracted.data : [B
ILOAD 7
IINC 7 1
ILOAD 14
SIPUSH 255
IAND
I2B
BASTORE
ALOAD 5
GETFIELD net/minecraft/network/play/server/S21PacketChunkData$Extracted.data : [B
ILOAD 7
IINC 7 1
ILOAD 14
BIPUSH 8
ISHR
SIPUSH 255
IAND
I2B
BASTORE

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
