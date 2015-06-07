package org.agecraft.extendedmetadata;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class ExtendedMetadata {

	public static int getIDFromState(IBlockState state) {
		return getIDFromState(Block.getIdFromBlock(state.getBlock()), state);
	}
	
	public static int getIDFromState(int blockID, IBlockState state) {
		return ((blockID & 32767) << 16) | (state.getBlock().getMetaFromState(state) & 65535);
	}
	
	public static IBlockState getStateFromID(int id) {
        return Block.getBlockById((id >> 16) & 32767).getStateFromMeta(id & 65535);
	}
}
