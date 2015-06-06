package org.agecraft.extendedmetadata.asm;

import java.io.File;
import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import codechicken.core.launch.CodeChickenCorePlugin;

@TransformerExclusions({"org.agecraft.extendedmetadata.asm"})
public class EMCorePlugin implements IFMLLoadingPlugin, IFMLCallHook {

	public static File location;

	@Override
	public String[] getASMTransformerClass() {
		CodeChickenCorePlugin.versionCheck(CodeChickenCorePlugin.mcVersion, "ExtendedMetadata");
		return new String[]{"org.agecraft.extendedmetadata.asm.EMTransformer"};
	}

	@Override
	public String getModContainerClass() {
		return "org.agecraft.extendedmetadata.EMModContainer";
	}

	@Override
	public String getSetupClass() {
		return "org.agecraft.extendedmetadata.asm.EMCorePlugin";
	}

	@Override
	public void injectData(Map<String, Object> data) {
		location = (File) data.get("coremodLocation");
		if(location == null) {
			location = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		}
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	@Override
	public Void call() {
		return null;
	}
}
