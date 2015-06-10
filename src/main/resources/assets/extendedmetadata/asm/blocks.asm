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
GETSTATIC net/minecraft/block/Block.blockRegistry : Lnet/minecraft/util/RegistryNamespacedDefaultedByKey;
ALOAD 14
INVOKEVIRTUAL net/minecraft/util/RegistryNamespacedDefaultedByKey.getIDForObject (Ljava/lang/Object;)I
ICONST_4
ISHL
ALOAD 14
ALOAD 16
INVOKEVIRTUAL net/minecraft/block/Block.getMetaFromState (Lnet/minecraft/block/state/IBlockState;)I
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
INVOKESTATIC net/minecraft/block/Block.getBlockById (I)Lnet/minecraft/block/Block;

list playAusSFX_1
ILOAD 4
BIPUSH 16
ISHR 16
LDC 32767
IAND
INVOKESTATIC net/minecraft/block/Block.getBlockById (I)Lnet/minecraft/block/Block;

list old_playAusSFX_2
ILOAD 4
BIPUSH 12
ISHR
SIPUSH 255
IAND
INVOKEVIRTUAL net/minecraft/block/Block.getStateFromMeta (I)Lnet/minecraft/block/state/IBlockState;

list playAusSFX_2
ILOAD 4
LDC 65535
IAND
INVOKEVIRTUAL net/minecraft/block/Block.getStateFromMeta (I)Lnet/minecraft/block/state/IBlockState;
