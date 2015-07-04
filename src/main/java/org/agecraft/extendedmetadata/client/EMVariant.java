package org.agecraft.extendedmetadata.client;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.BlockStateLoader.SubModel;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class EMVariant {

	private ResourceLocation model = null;
	private boolean modelSet = false;
	private Optional<ModelRotation> rotation = Optional.absent();
	private Optional<Boolean> uvLock = Optional.absent();
	private Optional<Integer> weight = Optional.absent();
	protected Map<String, String> textures = Maps.newHashMap();
	protected Map<String, List<EMVariant>> submodels = Maps.newHashMap();
	protected Map<String, Object> simpleSubmodels = Maps.newHashMap();
	protected Map<String, String> customData = Maps.newHashMap();

	public EMVariant() {
	}

	public EMVariant(ResourceLocation model) {
		this.model = model;
		this.modelSet = true;
	}

	public EMVariant(ResourceLocation model, ModelRotation rotation, boolean uvLock, int weight) {
		this(model);
		this.rotation = Optional.of(rotation);
		this.uvLock = Optional.of(uvLock);
		this.weight = Optional.of(weight);
	}

	public EMVariant(EMVariant other) {
		this.model = other.model;
		this.modelSet = other.modelSet;
		this.rotation = other.rotation;
		this.uvLock = other.uvLock;
		this.weight = other.weight;
		this.textures.putAll(other.textures);
		this.submodels.putAll(other.submodels);
		this.simpleSubmodels.putAll(other.simpleSubmodels);
		this.customData.putAll(other.customData);
	}

	public EMVariant sync(EMVariant parent) {
		if(!this.modelSet) {
			this.model = parent.model;
			this.modelSet = parent.modelSet;
		}
		if(!this.rotation.isPresent()) {
			this.rotation = parent.rotation;
		}
		if(!this.uvLock.isPresent()) {
			this.uvLock = parent.uvLock;
		}
		if(!this.weight.isPresent()) {
			this.weight = parent.weight;
		}

		for(Entry<String, String> e : parent.textures.entrySet()) {
			if(!this.textures.containsKey(e.getKey())) {
				this.textures.put(e.getKey(), e.getValue());
			}
		}

		mergeModelPartVariants(this.submodels, parent.submodels);

		for(Entry<String, Object> e : parent.simpleSubmodels.entrySet()) {
			if(!this.simpleSubmodels.containsKey(e.getKey())) {
				this.simpleSubmodels.put(e.getKey(), e.getValue());
			}
		}

		for(Entry<String, String> e : parent.customData.entrySet()) {
			if(!this.customData.containsKey(e.getKey())) {
				this.customData.put(e.getKey(), e.getValue());
			}
		}

		return this;
	}

	public Map<String, List<EMVariant>> mergeModelPartVariants(Map<String, List<EMVariant>> output, Map<String, List<EMVariant>> input) {
		for(Entry<String, List<EMVariant>> e : input.entrySet()) {
			String key = e.getKey();
			if(!output.containsKey(key)) {
				List<EMVariant> variants = e.getValue();

				if(variants != null) {
					List<EMVariant> newVariants = Lists.newArrayListWithCapacity(variants.size());

					for(EMVariant v : variants) {
						newVariants.add(new EMVariant(v));
					}

					output.put(key, newVariants);
				} else {
					output.put(key, variants);
				}
			}
		}
		return output;
	}

	protected SubModel asGenericSubModel() {
		return new SubModel(rotation.or(ModelRotation.X0_Y0), uvLock.or(false), getTextures(), model, getCustomData());
	}

	public ImmutableMap<String, SubModel> getOnlyPartsVariant() {
		if(submodels.size() > 0) {
			ImmutableMap.Builder<String, SubModel> builder = ImmutableMap.builder();

			for(Entry<String, List<EMVariant>> entry : submodels.entrySet()) {
				List<EMVariant> part = entry.getValue();

				if(part != null) {
					if(part.size() == 1) {
						builder.put(entry.getKey(), part.get(0).asGenericSubModel());
					} else {
						throw new RuntimeException("Something attempted to get the list of submodels " + "for a variant with model \"" + model + "\", when this variant " + "contains multiple variants for submodel " + entry.getKey());
					}
				}
			}
			return builder.build();
		} else {
			return ImmutableMap.of();
		}
	}

	public ResourceLocation getModel() {
		return model;
	}

	public boolean isModelSet() {
		return modelSet;
	}

	public Optional<ModelRotation> getRotation() {
		return rotation;
	}

	public void setRotation(ModelRotation value) {
		rotation = Optional.of(value);
	}

	public Optional<Boolean> getUvLock() {
		return uvLock;
	}

	public void setUvLock(boolean value) {
		uvLock = Optional.of(value);
	}

	public Optional<Integer> getWeight() {
		return weight;
	}

	public void setWeight(int value) {
		weight = Optional.of(value);
	}

	public ImmutableMap<String, String> getTextures() {
		return ImmutableMap.copyOf(textures);
	}

	public ImmutableMap<String, List<EMVariant>> getSubmodels() {
		return ImmutableMap.copyOf(submodels);
	}

	public ImmutableMap<String, String> getCustomData() {
		return ImmutableMap.copyOf(customData);
	}
}
