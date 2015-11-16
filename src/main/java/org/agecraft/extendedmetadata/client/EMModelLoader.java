package org.agecraft.extendedmetadata.client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockState.StateImplementation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition.Variant;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition.Variants;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ForgeBlockStateV1;
import net.minecraftforge.client.model.ForgeBlockStateV1.TRSRDeserializer;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.fml.common.registry.GameData;

import org.agecraft.extendedmetadata.EMUtil;
import org.agecraft.extendedmetadata.ExtendedMetadata;
import org.apache.commons.io.IOUtils;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EMModelLoader {

	public static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(EMBlockState.class, EMBlockState.Deserializer.INSTANCE).registerTypeAdapter(ForgeBlockStateV1.Variant.class, EMBlockState.Deserializer.FORGE_INSTANCE).registerTypeAdapter(TRSRTransformation.class, TRSRDeserializer.INSTANCE).create();

	private static Field blockDefinitions;
	private static Method createBlockState;
	protected static Method getSubmodelPermutations;
	protected static Constructor<ForgeBlockStateV1.Variant> constructor;
	protected static Method sync;
	private static Class<? extends Variant> smartVariant;
	private static Constructor<? extends Variant> smartVariantConstructor;

	private static HashMap<Block, ResourceLocation> blocks = Maps.newHashMap();

	public static void registerBlock(Block block) {
		registerBlock(block, getBlockStateLocation((ResourceLocation) GameData.getBlockRegistry().getNameForObject(block)));
	}

	public static void registerBlock(Block block, String name) {
		registerBlock(block, getBlockStateLocation(new ResourceLocation(name)));
	}

	public static void registerBlock(Block block, ResourceLocation location) {
		blocks.put(block, location);
	}

	public static void unregisterBlock(Block block) {
		blocks.remove(block);
	}

	public static ResourceLocation getBlockStateLocation(ResourceLocation location) {
		return new ResourceLocation(location.getResourceDomain(), "blockstates/" + location.getResourcePath() + ".json");
	}

	public static void load(ModelLoader loader) {
		try {
			ExtendedMetadata.log.info("Loading block models");

			createBlockState = EMUtil.getMethod(Block.class, "createBlockState", "func_180661_e", "e");
			getSubmodelPermutations = EMUtil.getMethod(ForgeBlockStateV1.Deserializer.class, "getSubmodelPermutations", "getSubmodelPermutations", "getSubmodelPermutations", ForgeBlockStateV1.Variant.class, Map.class);
			constructor = EMUtil.getConstructor(ForgeBlockStateV1.Variant.class, ForgeBlockStateV1.Variant.class);
			sync = EMUtil.getMethod(ForgeBlockStateV1.Variant.class, "sync", "sync", "sync", ForgeBlockStateV1.Variant.class);

			smartVariant = (Class<? extends Variant>) Class.forName("net.minecraftforge.client.model.BlockStateLoader$ForgeVariant");
			smartVariantConstructor = EMUtil.getConstructor(smartVariant, ResourceLocation.class, IModelState.class, boolean.class, int.class, ImmutableMap.class, ImmutableMap.class, ImmutableMap.class);

			blockDefinitions = EMUtil.getField(ModelBakery.class, "blockDefinitions", "field_177614_t", "t");
			Map<ResourceLocation, ModelBlockDefinition> map = (Map<ResourceLocation, ModelBlockDefinition>) blockDefinitions.get(loader);

			IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();

			for(Entry<Block, ResourceLocation> entry : blocks.entrySet()) {
				Iterator<IResource> iterator = resourceManager.getAllResources(entry.getValue()).iterator();

				while(iterator.hasNext()) {
					IResource resource = (IResource) iterator.next();
					InputStream inputstream = null;
					try {
						inputstream = resource.getInputStream();
						Reader reader = new InputStreamReader(inputstream, Charsets.UTF_8);
						byte[] data = IOUtils.toByteArray(reader);
						reader = new InputStreamReader(new ByteArrayInputStream(data), Charsets.UTF_8);

						EMBlockState blockState = GSON.fromJson(reader, EMBlockState.class);
						map.put(entry.getValue(), loadModelBlockDefinition(entry.getKey(), entry.getValue(), blockState));
					} catch(Exception e) {
						throw new RuntimeException("Encountered an exception when loading block model definition of \'" + entry.getValue() + "\' from: \'" + resource.getResourceLocation() + "\' in resourcepack: \'" + resource.getResourcePackName() + "\'", e);
					} finally {
						IOUtils.closeQuietly(inputstream);
					}
				}
			}

			blockDefinitions.set(loader, map);

			ExtendedMetadata.log.info("Finished loading " + map.size() + " block models");
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void loadBlockInventoryModels(ModelLoader loader) {
		try {
			ExtendedMetadata.log.info("Loading block item models");

			Map<ResourceLocation, ModelBlockDefinition> map = (Map<ResourceLocation, ModelBlockDefinition>) blockDefinitions.get(loader);
			HashMap<ResourceLocation, ModelBlockDefinition> itemMap = Maps.newHashMap();

			IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();

			ExtendedMetadata.log.info(blocks);
			
			for(Entry<Block, ResourceLocation> entry : blocks.entrySet()) {
				Iterator<IResource> iterator = resourceManager.getAllResources(entry.getValue()).iterator();

				while(iterator.hasNext()) {
					IResource resource = (IResource) iterator.next();
					InputStream inputstream = null;
					try {
						inputstream = resource.getInputStream();
						Reader reader = new InputStreamReader(inputstream, Charsets.UTF_8);
						byte[] data = IOUtils.toByteArray(reader);
						reader = new InputStreamReader(new ByteArrayInputStream(data), Charsets.UTF_8);
						
						ExtendedMetadata.log.info("Loading block item model for " + entry.getValue() + " from " + resource.getResourceLocation() + " in " + resource.getResourcePackName());
						
						//TODO: load block item model
					} catch(Exception e) {
						throw new RuntimeException("Encountered an exception when loading block item model definition of \'" + entry.getValue() + "\' from: \'" + resource.getResourceLocation() + "\' in resourcepack: \'" + resource.getResourcePackName() + "\'", e);
					} finally {
						IOUtils.closeQuietly(inputstream);
					}
				}
			}

			map.putAll(itemMap);
			blockDefinitions.set(loader, map);

			ExtendedMetadata.log.info("Finished loading " + itemMap.size() + " block item models");
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

		// ExtendedMetadata.log.info("INVENTORY LOADER");
		// try {
		// ResourceLocation blockStateLocation = getBlockStateLocation(location);
		// ModelBlockDefinition model = blockInventoryModels.get(blockStateLocation);
		// System.out.println("models: " + blockInventoryModels);
		// System.out.println("location: " + blockStateLocation);
		// if(model != null) {
		// System.out.println("YUP");
		// Map<ResourceLocation, ModelBlockDefinition> map = (Map<ResourceLocation, ModelBlockDefinition>) blockDefinitions.get(loader);
		// map.put(blockStateLocation, model);
		// blockDefinitions.set(loader, map);
		// }
		// } catch(Exception e) {
		// throw new RuntimeException(e);
		// }
	}

	private static ModelBlockDefinition loadModelBlockDefinition(Block block, ResourceLocation location, EMBlockState blockState) throws Exception {
		BlockState state = ((BlockState) createBlockState.invoke(block));
		ImmutableList<StateImplementation> states = state.getValidStates();

		HashMap<String, IProperty> properties = Maps.newHashMap();
		for(IProperty property : (Collection<IProperty>) state.getProperties()) {
			properties.put(property.getName(), property);
		}
		blockState.load(block, properties, states);

		ArrayList<Variants> list = Lists.newArrayList();

		for(Entry<String, Collection<ForgeBlockStateV1.Variant>> entry : blockState.variants.asMap().entrySet()) {
			ArrayList<Variant> variants = new ArrayList<Variant>();

			for(ForgeBlockStateV1.Variant variant : entry.getValue()) {
				boolean uvLock = variant.getUvLock().or(false);
				int weight = variant.getWeight().or(1);

				if(variant.getModel() != null && variant.getSubmodels().size() == 0 && variant.getTextures().size() == 0 && variant.getCustomData().size() == 0 && variant.getState().orNull() instanceof ModelRotation) {
					variants.add(new Variant(variant.getModel(), (ModelRotation) variant.getState().get(), uvLock, weight));
				} else {
					variants.add(smartVariantConstructor.newInstance(variant.getModel(), variant.getState().or(TRSRTransformation.identity()), uvLock, weight, variant.getTextures(), variant.getOnlyPartsVariant(), variant.getCustomData()));
				}
			}

			list.add(new Variants(entry.getKey(), variants));
		}
		return new ModelBlockDefinition((Collection) list);
	}

	public static String getPropertyString(Map<IProperty, Comparable> map) {
		StringBuilder builder = new StringBuilder();

		for(Entry<IProperty, Comparable> entry : map.entrySet()) {
			if(builder.length() != 0) {
				builder.append(",");
			}

			IProperty property = (IProperty) entry.getKey();
			Comparable comparable = (Comparable) entry.getValue();
			builder.append(property.getName());
			builder.append("=");
			builder.append(property.getName(comparable));
		}

		if(builder.length() == 0) {
			builder.append("normal");
		}
		return builder.toString();
	}
}
