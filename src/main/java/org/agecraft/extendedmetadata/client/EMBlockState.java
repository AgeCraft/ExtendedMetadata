package org.agecraft.extendedmetadata.client;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState.StateImplementation;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.client.model.ForgeBlockStateV1;
import net.minecraftforge.client.model.ForgeBlockStateV1.Variant;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class EMBlockState {

	public static final ForgeBlockStateV1.Deserializer DESERIALIZER = new ForgeBlockStateV1.Deserializer();
	
	public Variant defaults;
	public Multimap<Collection<String>, Variant> variants = HashMultimap.create();
	public Multimap<String, Variant> actualVariants = HashMultimap.create();

	public static class Deserializer implements JsonDeserializer<EMBlockState> {

		public static final Deserializer INSTANCE = new Deserializer();
		public static final ForgeBlockStateV1.Variant.Deserializer FORGE_INSTANCE = new ForgeBlockStateV1.Variant.Deserializer();
		
		@Override
		public EMBlockState deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject json = element.getAsJsonObject();
			EMBlockState ret = new EMBlockState();

			if(json.has("defaults")) {
				ret.defaults = context.deserialize(json.get("defaults"), Variant.class);
			}

			for(Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "variants").entrySet()) {
				if(!entry.getKey().contains("=")) {
					for(Entry<String, JsonElement> e : entry.getValue().getAsJsonObject().entrySet()) {
						ret.variants.put(Collections.singletonList(entry.getKey() + "=" + e.getKey()), (Variant) context.deserialize(e.getValue(), Variant.class));
					}
					
				} else {
					ret.variants.put(parseKey(entry.getKey()), (Variant) context.deserialize(entry.getValue(), Variant.class));
				}
			}

			return ret;
		}

		public Collection<String> parseKey(String key) {
			key = key.replaceAll(" ", "");
			String[] properties = key.split(",");

			ArrayList<String> list = Lists.newArrayListWithCapacity(properties.length);
			
			for(int i = 0; i < properties.length; i++) {
				String[] split = properties[i].split("=");
				if(split[1].charAt(0) == '{' && split[1].charAt(split[1].length() - 1) == '}') {
					list.add(split[0] + "=*");
				} else {
					list.add(properties[i]);
				}
			}
			
			return list;
		}
	}

	public void load(Block block, ImmutableList<StateImplementation> states) throws Exception {
		HashMap<String, IProperty> properties = Maps.newHashMap();
		for(IProperty property : (Collection<IProperty>) states.get(0).getPropertyNames()) {
			properties.put(property.getName(), property);
		}
		for(StateImplementation state : states) {
			//System.out.println(state);
			
			List<Variant> list = Lists.newArrayList();

			for(Entry<Collection<String>, Collection<Variant>> entry : variants.asMap().entrySet()) {
				boolean matches = true;
				for(String key : entry.getKey()) {
					String[] split = key.split("=");
					
					//System.out.println("    " + key);
					IProperty property = properties.get(split[0]);
					if(!split[1].equals("*") && !split[1].equals(property.getName(state.getValue(property)))) {
						matches = false;
						break;
					}
				}
				if(matches) {
					//System.out.println("    added");
					list.addAll(entry.getValue());
				}
			}
			
			//TODO: replace variables in textures, etc.
			
			//System.out.println("");
			
			list = Lists.reverse(list);
			//System.out.println(list);
			
			Variant variant = null;
			for(Variant var : list) {
				//System.out.println("    " + var.getModel() + " " + var.getTextures());
				if(variant == null) {
					variant = EMModelLoader.constructor.newInstance(var);
				} else {
					EMModelLoader.sync.invoke(variant, var);
				}
			}
			if(defaults != null) {
				EMModelLoader.sync.invoke(variant, defaults);
			}
			
			//System.out.println("    " + variant.getModel() + " " + variant.getTextures() + " " + variant.getSubmodels().size());
			
			if(!variant.getSubmodels().isEmpty()) {
				actualVariants.putAll(EMModelLoader.getPropertyString(state.getProperties()), (List<ForgeBlockStateV1.Variant>) EMModelLoader.getSubmodelPermutations.invoke(DESERIALIZER, variant, variant.getSubmodels()));
			} else {
				actualVariants.put(EMModelLoader.getPropertyString(state.getProperties()), variant);
			}
		}
	}
}
