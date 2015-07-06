package org.agecraft.extendedmetadata;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class BlockBasicMetadata extends BlockMetadata {
	
	public BlockBasicMetadata(Material material) {
		super(material, 1);
	}
	
	@Override
	public String getHarvestTool(IBlockState state) {
		return harvestTool[0];
	}

	@Override
	public int getHarvestLevel(IBlockState state) {
		return harvestLevel[0];
	}
	
	@Override
	public void setHarvestLevel(String toolClass, int level) {
		this.harvestTool[0] = toolClass;
		this.harvestLevel[0] = level;
	}

	@Override
	public void setHarvestLevel(String toolClass, int level, IBlockState state) {
		setHarvestLevel(toolClass, level);
	}
}
