package org.agecraft.extendedmetadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState.StateImplementation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;

public class ExtendedMetadata {

	public static Method getData;
	public static Method setData;

	public static Field chunkWorldObj;
	public static Field chunkStorageArrays;
	public static Field chunkTileEntityMap;
	public static Field chunkBlockBiomeArray;
	public static Field chunkIsLightPopulated;
	public static Field chunkIsTerrainPopulated;
	public static Method chunkGenerateHeightMap;

	public static Method func_180737_a;
	public static Method func_179757_a;

	public static Field propertyValueTable;
	public static Method setPropertyValue;

	public static Logger log = LogManager.getLogger("ExtendedMetadata");

	public static void init() {
		try {
			getData = EMUtil.getMethod(ExtendedBlockStorage.class, "getData", "func_177487_g", "g");
			setData = EMUtil.getMethod(ExtendedBlockStorage.class, "setData", "func_177486_a", "a", int[].class);

			chunkWorldObj = EMUtil.getField(Chunk.class, "worldObj", "field_76637_e", "i");
			chunkStorageArrays = EMUtil.getField(Chunk.class, "storageArrays", "field_76652_q", "d");
			chunkTileEntityMap = EMUtil.getField(Chunk.class, "chunkTileEntityMap", "field_150816_i", "l");
			chunkBlockBiomeArray = EMUtil.getField(Chunk.class, "blockBiomeArray", "field_76651_r", "e");
			chunkIsLightPopulated = EMUtil.getField(Chunk.class, "isLightPopulated", "field_150814_l", "o");
			chunkIsTerrainPopulated = EMUtil.getField(Chunk.class, "isTerrainPopulated", "field_76646_k", "n");
			if(FMLLaunchHandler.side().isClient()) {
				chunkGenerateHeightMap = EMUtil.getMethod(Chunk.class, "generateHeightMap", "func_76590_a", "a");
			}

			func_180737_a = EMUtil.getMethod(S21PacketChunkData.class, "func_180737_a", "func_180737_a", "a", int.class, boolean.class, boolean.class);
			func_179757_a = EMUtil.getMethod(S21PacketChunkData.class, "func_179757_a", "func_179757_a", "a", byte[].class, byte[].class, int.class);
		} catch(Exception e) {
			throw new RuntimeException("Error while initializing ExtendedMetadata: ", e);
		}
	}

	public static int getIDFromState(IBlockState state) {
		return getIDFromState(Block.getIdFromBlock(state.getBlock()), state);
	}

	public static int getIDFromState(Block block, IBlockState state) {
		return getIDFromState(Block.getIdFromBlock(block), state);
	}

	public static int getIDFromState(int blockID, IBlockState state) {
		return ((blockID & 32767) << 16) | (state.getBlock().getMetaFromState(state) & 65535);
	}

	public static IBlockState getStateFromID(int id) {
		return Block.getBlockById((id >> 16) & 32767).getStateFromMeta(id & 65535);
	}

	public static void buildPropertyValueTable(StateImplementation state, ImmutableMap<IProperty, Object> properties, Map<Map<IProperty, Comparable>, IBlockState> map) {
		if(propertyValueTable == null) {
			try {
				propertyValueTable = EMUtil.getField(StateImplementation.class, "propertyValueTable", "field_177238_c", "c");
				setPropertyValue = EMUtil.getMethod(StateImplementation.class, "setPropertyValue", "func_177236_b", "b", IProperty.class, Comparable.class);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		try {
			if(propertyValueTable.get(state) != null) {
				throw new IllegalStateException();
			} else {
				HashBasedTable<IProperty, Comparable, IBlockState> table = HashBasedTable.create();
				for(IProperty property : properties.keySet()) {
					for(Comparable comparable : (Collection<Comparable>) property.getAllowedValues()) {
						if(comparable != properties.get(property)) {
							table.put(property, comparable, map.get(setPropertyValue.invoke(state, property, comparable)));
						}
					}
				}
				propertyValueTable.set(state, ImmutableTable.copyOf(table));
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
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
			int[] data = new int[arrayBlock.length];

			for(int l = 0; l < data.length; ++l) {
				int blockExt = arrayBlockExt != null ? arrayBlockExt[l] : 0;
				int metaExt = arrayMetaExt != null ? arrayMetaExt[l] : 0;
				data[l] = ((blockExt & 127) << 24 | (arrayBlock[l] & 255) << 16 | (metaExt & 255) << 8 | (arrayMeta[l] & 255));
			}
			try {
				setData.invoke(extendedblockstorage, data);
			} catch(Exception e) {
				throw new RuntimeException(e);
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
		NBTTagCompound nbttagcompound1;

		for(int j = 0; j < aextendedblockstorage.length; ++j) {
			ExtendedBlockStorage extendedblockstorage = aextendedblockstorage1[j];

			if(extendedblockstorage != null) {
				nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Y", (byte) (extendedblockstorage.getYLocation() >> 4 & 255));
				int[] data = null;
				try {
					data = (int[]) getData.invoke(extendedblockstorage);
				} catch(Exception e) {
					throw new RuntimeException(e);
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

		for(int i = 0; i < chunk.getEntityLists().length; ++i) {
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

	@SideOnly(Side.CLIENT)
	public static void readChunkFromPacket(Chunk chunk, byte[] data, int chunks, boolean sendBiomes) {
		World worldObj = null;
		ExtendedBlockStorage[] storageArrays = null;
		Map<?, ?> tileEntityMap = null;
		byte[] blockBiomeArray = null;
		try {
			worldObj = (World) chunkWorldObj.get(chunk);
			storageArrays = (ExtendedBlockStorage[]) chunkStorageArrays.get(chunk);
			tileEntityMap = (Map<?, ?>) chunkTileEntityMap.get(chunk);
			blockBiomeArray = (byte[]) chunkBlockBiomeArray.get(chunk);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		Iterator<?> iterator = tileEntityMap.values().iterator();
		while(iterator.hasNext()) {
			TileEntity tileEntity = (TileEntity) iterator.next();
			tileEntity.updateContainingBlockInfo();
			tileEntity.getBlockMetadata();
			tileEntity.getBlockType();
		}
		int j = 0;
		boolean flag1 = !worldObj.provider.getHasNoSky();
		for(int i = 0; i < storageArrays.length; ++i) {
			if((chunks & 1 << i) != 0) {
				if(storageArrays[i] == null) {
					storageArrays[i] = new ExtendedBlockStorage(i << 4, flag1);
				}
				int[] aint = null;
				try {
					aint = (int[]) getData.invoke(storageArrays[i]);
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
				for(int l = 0; l < aint.length; ++l) {
					aint[l] = (((data[j + 1] & 127) << 24) | ((data[j] & 255) << 16) | ((data[j + 3] & 255) << 8) | (data[j + 2] & 255));
					j += 4;
				}
			} else if(sendBiomes && storageArrays[i] != null) {
				storageArrays[i] = null;
			}
		}
		NibbleArray nibblearray;
		for(int i = 0; i < storageArrays.length; ++i) {
			if((chunks & 1 << i) != 0 && storageArrays[i] != null) {
				nibblearray = storageArrays[i].getBlocklightArray();
				System.arraycopy(data, j, nibblearray.getData(), 0, nibblearray.getData().length);
				j += nibblearray.getData().length;
			}
		}
		if(flag1) {
			for(int i = 0; i < storageArrays.length; ++i) {
				if((chunks & 1 << i) != 0 && storageArrays[i] != null) {
					nibblearray = storageArrays[i].getSkylightArray();
					System.arraycopy(data, j, nibblearray.getData(), 0, nibblearray.getData().length);
					j += nibblearray.getData().length;
				}
			}
		}
		if(sendBiomes) {
			System.arraycopy(data, j, blockBiomeArray, 0, blockBiomeArray.length);
			j += blockBiomeArray.length;
		}
		for(int i = 0; i < storageArrays.length; ++i) {
			if(storageArrays[i] != null && (chunks & 1 << i) != 0) {
				storageArrays[i].removeInvalidBlocks();
			}
		}
		try {
			chunkIsLightPopulated.setBoolean(chunk, true);
			chunkIsTerrainPopulated.setBoolean(chunk, true);
			chunkGenerateHeightMap.invoke(chunk);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		List<TileEntity> invalidList = new ArrayList<TileEntity>();
		iterator = tileEntityMap.values().iterator();
		while(iterator.hasNext()) {
			TileEntity tileentity = (TileEntity) iterator.next();
			if(tileentity.shouldRefresh(worldObj, tileentity.getPos(), tileentity.getBlockType().getStateFromMeta(tileentity.getBlockMetadata()), chunk.getBlockState(tileentity.getPos()))) {
				invalidList.add(tileentity);
			}
			tileentity.updateContainingBlockInfo();
		}
		for(TileEntity te : invalidList) {
			te.invalidate();
		}
	}

	public static S21PacketChunkData.Extracted writeChunkToPacket(Chunk chunk, boolean sendAllChunks, boolean sendSkylight, int chunks) {
		ExtendedBlockStorage[] aextendedblockstorage = chunk.getBlockStorageArray();
		S21PacketChunkData.Extracted extracted = new S21PacketChunkData.Extracted();
		ArrayList<ExtendedBlockStorage> arraylist = Lists.newArrayList();

		for(int i = 0; i < aextendedblockstorage.length; ++i) {
			ExtendedBlockStorage extendedblockstorage = aextendedblockstorage[i];

			if(extendedblockstorage != null && (!sendAllChunks || !extendedblockstorage.isEmpty()) && (chunks & 1 << i) != 0) {
				extracted.dataSize |= 1 << i;
				arraylist.add(extendedblockstorage);
			}
		}
		try {
			extracted.data = new byte[(Integer) func_180737_a.invoke(null, Integer.bitCount(extracted.dataSize), sendSkylight, sendAllChunks)];
			int i = 0;
			Iterator<ExtendedBlockStorage> iterator = arraylist.iterator();
			ExtendedBlockStorage extendedblockstorage1;

			while(iterator.hasNext()) {
				extendedblockstorage1 = (ExtendedBlockStorage) iterator.next();
				int[] data = null;
				try {
					data = (int[]) getData.invoke(extendedblockstorage1);
				} catch(Exception e) {
					e.printStackTrace();
				}
				int[] aint1 = data;
				int k = data.length;
				for(int l = 0; l < k; ++l) {
					int c0 = aint1[l];
					extracted.data[i++] = (byte) ((c0 >> 16) & 255);
					extracted.data[i++] = (byte) ((c0 >> 24) & 127);
					extracted.data[i++] = (byte) (c0 & 255);
					extracted.data[i++] = (byte) ((c0 >> 8) & 255);
				}
			}
			for(iterator = arraylist.iterator(); iterator.hasNext(); i = (Integer) func_179757_a.invoke(null, extendedblockstorage1.getBlocklightArray().getData(), extracted.data, i)) {
				extendedblockstorage1 = (ExtendedBlockStorage) iterator.next();
			}
			if(sendSkylight) {
				for(iterator = arraylist.iterator(); iterator.hasNext(); i = (Integer) func_179757_a.invoke(null, extendedblockstorage1.getSkylightArray().getData(), extracted.data, i)) {
					extendedblockstorage1 = (ExtendedBlockStorage) iterator.next();
				}
			}
			if(sendAllChunks) {
				i = (Integer) func_179757_a.invoke(null, chunk.getBiomeArray(), extracted.data, i);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return extracted;
	}
}
