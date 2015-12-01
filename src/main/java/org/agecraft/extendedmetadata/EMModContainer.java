package org.agecraft.extendedmetadata;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.agecraft.extendedmetadata.asm.EMCorePlugin;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.minecraftforge.fml.client.FMLFileResourcePack;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

public class EMModContainer extends DummyModContainer {

	public static HashMap<String, Object> map = new HashMap<String, Object>();

	static {
		map.put("name", "ExtendedMetadata");
		map.put("version", "@VERSION@");
	}

	public EMModContainer() {
		super(MetadataCollection.from(MetadataCollection.class.getResourceAsStream("/mcmod.info"), "ExtendedMetadata").getMetadataForId("ExtendedMetadata", map));
	}

	@Override
	public Set<ArtifactVersion> getRequirements() {
		return Sets.newHashSet();
	}

	@Override
	public List<ArtifactVersion> getDependencies() {
		return new LinkedList<ArtifactVersion>(getRequirements());
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
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

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		ExtendedMetadata.init();
	}
}
