package org.agecraft.extendedmetadata;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class ExtendedMetadata {
	
	public static Method getData;
	public static Method setData;
	
	public static void init() {
		try {
			getData = ExtendedBlockStorage.class.getDeclaredMethod("getData");
			setData = ExtendedBlockStorage.class.getDeclaredMethod("setData", int[].class);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static int getIDFromState(IBlockState state) {
		return getIDFromState(Block.getIdFromBlock(state.getBlock()), state);
	}

	public static int getIDFromState(int blockID, IBlockState state) {
		return ((blockID & 32767) << 16) | (state.getBlock().getMetaFromState(state) & 65535);
	}

	public static IBlockState getStateFromID(int id) {
		return Block.getBlockById((id >> 16) & 32767).getStateFromMeta(id & 65535);
	}

	public static Chunk readChunkFromNBT(World world, NBTTagCompound nbt) {
		int i = nbt.getInteger("xPos");
		int j = nbt.getInteger("zPos");
		Chunk chunk = new Chunk(world, i, j);
		chunk.setHeightMap(nbt.getIntArray("HeightMap"));
		chunk.setTerrainPopulated(nbt.getBoolean("TerrainPopulated"));
		chunk.setLightPopulated(nbt.getBoolean("LightPopulated"));
		chunk.setInhabitedTime(nbt.getLong("InhabitedTime"));
		NBTTagList nbttaglist = nbt.getTagList("Sections", 10);
		byte b0 = 16;
		ExtendedBlockStorage[] aextendedblockstorage = new ExtendedBlockStorage[b0];
		boolean flag = !world.provider.getHasNoSky();

		for(int k = 0; k < nbttaglist.tagCount(); ++k) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(k);
			byte b1 = nbttagcompound1.getByte("Y");
			ExtendedBlockStorage extendedblockstorage = new ExtendedBlockStorage(b1 << 4, flag);
			byte[] arrayBlock = nbttagcompound1.getByteArray("Blocks");
			if(nbttagcompound1.hasKey("Add") && !nbttagcompound1.hasKey("BlocksExt")) {
				NibbleArray data = new NibbleArray(nbttagcompound1.getByteArray("Add"));
				byte[] array = new byte[arrayBlock.length];
				for(int l = 0; l < array.length; ++l) {
					int x = l & 15;
					int y = l >> 8 & 15;
					int z = l >> 4 & 15;
					array[l] = (byte) data.get(x, y, z);
				}
				nbttagcompound1.removeTag("Add");
				nbttagcompound1.setByteArray("BlocksExt", array);
			}
			byte[] arrayBlockExt = nbttagcompound1.hasKey("BlocksExt") ? nbttagcompound1.getByteArray("BlocksExt") : null;
			if(nbttagcompound1.hasKey("Data") && !nbttagcompound1.hasKey("Meta")) {
				NibbleArray data = new NibbleArray(nbttagcompound1.getByteArray("Data"));
				byte[] array = new byte[arrayBlock.length];
				for(int l = 0; l < array.length; ++l) {
					int x = l & 15;
					int y = l >> 8 & 15;
					int z = l >> 4 & 15;
					array[l] = (byte) data.get(x, y, z);
				}
				nbttagcompound1.removeTag("Data");
				nbttagcompound1.setByteArray("Meta", array);
			}
			byte[] arrayMeta = nbttagcompound1.getByteArray("Meta");
			byte[] arrayMetaExt = nbttagcompound1.hasKey("MetaExt") ? nbttagcompound1.getByteArray("MetaExt") : null;
			int[] aint = new int[arrayBlock.length];

			for(int l = 0; l < aint.length; ++l) {
				int blockExt = arrayBlockExt != null ? arrayBlockExt[l] : 0;
				int metaExt = arrayMetaExt != null ? arrayMetaExt[l] : 0;
				aint[l] = ((blockExt & 127) << 24 | (arrayBlock[l] & 255) << 16 | (metaExt & 255) << 8 | (arrayMeta[l] & 255));
			}
			
			// extendedblockstorage.setData(aint);
			try {
				setData.invoke(extendedblockstorage, aint);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			extendedblockstorage.setBlocklightArray(new NibbleArray(nbttagcompound1.getByteArray("BlockLight")));
			if(flag) {
				extendedblockstorage.setSkylightArray(new NibbleArray(nbttagcompound1.getByteArray("SkyLight")));
			}

			extendedblockstorage.removeInvalidBlocks();
			aextendedblockstorage[b1] = extendedblockstorage;
		}
		chunk.setStorageArrays(aextendedblockstorage);

		if(nbt.hasKey("Biomes", 7)) {
			chunk.setBiomeArray(nbt.getByteArray("Biomes"));
		}
		return chunk;
	}

	public static void writeChunkToNBT(Chunk chunk, World world, NBTTagCompound nbt) {
		nbt.setByte("V", (byte) 1);
		nbt.setInteger("xPos", chunk.xPosition);
		nbt.setInteger("zPos", chunk.zPosition);
		nbt.setLong("LastUpdate", world.getTotalWorldTime());
		nbt.setIntArray("HeightMap", chunk.getHeightMap());
		nbt.setBoolean("TerrainPopulated", chunk.isTerrainPopulated());
		nbt.setBoolean("LightPopulated", chunk.isLightPopulated());
		nbt.setLong("InhabitedTime", chunk.getInhabitedTime());
		ExtendedBlockStorage[] aextendedblockstorage = chunk.getBlockStorageArray();
		NBTTagList nbttaglist = new NBTTagList();
		boolean flag = !world.provider.getHasNoSky();
		ExtendedBlockStorage[] aextendedblockstorage1 = aextendedblockstorage;
		int i = aextendedblockstorage.length;
		NBTTagCompound nbttagcompound1;

		for(int j = 0; j < i; ++j) {
			ExtendedBlockStorage extendedblockstorage = aextendedblockstorage1[j];

			if(extendedblockstorage != null) {
				nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Y", (byte) (extendedblockstorage.getYLocation() >> 4 & 255));
				int[] data = null;
				try {
					// data = extendedblockstorage.getData();
					data = (int[]) getData.invoke(extendedblockstorage);
				} catch(Exception e) {
					e.printStackTrace();
				}
				byte[] arrayBlock = new byte[data.length];
				byte[] arrayBlockExt = null;
				byte[] arrayMeta = new byte[data.length];
				byte[] arrayMetaExt = null;

				for(int k = 0; k < data.length; ++k) {
					int c0 = data[k];
					arrayBlock[k] = (byte) ((c0 >> 16) & 255);
					arrayMeta[k] = (byte) (c0 & 255);
					if(((c0 >> 24) & 127) != 0) {
						if(arrayBlockExt == null) {
							arrayBlockExt = new byte[data.length];
						}
						arrayBlockExt[k] = (byte) ((c0 >> 24) & 127);
					}
					if(((c0 >> 8) & 255) != 0) {
						if(arrayMetaExt == null) {
							arrayMetaExt = new byte[data.length];
						}
						arrayMetaExt[k] = (byte) ((c0 >> 8) & 255);
					}
				}

				nbttagcompound1.setByteArray("Blocks", arrayBlock);
				if(arrayBlockExt != null) {
					nbttagcompound1.setByteArray("BlocksExt", arrayBlockExt);
				}
				nbttagcompound1.setByteArray("Meta", arrayMeta);
				if(arrayMetaExt != null) {
					nbttagcompound1.setByteArray("MetaExt", arrayMetaExt);
				}

				nbttagcompound1.setByteArray("BlockLight", extendedblockstorage.getBlocklightArray().getData());

				if(flag) {
					nbttagcompound1.setByteArray("SkyLight", extendedblockstorage.getSkylightArray().getData());
				} else {
					nbttagcompound1.setByteArray("SkyLight", new byte[extendedblockstorage.getBlocklightArray().getData().length]);
				}

				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		nbt.setTag("Sections", nbttaglist);
		nbt.setByteArray("Biomes", chunk.getBiomeArray());
		chunk.setHasEntities(false);
		NBTTagList nbttaglist1 = new NBTTagList();
		Iterator<?> iterator;

		for(i = 0; i < chunk.getEntityLists().length; ++i) {
			iterator = chunk.getEntityLists()[i].iterator();

			while(iterator.hasNext()) {
				Entity entity = (Entity) iterator.next();
				nbttagcompound1 = new NBTTagCompound();

				try {
					if(entity.writeToNBTOptional(nbttagcompound1)) {
						chunk.setHasEntities(true);
						nbttaglist1.appendTag(nbttagcompound1);
					}
				} catch(Exception e) {
					net.minecraftforge.fml.common.FMLLog.log(org.apache.logging.log4j.Level.ERROR, e, "An Entity type %s has thrown an exception trying to write state. It will not persist. Report this to the mod author", entity.getClass().getName());
				}
			}
		}

		nbt.setTag("Entities", nbttaglist1);
		NBTTagList nbttaglist2 = new NBTTagList();
		iterator = chunk.getTileEntityMap().values().iterator();

		while(iterator.hasNext()) {
			TileEntity tileentity = (TileEntity) iterator.next();
			nbttagcompound1 = new NBTTagCompound();
			try {
				tileentity.writeToNBT(nbttagcompound1);
				nbttaglist2.appendTag(nbttagcompound1);
			} catch(Exception e) {
				net.minecraftforge.fml.common.FMLLog.log(org.apache.logging.log4j.Level.ERROR, e, "A TileEntity type %s has throw an exception trying to write state. It will not persist. Report this to the mod author", tileentity.getClass().getName());
			}
		}

		nbt.setTag("TileEntities", nbttaglist2);
		List<?> list = world.getPendingBlockUpdates(chunk, false);

		if(list != null) {
			long k1 = world.getTotalWorldTime();
			NBTTagList nbttaglist3 = new NBTTagList();
			Iterator<?> iterator1 = list.iterator();

			while(iterator1.hasNext()) {
				NextTickListEntry nextticklistentry = (NextTickListEntry) iterator1.next();
				NBTTagCompound nbttagcompound2 = new NBTTagCompound();
				ResourceLocation resourcelocation = (ResourceLocation) Block.blockRegistry.getNameForObject(nextticklistentry.getBlock());
				nbttagcompound2.setString("i", resourcelocation == null ? "" : resourcelocation.toString());
				nbttagcompound2.setInteger("x", nextticklistentry.position.getX());
				nbttagcompound2.setInteger("y", nextticklistentry.position.getY());
				nbttagcompound2.setInteger("z", nextticklistentry.position.getZ());
				nbttagcompound2.setInteger("t", (int) (nextticklistentry.scheduledTime - k1));
				nbttagcompound2.setInteger("p", nextticklistentry.priority);
				nbttaglist3.appendTag(nbttagcompound2);
			}

			nbt.setTag("TileTicks", nbttaglist3);
		}
	}
}
