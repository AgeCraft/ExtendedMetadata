package org.agecraft.extendedmetadata.test;

import java.util.List;

import org.agecraft.extendedmetadata.BlockBasicMetadata;
import org.agecraft.extendedmetadata.ItemBlockMetadata;
import org.agecraft.extendedmetadata.client.EMModelLoader;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = ExtendedMetadataTest.MOD_ID, name = ExtendedMetadataTest.MOD_ID, version = ExtendedMetadataTest.VERSION)
public class ExtendedMetadataTest {

	public static final String MOD_ID = "ExtendedMetadataTest";
	public static final String VERSION = "1.0.0";

	@SidedProxy(serverSide = "org.agecraft.extendedmetadata.test.ExtendedMetadataTest$CommonProxy", clientSide = "org.agecraft.extendedmetadata.test.ExtendedMetadataTest$ClientProxy")
	public static CommonProxy proxy;

	public static BlockExtendedMetadata block;
	public static BlockWoolFence woolFence;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		block = new BlockExtendedMetadata();
		woolFence = new BlockWoolFence();

		GameRegistry.registerBlock(block, ItemBlockMetadata.class, BlockExtendedMetadata.NAME);
		GameRegistry.registerBlock(woolFence, ItemBlockMetadata.class, BlockWoolFence.NAME);

		// Force load ExtendedBlockState for testing
		try {
			Class.forName("net.minecraftforge.common.property.ExtendedBlockState");
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		// Force load ChunkPrimer for testing
		try {
			Class.forName("net.minecraft.world.chunk.ChunkPrimer");
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		proxy.preInit();
	}

	public static class CommonProxy {

		public void preInit() {

		}
	}

	public static class ClientProxy extends CommonProxy {

		@Override
		public void preInit() {
			// Register blocks to use ExtendedMetadata's blockstate format
			EMModelLoader.registerBlock(block);
			EMModelLoader.registerBlock(woolFence);

			// Register the default item models for the block
			EMModelLoader.registerBlockItemModels(block);

			// Register a custom item model for the block
			EMModelLoader.registerBlockItemModel(block, 14, "inventory14");
			
			// Register a custom item model for all block subtypes
//			EMModelLoader.registerBlockItemModels(woolFence, "inventory");
			
			// Register a custom item model for the block
//			EMModelLoader.registerBlockItemModel(woolFence, 8, "proper_inventory");
		}
	}

	public static class BlockExtendedMetadata extends BlockBasicMetadata {

		// Tested up to 32767, the maximum value (2^15 for values + 2^1 for half/full = 2^16 = 65536)
		public static final int VALUE_SIZE = 15;

		public static final String NAME = "extended_metadata";
		public static final PropertyInteger VALUE = PropertyInteger.create("value", 0, VALUE_SIZE);
		public static final PropertyBool HALF = PropertyBool.create("half");

		public BlockExtendedMetadata() {
			super(Material.cloth);
			setUnlocalizedName(MOD_ID.toLowerCase() + ":" + NAME);
			setDefaultState(blockState.getBaseState().withProperty(VALUE, 0).withProperty(HALF, false));
			setHardness(0.8F);
			setStepSound(Block.soundTypeCloth);
			setCreativeTab(CreativeTabs.tabBlock);
		}

		@Override
		public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
			if(!world.isRemote) {
				player.addChatMessage(new ChatComponentText("Color: " + state.getValue(VALUE)));

				world.setBlockState(pos, state.withProperty(HALF, !Boolean.valueOf((Boolean) state.getValue(HALF))));
			}
			return true;
		}

		@Override
		public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
			IBlockState state = getDefaultState().withProperty(VALUE, meta >> 1).withProperty(HALF, Boolean.valueOf((meta & 1) == 1));
			if(!world.isRemote) {
				placer.addChatMessage(new ChatComponentText("Placing color: " + state.getValue(VALUE)));
			}
			return state;
		}

		@Override
		public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
			setBlockBoundsBasedOnState(world, pos);
			super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);
		}

		@Override
		public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
			if(((Boolean) world.getBlockState(pos).getValue(HALF)).booleanValue()) {
				setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
			} else {
				setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			}
		}

		@Override
		public boolean isOpaqueCube() {
			return false;
		}

		@Override
		public String getUnlocalizedName(int meta) {
			return "extended_" + Integer.toString(meta);
		}

		@Override
		public int damageDropped(IBlockState state) {
			return getMetaFromState(state) & ~1;
		}

		@Override
		public IBlockState getStateFromMeta(int meta) {
			return getDefaultState().withProperty(VALUE, Integer.valueOf(meta >> 1)).withProperty(HALF, Boolean.valueOf((meta & 1) == 1));
		}

		@Override
		public int getMetaFromState(IBlockState state) {
			return (((Integer) state.getValue(VALUE)).intValue() << 1) | (((Boolean) state.getValue(HALF)).booleanValue() ? 1 : 0);
		}

		@Override
		protected BlockState createBlockState() {
			return new BlockState(this, VALUE, HALF);
		}

		@Override
		public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
			for(int i = 0; i < 16; i++) {
				list.add(new ItemStack(item, 1, i << 1));
				list.add(new ItemStack(item, 1, i << 1 | 1));
			}
		}
	}

	public static class BlockWoolFence extends BlockBasicMetadata {

		public static final String NAME = "wool_fence";

		public static final PropertyBool NORTH = PropertyBool.create("north");
		public static final PropertyBool EAST = PropertyBool.create("east");
		public static final PropertyBool SOUTH = PropertyBool.create("south");
		public static final PropertyBool WEST = PropertyBool.create("west");
		public static final PropertyInteger COLOR = PropertyInteger.create("color", 0, 15);

		public BlockWoolFence() {
			super(Material.cloth);
			setUnlocalizedName(MOD_ID.toLowerCase() + ":" + NAME);
			setDefaultState(blockState.getBaseState().withProperty(COLOR, 0));
			setHardness(0.8F);
			setStepSound(Block.soundTypeCloth);
			setCreativeTab(CreativeTabs.tabBlock);
		}

		@Override
		public BlockState createBlockState() {
			return new BlockState(this, NORTH, EAST, SOUTH, WEST, COLOR);
		}

		@Override
		public int getMetaFromState(IBlockState state) {
			return state.getValue(COLOR);
		}

		@Override
		public IBlockState getStateFromMeta(int meta) {
			return getDefaultState().withProperty(COLOR, meta);
		}

		@Override
		public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
			return state.withProperty(NORTH, canConnectTo(world, state, pos.north())).withProperty(EAST, canConnectTo(world, state, pos.east())).withProperty(SOUTH, canConnectTo(world, state, pos.south())).withProperty(WEST, canConnectTo(world, state, pos.west()));
		}

		@Override
		public boolean isOpaqueCube() {
			return false;
		}

		@Override
		public boolean isFullCube() {
			return false;
		}

		@Override
		public boolean isPassable(IBlockAccess world, BlockPos pos) {
			return false;
		}

		@Override
		public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
			return true;
		}

		public boolean canConnectTo(IBlockAccess world, IBlockState state, BlockPos pos) {
			IBlockState other = world.getBlockState(pos);
			Block block = other.getBlock();
			if(block instanceof BlockWoolFence) {
				return state.getValue(COLOR).equals(other.getValue(COLOR));
			}
			return block != Blocks.barrier && block.getMaterial().isOpaque() && block.isFullCube() ? block.getMaterial() != Material.gourd : false;
		}

		@Override
		public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity entity) {
			boolean connectMinX = canConnectTo(world, state, pos.west());
			boolean connectMaxX = canConnectTo(world, state, pos.east());
			boolean connectMinZ = canConnectTo(world, state, pos.north());
			boolean connectMaxZ = canConnectTo(world, state, pos.south());
			float minX = 0.375F;
			float maxX = 0.625F;
			float minZ = 0.375F;
			float maxZ = 0.625F;

			if(connectMinZ) {
				minZ = 0.0F;
			}
			if(connectMaxZ) {
				maxZ = 1.0F;
			}
			if(connectMinZ || connectMaxZ) {
				setBlockBounds(minX, 0.0F, minZ, maxX, 1.5F, maxZ);
				super.addCollisionBoxesToList(world, pos, state, mask, list, entity);
			}
			minZ = 0.375F;
			maxZ = 0.625F;
			if(connectMinX) {
				minX = 0.0F;
			}
			if(connectMaxX) {
				maxX = 1.0F;
			}
			if(connectMinX || connectMaxX || !connectMinZ && !connectMaxZ) {
				setBlockBounds(minX, 0.0F, minZ, maxX, 1.5F, maxZ);
				super.addCollisionBoxesToList(world, pos, state, mask, list, entity);
			}
			if(connectMinZ) {
				minZ = 0.0F;
			}
			if(connectMaxZ) {
				maxZ = 1.0F;
			}
			setBlockBounds(minX, 0.0F, minZ, maxX, 1.0F, maxZ);
		}

		@Override
		public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
			IBlockState state = world.getBlockState(pos);
			boolean connectMinZ = canConnectTo(world, state, pos.north());
			boolean connectMaxZ = canConnectTo(world, state, pos.south());
			boolean connectMinX = canConnectTo(world, state, pos.west());
			boolean connectMaxX = canConnectTo(world, state, pos.east());
			float minX = 0.375F;
			float maxX = 0.625F;
			float minZ = 0.375F;
			float maxZ = 0.625F;

			if(connectMinX) {
				minX = 0.0F;
			}
			if(connectMaxX) {
				maxX = 1.0F;
			}
			if(connectMinZ) {
				minZ = 0.0F;
			}
			if(connectMaxZ) {
				maxZ = 1.0F;
			}
			setBlockBounds(minX, 0.0F, minZ, maxX, 1.0F, maxZ);
		}
		
		@Override
		public String getLocalizedName() {
			return getUnlocalizedName();
		}

		@Override
		public String getLocalizedName(int meta) {
			return getUnlocalizedName(meta);
		}

		@Override
		public String getUnlocalizedName() {
			return "Wool Fence";
		}

		@Override
		public String getUnlocalizedName(int meta) {
			return "Wool Fence " + meta;
		}

		@Override
		public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
			for(int i = 0; i < 16; i++) {
				list.add(new ItemStack(item, 1, i));
			}
		}
	}
}
