package org.agecraft.extendedmetadata.client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition.Variants;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

import org.agecraft.extendedmetadata.EMUtil;
import org.agecraft.extendedmetadata.test.ExtendedMetadataTest;
import org.agecraft.extendedmetadata.test.ExtendedMetadataTest.BlockExtendedMetadata;

public class EMModelLoader {

	public static void load(ModelLoader loader) {
		try {
			Field field = EMUtil.getField(ModelBakery.class, "blockDefinitions", "field_177614_t", "t");
			Map<ResourceLocation, ModelBlockDefinition> map = (Map<ResourceLocation, ModelBlockDefinition>) field.get(loader);

			ArrayList<Variants> list = new ArrayList<Variants>();

			for(int i = 0; i < 301; i++) {
				ArrayList<ModelBlockDefinition.Variant> variants = new ArrayList<ModelBlockDefinition.Variant>();

				IBlockState state = ExtendedMetadataTest.block.getStateFromMeta(i);
				String properties = getPropertyString(state.getProperties());

				EMVariant var = new EMVariant(getBlockLocation(ExtendedMetadataTest.MOD_ID.toLowerCase() + ":" + BlockExtendedMetadata.NAME));
				var.textures.put("all", ExtendedMetadataTest.MOD_ID.toLowerCase() + ":blocks/" + BlockExtendedMetadata.NAME + "_" + Integer.toString(i));
				
				ModelRotation rot = var.getRotation().or(ModelRotation.X0_Y0);
				boolean uvLock = var.getUvLock().or(false);
				int weight = var.getWeight().or(1);

				variants.add(new SmartVariant(var.getModel(), rot, uvLock, weight, var.getTextures(), var.getOnlyPartsVariant(), var.getCustomData()));

				list.add(new Variants(properties, variants));
			}

			map.put(new ResourceLocation(ExtendedMetadataTest.MOD_ID.toLowerCase(), "blockstates/" + BlockExtendedMetadata.NAME + ".json"), new ModelBlockDefinition((Collection) list));

			field.set(loader, map);

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static String getPropertyString(Map map) {
		StringBuilder stringbuilder = new StringBuilder();
		Iterator iterator = map.entrySet().iterator();

		while(iterator.hasNext()) {
			Entry entry = (Entry) iterator.next();

			if(stringbuilder.length() != 0) {
				stringbuilder.append(",");
			}

			IProperty iproperty = (IProperty) entry.getKey();
			Comparable comparable = (Comparable) entry.getValue();
			stringbuilder.append(iproperty.getName());
			stringbuilder.append("=");
			stringbuilder.append(iproperty.getName(comparable));
		}

		if(stringbuilder.length() == 0) {
			stringbuilder.append("normal");
		}

		return stringbuilder.toString();
	}

	public static ResourceLocation getBlockLocation(String location) {
		ResourceLocation tmp = new ResourceLocation(location);
		return new ResourceLocation(tmp.getResourceDomain(), "block/" + tmp.getResourcePath());
	}
}
