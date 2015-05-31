package org.agecraft.extendedmetadata;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.minecraftforge.fml.client.FMLFileResourcePack;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;

import org.agecraft.extendedmetadata.asm.EMCorePlugin;

import codechicken.core.launch.CodeChickenCorePlugin;

import com.google.common.eventbus.EventBus;

public class EMModContainer extends DummyModContainer {

	public EMModContainer() {
		super(MetadataCollection.from(MetadataCollection.class.getResourceAsStream("/mcmod.info"), "ExtendedMetadata").getMetadataForId("ExtendedMetadata", null));
	}

	@Override
	public Set<ArtifactVersion> getRequirements() {
		Set<ArtifactVersion> deps = new HashSet<ArtifactVersion>();
		deps.add(VersionParser.parseVersionReference("CodeChickenCore@[" + CodeChickenCorePlugin.version + ",)"));
		return deps;
	}

	@Override
	public List<ArtifactVersion> getDependencies() {
		return new LinkedList<ArtifactVersion>(getRequirements());
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(true);
		return true;
	}

	@Override
	public File getSource() {
		return EMCorePlugin.location;
	}

	@Override
	public Class<?> getCustomResourcePackClass() {
		return getSource().isDirectory() ? FMLFolderResourcePack.class : FMLFileResourcePack.class;
	}
}
