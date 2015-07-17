package org.agecraft.extendedmetadata.test;

import java.util.List;

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

import org.agecraft.extendedmetadata.BlockBasicMetadata;
import org.agecraft.extendedmetadata.ItemBlockMetadata;
import org.agecraft.extendedmetadata.client.EMModelLoader;

@Mod(modid = ExtendedMetadataTest.MOD_ID, name = ExtendedMetadataTest.MOD_ID, version = ExtendedMetadataTest.VERSION)
public class ExtendedMetadataTest {

	public static final String MOD_ID = "ExtendedMetadataTest";
	public static final String VERSION = "1.0.0";

	@SidedProxy(serverSide = "org.agecraft.extendedmetadata.test.ExtendedMetadataTest$CommonProxy", clientSide = "org.agecraft.extendedmetadata.test.ExtendedMetadataTest$ClientProxy")
	public static CommonProxy proxy;

	public static BlockExtendedMetadata block;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		block = new BlockExtendedMetadata();

		GameRegistry.registerBlock(block, ItemBlockMetadata.class, BlockExtendedMetadata.NAME);

		proxy.preInit();
	}

	public static class CommonProxy {

		public void preInit() {

		}
	}

	public static class ClientProxy extends CommonProxy {

		@Override
		public void preInit() {
			EMModelLoader.registerBlock(block);
		}
	}

	public static class BlockExtendedMetadata extends BlockBasicMetadata {

		public static final String NAME = "extended_metadata";
		public static final PropertyInteger VALUE = PropertyInteger.create("value", 0, 1023);
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
				player.addChatMessage(new ChatComponentText("Metadata: " + state.getValue(VALUE)));

				world.setBlockState(pos, state.withProperty(HALF, !Boolean.valueOf((Boolean) state.getValue(HALF))));
			}
			return true;
		}

		@Override
		public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
			IBlockState state = getDefaultState().withProperty(VALUE, meta & 32767);
			if(!world.isRemote) {
				placer.addChatMessage(new ChatComponentText("Placing metadata: " + state.getValue(VALUE)));
			}
			return state;
		}

		@Override
		public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list, Entity collidingEntity) {
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
			return getMetaFromState(state);
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

		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public void getSubBlocks(Item item, CreativeTabs tab, List list) {
			for(int i = 0; i < 16; i++) {
				list.add(new ItemStack(item, 1, i));
			}
			list.add(new ItemStack(item, 1, 63));
			list.add(new ItemStack(item, 1, 127));
			list.add(new ItemStack(item, 1, 128));
			list.add(new ItemStack(item, 1, 255));
			list.add(new ItemStack(item, 1, 256));
			list.add(new ItemStack(item, 1, 300));
			list.add(new ItemStack(item, 1, 1023));
			list.add(new ItemStack(item, 1, 4095));
		}
	}
}
