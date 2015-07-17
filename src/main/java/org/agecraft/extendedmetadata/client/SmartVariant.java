package org.agecraft.extendedmetadata.client;

import java.io.IOException;
import java.util.Map.Entry;

import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.BlockStateLoader.SubModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelCustomData;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ISmartVariant;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelStateComposition;
import net.minecraftforge.client.model.MultiModel;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableMap;

public class SmartVariant extends ModelBlockDefinition.Variant implements ISmartVariant {

	private final ImmutableMap<String, String> textures;
	private final ImmutableMap<String, SubModel> parts;
	private final ImmutableMap<String, String> customData;
	private final IModelState state;

	public SmartVariant(ResourceLocation model, IModelState state, boolean uvLock, int weight, ImmutableMap<String, String> textures, ImmutableMap<String, SubModel> parts, ImmutableMap<String, String> customData) {
		super(model == null ? new ResourceLocation("builtin/missing") : model, state instanceof ModelRotation ? (ModelRotation) state : ModelRotation.X0_Y0, uvLock, weight);
		this.textures = textures;
		this.parts = parts;
		this.customData = customData;
		this.state = state;
	}

	public IModel runModelHooks(IModel base, ImmutableMap<String, String> textureMap, ImmutableMap<String, String> customData) {
		if(!customData.isEmpty()) {
			if(base instanceof IModelCustomData) {
				base = ((IModelCustomData) base).process(customData);
			} else {
				throw new RuntimeException("Attempted to add custom data to a model that doesn't need it: " + base);
			}
		}
		if(!textureMap.isEmpty()) {
			if(base instanceof IRetexturableModel) {
				base = ((IRetexturableModel) base).retexture(textureMap);
			} else {
				throw new RuntimeException("Attempted to retexture a non-retexturable model: " + base);
			}
		}
		return base;
	}

	@Override
	public IModel process(IModel base, ModelLoader loader) {
		int size = parts.size();
		boolean hasBase = base != loader.getMissingModel();

		if(hasBase) {
			base = runModelHooks(base, textures, customData);
			if(size <= 0) {
				return base;
			}
		}
		IModelState baseTr = getState();
		ImmutableMap.Builder<String, Pair<IModel, IModelState>> models = ImmutableMap.builder();
		for(Entry<String, SubModel> entry : parts.entrySet()) {
			SubModel part = entry.getValue();
			IModel model = null;
			try {
				model = loader.getModel(part.getModelLocation());
			} catch(IOException e) {
				FMLLog.warning("Unable to load block sub-model: \'" + part.getModelLocation() /* + "\' for variant: \'" + parent */+ "\': " + e.toString());
				model = loader.getMissingModel();
			}

			IModelState partState = new ModelStateComposition(baseTr, part.getState());
			if(part.isUVLock())
				partState = new ModelLoader.UVLock(partState);

			models.put(entry.getKey(), Pair.of(runModelHooks(model, part.getTextures(), part.getCustomData()), partState));
		}
		return new MultiModel(hasBase ? base : null, baseTr, models.build());
	}

	@Override
	public IModelState getState() {
		return state;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("TexturedVariant:");
		for(Entry<String, String> e : this.textures.entrySet()) {
			buf.append(" ").append(e.getKey()).append(" = ").append(e.getValue());
		}
		return buf.toString();
	}
}
