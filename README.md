# ExtendedMetadata

A Forge core mod that increases the block ID and metadata limit.
Vanilla Minecraft uses 12 bits for block IDs and 4 bits for metadata, so combined it can be stored in a 16-bit short/character.
ExtendedMetadata replaces the short/character stores with integers, this extends block IDs to 15 bit numbers and metadata to 16 bit numbers. This increases the block ID possibilities from 4096 to 32768 and block metadata possibilities from 16 to 65536.

Ideally we would also want 16 bit block IDs, but that's not possible due to the way Java handles integers. Java 7 and below can only handle signed integer, with a range from 2^-31 to 2^31. There are two solutions for this:
* Force Java 8 as a dependency, which can handle unsigned integers. 
* Use longs to store the block states, but this would increase storage and network usage an awful lot and it would be way to much work to change everything from integers to longs.
So that's why this mod only fully increases block metadata and not block IDs, because metadata is more useful than block IDs.

For those that are curious how this mod was made: I forked Minecraft Forge and setup the contributor environment (https://github.com/MinecraftForge/MinecraftForge/wiki/If-you-want-to-contribute-to-Forge) and started looking through the decompiled Minecraft code to get an idea of the internals of block states, chunk storage and chunk networking. While I was making changes to the source code I wrote down all the fields and/or methods I changed. This simplified making the coremod because I already knew where to change what, all that was left todo was search the corresponding bytecode and setup the ASM transformations for it. This is quite a tedious process, but CodeChickenLib/CodeChickenCore and the Bytecode Outline plugin for Eclipse (http://marketplace.eclipse.org/content/bytecode-outline) helped a lot.

## Warning
This mod changes the way your world is stored on disk. Old worlds get converted automatically, but you can't open worlds touched by ExtendedMetadata using vanilla Minecraft or Forge.
ExtendedMetadata also increases the amount of disk storage and network bandwidth being used. This in can cause lag, you have been warned.
Also be careful with the 1.8 model / texture loader, using too much metadata can cause out of memory errors. The solution for this is a custom model or texture loader, we have implemented this, but it might take some time to perfect.

## Blockstate / Model Loader
ExtendedMetadata also adds an improved blockstate / model loader, example: 
* Implementation: https://github.com/AgeCraft/ExtendedMetadata/blob/1.8/src/test/java/org/agecraft/extendedmetadata/test/ExtendedMetadataTest.java
* Blockstate JSON: https://github.com/AgeCraft/ExtendedMetadata/blob/1.8/src/test/resources/assets/extendedmetadatatest/blockstates/extended_metadata.json

### Format
Here you can find the Forge format for reference: http://mcforge.readthedocs.org/en/latest/blockstates/forgeBlockstates/.

Basically there are three parts to the blockstate JSON file: `defaults`, `variants` and `customVariants`.
In defaults you can specify defaults exactly as you would in the Forge format.

In variants you have three ways of declaring your block states:
* Fully qualified property string (for example: `half=false,value=0`)
* A single property with all of it's values below (for example: `half`)
* One or more properties with variables (for example: `value={0}` or `half=false,value={0]` or `half={1},value={1}`). Note that a variable (a number between `{}`) is bound to a property, it can be used in multiple places as long as the property name stays the same. 

In the JSON objects assigned to these keys you can specify variants just like you would in the Forge format.

In the last part, custom variants, you can define custom states, which are saved as it's JSON key. These are useful to define custom item models for a block.

#### Complete example
```json
{
	"defaults": {
		"transform": "forge:default-block"
	},
	"variants": {
		// Property with a variable
		"value={0}": {
			"textures": {
				"all": "extendedmetadatatest:blocks/extended_metadata_{0}"
			}
		},
		// Single property with it's values below
		"half": {
			"false": {
				"model": "minecraft:cube_all"
			},
			"true": {
				"model": "minecraft:half_slab",
				"textures": {
					"side": "#all",
					"top": "#all",
					"bottom": "#all",
					"particle": "#all"
				}
			}
		}
	},
	"customVariants": {
		"inventory14": {
			"model": "minecraft:stairs",
			"textures": {
				"all": "extendedmetadatatest:blocks/extended_metadata_7",
				"side": "#all",
				"top": "#all",
				"bottom": "#all"
			}
		}
	}
}
```

### Usage
If you want to use the new format for a block you first need to register it with ExtendedMetdata, like so:
```java
// During pre-initialization in your client proxy
EMModelLoader.registerBlock(block);
```
To also load the block models as inventory / item models, register them like this: (you probably want to do this unless the inventory model is supposed to be different from the block model)
```java
// During pre-initialization in your client proxy
EMModelLoader.registerBlockItemModels(block);
```
Forge will probably give a warning about <block name>#inventory not being found, but you can ignore it.
But if you want to use custom models you can specify them in your blockstate json file under the `customVariants` tag and register them with Forge:
```java
// During pre-initialization in your client proxy
// Replace 14 with your metadata value
// Replace "inventory14" with the key of the custom variant in your blockstate json
EMModelLoader.registerBlockItemModel(block, 14, "inventory14");
```

## Dependencies
* [Minecraft Forge](http://minecraftforge.net) 11.14.3.1487 or higher
* [CodeChickenCore](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1279956-chickenbones-mods) 1.0.5.34 or higher
* [CodeChickenLib](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1279956-chickenbones-mods) 1.1.2.133 or higher

## Installation
1. [Download](http://files.minecraftforge.net) and install Minecraft Forge
2. [Download](http://chickenbones.net/Pages/links.html) and install CodeChickenCore
3. Download ExtendedMetadata and put the file in the mods folder

## Changes
The changes made by the ASM transformer are listed below. Useful in the future as reference for updating to another Minecraft version or updating Forge changes.

#### net.minecraft.world.chunk.storage.ExtendedBlockStorage
##### Field: `data`
Change type from `char[]` to `int[]`. Affected methods: 
* `void <init>(int, boolean)`
* `IBlockState get(int, int, int)`
* `IBlockState set(int, int, int)`
* `char[] getData()`
* `void setData(char[])`

##### Method: `char[] getData()`
Change return type from `char[]` to `int[]`.

##### Method: `void setData(char[])`
Change parameter type from `char[]` to `int[]`.

#### net.minecraft.world.chunk.storage.AnvilChunkLoader
##### Method: `Chunk readChunkFromNBT(World, NBTTagCompound)`
Replaced by `Chunk ExtendedMetadata.readChunkFromNBT(World, NBTTagCompound)` to change block state storage from 2 bytes to 4 bytes.

##### Method: `void writeChunkToNBT(Chunk, World, NBTTagCompound)`
Replaced by `void ExtendedMetadata.writeChunkToNBT(Chunk, World, NBTTagCompound)` to change block state storage from 2 bytes to 4 bytes.

#### net.minecraft.block.Block
##### Method: `int getStateId(IBlockState)`
Replaced by `int ExtendedMetadata.getIDFromState(IBlockState)` to change how block states are formed.

##### Method: `IBlockState getStateById(int)`
Replaced by `IBlockState ExtendedMetadata.getStateFromID(int)` to change how block states are formed.

##### Method: `void registerBlocks()`
The last lines are replaced by `int ExtendedMetadata.getIDFromState(Block, IBlockState)` to change how block states are formed.

#### net.minecraft.network.play.server.S21PacketChunkData
##### Method: `int func_180737_a(int, boolean, boolean)`
Changed the calculation of the byte array size to support 4 bytes for block states

##### Method: `S21PacketChunkData.Extracted func_179756_a(Chunk, boolean, boolean, int)`
Replaced by `S21PacketChunkData.Extracted ExtendedMetadata.writeChunkToPacket(Chunk, boolean, boolean, int)` to change how the packet handles block states. Writes 4 bytes instead of 2 bytes per block state.

#### net.minecraft.client.renderer.RenderGlobal
##### Method: `void playAusSFX(EntityPlayer, int, BlockPos, int)` *CLIENT SIDE ONLY*
Changed reading block ID and metadata from the block state

#### net.minecraft.world.chunk.Chunk
##### Method: `void fillChunk(byte[], boolean, int)` *CLIENT SIDE ONLY*
Replaced by `void ExtendedMetadata.writeChunkToPacket(Chunk, byte[], boolean, int)` to change how the packet handles block states. Writes 4 bytes instead of 2 bytes per block state.

#### net.minecraft.world.chunk.ChunkPrimer
##### Field: `data`
Change type from `short[]` to `int[]`. Affected methods:
* `void <init>()`
* `IBlockState getBlockState(int)`
* `void setBlockState(int, IBlockState)`

#### net.minecarft.stats.StatList
##### Field: `mineBlockStatArray`
Change array size from `4096` to `32768`.

##### Field: `objectCraftStats`
Change array size from `32000` to `65536`

##### Field: `objectUseStats`
Change array size from `32000` to `65536`

##### Field: `objectBreakStats`
Change array size from `32000` to `65536`

#### net.minecraft.util.ObjectIntIdentityMap
##### Field: `identityMap`
Changed initial size from `512` to `8192`.
The new size is quite random, but should prevent frequent resizing.

##### Field: `objectList`
Changed initial size from nothing to `8192`.
The new size is quite random, but should prevent frequent resizing.

#### net.minecraftforge.fml.common.registry.GameData
##### Field: `MAX_BLOCK_ID`
Change value from `4095` to `32767`

##### Field: `MIN_ITEM_ID`
Change value from `4096` to `32768`

##### Field: `MAX_ITEM_ID`
Change value from `31999` to `65535`

##### Method: `int registerBlock(Block, String, int)`
The last lines are replaced by `int ExtendedMetadata.getIDFromState(int, IBlockState)` to change how block states are formed.

#### net.minecraftforge.client.model.ModeLoader
##### Method: `IRegistry setupModelRegistry()` *CLIENT SIDE ONLY*
Insert hook to `void load(ModelLoader)` to load blocks that use the custom block state format
