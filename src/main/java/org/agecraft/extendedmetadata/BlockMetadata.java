package org.agecraft.extendedmetadata;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.StatCollector;

public class BlockMetadata extends Block {

	protected String[] harvestTool;
	protected int[] harvestLevel;

	public BlockMetadata(Material material, int maxMetadata) {
		super(material);
		this.harvestTool = new String[maxMetadata];
		this.harvestLevel = new int[maxMetadata];
		Arrays.fill(harvestLevel, -1);
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	public String getLocalizedName(int meta) {
		return StatCollector.translateToLocal(getUnlocalizedName(meta) + ".name");
	}

	public String getUnlocalizedName(int meta) {
		return getUnlocalizedName();
	}

	@Override
	public String getHarvestTool(IBlockState state) {
		return harvestTool[getMetaFromState(state)];
	}

	@Override
	public int getHarvestLevel(IBlockState state) {
		return harvestLevel[getMetaFromState(state)];
	}

	@Override
	public void setHarvestLevel(String toolClass, int level, IBlockState state) {
		int meta = getMetaFromState(state);
		this.harvestTool[meta] = toolClass;
		this.harvestLevel[meta] = level;
	}
}
