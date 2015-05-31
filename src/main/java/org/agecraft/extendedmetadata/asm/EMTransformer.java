package org.agecraft.extendedmetadata.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import codechicken.lib.asm.ModularASMTransformer;

public class EMTransformer implements IClassTransformer {

	private ModularASMTransformer transformer = new ModularASMTransformer();
	
	public EMTransformer() {
		// initialize transformer
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if(bytes == null) {
			return null;
		}
		try {
			// stuff maybe?
			bytes = transformer.transform(name, bytes);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return bytes;
	}
}
