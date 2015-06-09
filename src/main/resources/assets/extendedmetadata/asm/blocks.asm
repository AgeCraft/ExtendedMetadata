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
#list readChunkFromPacket


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
