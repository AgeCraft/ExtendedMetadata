package org.agecraft.extendedmetadata.client;

import java.util.Map.Entry;

import javax.vecmath.Matrix4f;

import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelCustomData;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ISmartVariant;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.MultiModel;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.client.model.BlockStateLoader.SubModel;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableMap;

public class SmartVariant extends ModelBlockDefinition.Variant implements ISmartVariant {

	private final ImmutableMap<String, String> textures;
	private final ImmutableMap<String, SubModel> parts;
	private final ImmutableMap<String, String> customData;

	public SmartVariant(ResourceLocation model, ModelRotation rotation, boolean uvLock, int weight, ImmutableMap<String, String> textures, ImmutableMap<String, SubModel> parts, ImmutableMap<String, String> customData) {
		super(model == null ? new ResourceLocation("builtin/missing") : model, rotation, uvLock, weight);
		this.textures = textures;
		this.parts = parts;
		this.customData = customData;
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
		ModelRotation baseRot = getRotation();
		ImmutableMap.Builder<String, Pair<IModel, IModelState>> models = ImmutableMap.builder();
		for(Entry<String, SubModel> entry : parts.entrySet()) {
			SubModel part = entry.getValue();

			Matrix4f matrix = new Matrix4f(baseRot.getMatrix());
			matrix.mul(part.getRotation().getMatrix());
			IModelState partState = new TRSRTransformation(matrix);
			if(part.isUVLock()) {
				partState = new ModelLoader.UVLock(partState);
			}
			models.put(entry.getKey(), Pair.of(runModelHooks(loader.getModel(part.getModelLocation()), part.getTextures(), part.getCustomData()), partState));
		}
		return new MultiModel(hasBase ? base : null, baseRot, models.build());
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
