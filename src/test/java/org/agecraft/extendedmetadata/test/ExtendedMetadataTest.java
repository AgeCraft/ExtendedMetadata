package org.agecraft.extendedmetadata.test;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = ExtendedMetadataTest.MOD_ID, version = ExtendedMetadataTest.VERSION)
public class ExtendedMetadataTest {

    public static final String MOD_ID = "ExtendedMetadataTest";
    public static final String VERSION = "1.0.0";

    @SidedProxy(serverSide = "org.agecraft.extendedmetadata.test.ExtendedMetadataTest$CommonProxy", clientSide = "org.agecraft.extendedmetadata.test.ExtendedMetadataTest$ClientProxy")
    public static CommonProxy proxy;
    
    public static BlockExtendedMetadata block;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        block = new BlockExtendedMetadata();
        
        GameRegistry.registerBlock(block, ItemExtendedMetadata.class, BlockExtendedMetadata.NAME);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    public static class CommonProxy {

        public void init() {
           
        }
    }

    public static class ClientProxy extends CommonProxy {
        
        @Override
        public void init() {            
            
        }
    }
    
    public static class BlockExtendedMetadata extends Block {
        
        public static final String NAME = "extended_metadata";
        public static final PropertyInteger VALUE = PropertyInteger.create("value", 0, 1023);

        public BlockExtendedMetadata() {
            super(Material.cloth);
            setCreativeTab(CreativeTabs.tabBlock);
            setUnlocalizedName(MOD_ID.toLowerCase() + ":" + NAME);
            setDefaultState(blockState.getBaseState().withProperty(VALUE, 0));
            setHardness(0.8F);
            setStepSound(Block.soundTypeCloth);
        }
        
        @Override
        public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
            if(world.isRemote) {
                return true;
            }
            player.addChatMessage(new ChatComponentText("Metadata: " + state.getValue(VALUE)));
            return true;
        }
        
        @Override
        public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
            IBlockState state = getDefaultState().withProperty(VALUE, meta);
            if(!world.isRemote) {
                placer.addChatMessage(new ChatComponentText("Placing metadata: " + state.getValue(VALUE)));
            }
            return state;
        }
        
        @Override
        public int damageDropped(IBlockState state) {
            return getMetaFromState(state);
        }
        
        @Override
        public IBlockState getStateFromMeta(int meta) {
            return getDefaultState().withProperty(VALUE, meta);
        }
        
        @Override
        public int getMetaFromState(IBlockState state) {
            return ((Integer) state.getValue(VALUE)).intValue();
        }
        
        @Override
        protected BlockState createBlockState() {
            return new BlockState(this, VALUE);
        }
        
        @SuppressWarnings({"rawtypes", "unchecked"})
		@Override
        public void getSubBlocks(Item item, CreativeTabs tab, List list) {
            list.add(new ItemStack(item, 1, 0));
            list.add(new ItemStack(item, 1, 63));
            list.add(new ItemStack(item, 1, 127));
            list.add(new ItemStack(item, 1, 128));
            list.add(new ItemStack(item, 1, 255));
            list.add(new ItemStack(item, 1, 256));
            list.add(new ItemStack(item, 1, 300));
            list.add(new ItemStack(item, 1, 512));
            list.add(new ItemStack(item, 1, 1023));
        }
    }
    
    public static class ItemExtendedMetadata extends ItemBlock {
        
        public ItemExtendedMetadata(Block block) {
            super(block);
            setMaxDamage(0);
            setHasSubtypes(true);
        }

        public int getMetadata(int meta) {
            return meta;
        }

        public String getUnlocalizedName(ItemStack stack) {
            return "extended_" + stack.getItemDamage();
        }
    }
}
