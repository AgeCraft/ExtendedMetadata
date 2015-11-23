package org.agecraft.extendedmetadata;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

public class BlockStateCache {

	public IProperty property;
	public Comparable value;
	public IBlockState state;
	
	public boolean has(IProperty property, Comparable value) {
		return this.property != null && this.property.equals(property) && this.value != null && this.value.equals(value);
	}
	
	public IBlockState get(IProperty property, Comparable value) {
		if(has(property, value)) {
			return state;
		}
		return null;
	}
	
	public void set(IProperty property, Comparable value, IBlockState state) {
		this.property = property;
		this.value = value;
		this.state = state;
	}
}
