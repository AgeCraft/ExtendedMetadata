package org.agecraft.extendedmetadata.client;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class EMBlockState {

	public Variant defaults;
	public Multimap<String, Variant> variants = HashMultimap.create();
	public Multimap<String, String> variantKeys = HashMultimap.create();
	public Multimap<String, Variant> actualVariants = HashMultimap.create();

	public static class Deserializer implements JsonDeserializer<EMBlockState> {

		public static final Deserializer INSTANCE = new Deserializer();
		public static final ForgeBlockStateV1.Deserializer FORGE_INSTANCE = new ForgeBlockStateV1.Deserializer();

		@Override
		public EMBlockState deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject json = element.getAsJsonObject();
			EMBlockState ret = new EMBlockState();

			if(json.has("defaults")) {
				ret.defaults = context.deserialize(json.get("defaults"), Variant.class);
			}

			for(Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "variants").entrySet()) {
				//TODO: remove variantKeys and change storing process
				if(!entry.getKey().contains("=")) {
					for(Entry<String, JsonElement> e : entry.getValue().getAsJsonObject().entrySet()) {
						ret.variantKeys.put(entry.getKey(), entry.getKey() + "=" + e.getKey());
						
						ret.variants.put(entry.getKey() + "=" + e.getKey(), (Variant) context.deserialize(e.getValue(), Variant.class));
					}
					
				} else {
					List<String> properties = parseKey(entry.getKey());
					for(String property : properties) {
						ret.variantKeys.put(property, entry.getKey());
					}

					ret.variants.put(entry.getKey(), (Variant) context.deserialize(entry.getValue(), Variant.class));
				}
			}

			return ret;
		}

		public List<String> parseKey(String key) {
			key = key.replaceAll(" ", "");
			String[] properties = key.split(",");

			ArrayList<String> list = Lists.newArrayListWithCapacity(properties.length);

			for(int i = 0; i < properties.length; i++) {
				String[] split = properties[0].split("=");
				list.add(split[0]);
			}

			return list;
		}
	}

	public void load(Block block, ImmutableList<StateImplementation> states) {
		for(StateImplementation state : states) {
			ArrayList<Variant> list = Lists.newArrayList();
			
			for(Entry<IProperty, Comparable> entry : (ImmutableSet<Entry<IProperty, Comparable>>) state.getProperties().entrySet()) {
				
				for(String variantName : variantKeys.get(entry + "=*")) {
					list.addAll(variants.get(variantName));
				}
				for(String variantName : variantKeys.get(entry + "=" + entry.getValue())) {
					list.addAll(variants.get(variantName));
				}
			}
			
			//TODO: merge variants and add it to the actual variants maps
		}
	}
}
