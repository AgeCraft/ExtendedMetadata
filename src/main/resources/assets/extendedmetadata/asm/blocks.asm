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
list old_readChunkFromNBT
ALOAD 11
LDC "Blocks"
INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.getByteArray (Ljava/lang/String;)[B
ASTORE 14
   L20
ALOAD 11
LDC "Add"
INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.hasKey (Ljava/lang/String;)Z
IFEQ L21
ALOAD 11
LDC "BlocksExt"
INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.hasKey (Ljava/lang/String;)Z
IFNE L21
   L22
NEW net/minecraft/world/chunk/NibbleArray
DUP
ALOAD 11
LDC "Add"
INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.getByteArray (Ljava/lang/String;)[B
INVOKESPECIAL net/minecraft/world/chunk/NibbleArray.<init> ([B)V
ASTORE 15
   L23
ALOAD 14
ARRAYLENGTH
NEWARRAY T_BYTE
ASTORE 16
   L24
ICONST_0
ISTORE 17
   L25
GOTO L26
   L27
   FRAME FULL [net/minecraft/world/chunk/storage/AnvilChunkLoader net/minecraft/world/World net/minecraft/nbt/NBTTagCompound I I net/minecraft/world/chunk/Chunk net/minecraft/nbt/NBTTagList I [Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage; I I net/minecraft/nbt/NBTTagCompound I net/minecraft/world/chunk/storage/ExtendedBlockStorage [B net/minecraft/world/chunk/NibbleArray [B I] []
ILOAD 17
BIPUSH 15
IAND
ISTORE 18
   L28
ILOAD 17
BIPUSH 8
ISHR
BIPUSH 15
IAND
ISTORE 19
   L29
ILOAD 17
ICONST_4
ISHR
BIPUSH 15
IAND
ISTORE 20
   L30
ALOAD 16
ILOAD 17
ALOAD 15
ILOAD 18
ILOAD 19
ILOAD 20
INVOKEVIRTUAL net/minecraft/world/chunk/NibbleArray.get (III)I
I2B
BASTORE
   L31
IINC 17 1
   L26
   FRAME SAME
ILOAD 17
ALOAD 16
ARRAYLENGTH
IF_ICMPLT L27
   L32
ALOAD 11
LDC "Add"
INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.removeTag (Ljava/lang/String;)V
   L33
ALOAD 11
LDC "BlocksExt"
ALOAD 16
INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.setByteArray (Ljava/lang/String;[B)V
   L21
   FRAME CHOP 3
ALOAD 11
LDC "BlocksExt"
INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.hasKey (Ljava/lang/String;)Z
IFEQ L34
ALOAD 11
LDC "BlocksExt"
INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.getByteArray (Ljava/lang/String;)[B
GOTO L35
   L34
   FRAME SAME
ACONST_NULL
   L35
   FRAME SAME1 [B
ASTORE 15
   L36
ALOAD 11
LDC "Data"
INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.hasKey (Ljava/lang/String;)Z
IFEQ L37
ALOAD 11
LDC "Meta"
INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.hasKey (Ljava/lang/String;)Z
IFNE L37
   L38
NEW net/minecraft/world/chunk/NibbleArray
DUP
ALOAD 11
LDC "Data"
INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.getByteArray (Ljava/lang/String;)[B
INVOKESPECIAL net/minecraft/world/chunk/NibbleArray.<init> ([B)V
ASTORE 16
   L39
ALOAD 14
ARRAYLENGTH
NEWARRAY T_BYTE
ASTORE 17
   L40
ICONST_0
ISTORE 18
   L41
GOTO L42
   L43
   FRAME FULL [net/minecraft/world/chunk/storage/AnvilChunkLoader net/minecraft/world/World net/minecraft/nbt/NBTTagCompound I I net/minecraft/world/chunk/Chunk net/minecraft/nbt/NBTTagList I [Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage; I I net/minecraft/nbt/NBTTagCompound I net/minecraft/world/chunk/storage/ExtendedBlockStorage [B [B net/minecraft/world/chunk/NibbleArray [B I] []
ILOAD 18
BIPUSH 15
IAND
ISTORE 19
   L44
ILOAD 18
BIPUSH 8
ISHR
BIPUSH 15
IAND
ISTORE 20
   L45
ILOAD 18
ICONST_4
ISHR
BIPUSH 15
IAND
ISTORE 21
   L46
ALOAD 17
ILOAD 18
ALOAD 16
ILOAD 19
ILOAD 20
ILOAD 21
INVOKEVIRTUAL net/minecraft/world/chunk/NibbleArray.get (III)I
I2B
BASTORE
   L47
IINC 18 1
   L42
   FRAME SAME
ILOAD 18
ALOAD 17
ARRAYLENGTH
IF_ICMPLT L43
   L48
ALOAD 11
LDC "Data"
INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.removeTag (Ljava/lang/String;)V
   L49
ALOAD 11
LDC "Meta"
ALOAD 17
INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.setByteArray (Ljava/lang/String;[B)V
   L37
   FRAME CHOP 3
ALOAD 11
LDC "Meta"
INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.getByteArray (Ljava/lang/String;)[B
ASTORE 16
   L50
ALOAD 11
LDC "MetaExt"
INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.hasKey (Ljava/lang/String;)Z
IFEQ L51
ALOAD 11
LDC "MetaExt"
INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.getByteArray (Ljava/lang/String;)[B
GOTO L52
   L51
   FRAME APPEND [[B]
ACONST_NULL
   L52
   FRAME SAME1 [B
ASTORE 17
   L53
ALOAD 14
ARRAYLENGTH
NEWARRAY T_INT
ASTORE 18
   L54
ICONST_0
ISTORE 19
   L55
GOTO L56
   L57
   FRAME APPEND [[B [I I]
ALOAD 15
IFNULL L58
ALOAD 15
ILOAD 19
BALOAD
GOTO L59
   L58
   FRAME SAME
ICONST_0
   L59
   FRAME SAME1 I
ISTORE 20
   L60
ALOAD 17
IFNULL L61
ALOAD 17
ILOAD 19
BALOAD
GOTO L62
   L61
   FRAME APPEND [I]
ICONST_0
   L62
   FRAME SAME1 I
ISTORE 21
   L63
ALOAD 18
ILOAD 19
ILOAD 20
BIPUSH 127
IAND
BIPUSH 24
ISHL
ALOAD 14
ILOAD 19
BALOAD
SIPUSH 255
IAND
BIPUSH 16
ISHL
IOR
ILOAD 21
SIPUSH 255
IAND
BIPUSH 8
ISHL
IOR
ALOAD 16
ILOAD 19
BALOAD
SIPUSH 255
IAND
IOR
IASTORE
   L64
IINC 19 1
   L56
   FRAME CHOP 1
ILOAD 19
ALOAD 18
ARRAYLENGTH
IF_ICMPLT L57
   L65
ALOAD 13
ALOAD 18
INVOKEVIRTUAL net/minecraft/world/chunk/storage/ExtendedBlockStorage.setData ([I)V

list readChunkFromNBT

