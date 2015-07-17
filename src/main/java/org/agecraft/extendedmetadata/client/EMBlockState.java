package org.agecraft.extendedmetadata.client;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

public class EMBlockState {

	public static final ForgeBlockStateV1.Deserializer DESERIALIZER = new ForgeBlockStateV1.Deserializer();
	public static final Collection<String> INVENTORY_KEY = Collections.singleton("inventory");

	public Variant defaults;
	public Multimap<Collection<String>, JsonObject> variantsJson = HashMultimap.create();
	public Multimap<String, Variant> variants = HashMultimap.create();
	public int maxVariable = 0;

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
				if(entry.getKey().equals("inventory")) {
					ret.variantsJson.put(INVENTORY_KEY, entry.getValue().getAsJsonObject());
				} else if(!entry.getKey().contains("=")) {
					for(Entry<String, JsonElement> e : entry.getValue().getAsJsonObject().entrySet()) {
						ret.variantsJson.put(Collections.singleton(entry.getKey() + "=" + e.getKey()), e.getValue().getAsJsonObject());
					}

				} else {
					String[] split = entry.getKey().replaceAll(" ", "").split(",");
					for(int i = 0; i < split.length; i++) {
						if(split[i].contains("{")) {
							String s = split[i].split("=")[1];
							int variable = Integer.parseInt(s.substring(1, s.length() - 1));
							if(variable > ret.maxVariable) {
								ret.maxVariable = variable;
							}
						}
					}
					ret.variantsJson.put(Arrays.asList(split), entry.getValue().getAsJsonObject());
				}
			}

			return ret;
		}
	}

	public void load(Block block, Map<String, IProperty> properties, ImmutableList<StateImplementation> states) throws Exception {
		if(variantsJson.containsKey(INVENTORY_KEY)) {
			Collection<JsonObject> values = variantsJson.get(INVENTORY_KEY);

			List<Variant> list = Lists.newArrayList();

			for(JsonObject json : values) {
				list.add((Variant) EMModelLoader.GSON.fromJson(replaceObjectVariables(new Object[0], json), Variant.class));
			}

			list = Lists.reverse(list);

			Variant variant = null;
			for(Variant var : list) {
				if(variant == null) {
					variant = EMModelLoader.constructor.newInstance(var);
				} else {
					EMModelLoader.sync.invoke(variant, var);
				}
			}
			if(defaults != null) {
				EMModelLoader.sync.invoke(variant, defaults);
			}

			if(!variant.getSubmodels().isEmpty()) {
				variants.putAll("inventory", (List<ForgeBlockStateV1.Variant>) EMModelLoader.getSubmodelPermutations.invoke(DESERIALIZER, variant, variant.getSubmodels()));
			} else {
				variants.put("inventory", variant);
			}
		}
		variantsJson.removeAll(INVENTORY_KEY);
		
		for(StateImplementation state : states) {
			List<Variant> list = Lists.newArrayList();

			for(Entry<Collection<String>, Collection<JsonObject>> entry : variantsJson.asMap().entrySet()) {
				Object[] variables = new Object[maxVariable + 1];

				boolean matches = true;
				for(String key : entry.getKey()) {
					String[] split = key.split("=");

					IProperty property = properties.get(split[0]);
					if(split[1].charAt(0) != '{' && !split[1].equals(property.getName(state.getValue(property)))) {
						matches = false;
						break;
					}

					if(split[1].charAt(0) == '{') {
						variables[Integer.parseInt(split[1].substring(1, split[1].length() - 1))] = property.getName(state.getValue(property));
					}
				}
				if(matches) {
					for(JsonObject json : entry.getValue()) {
						list.add((Variant) EMModelLoader.GSON.fromJson(replaceObjectVariables(variables, json), Variant.class));
					}
				}
			}

			list = Lists.reverse(list);

			Variant variant = null;
			for(Variant var : list) {
				if(variant == null) {
					variant = EMModelLoader.constructor.newInstance(var);
				} else {
					EMModelLoader.sync.invoke(variant, var);
				}
			}
			if(defaults != null) {
				EMModelLoader.sync.invoke(variant, defaults);
			}

			if(!variant.getSubmodels().isEmpty()) {
				variants.putAll(EMModelLoader.getPropertyString(state.getProperties()), (List<ForgeBlockStateV1.Variant>) EMModelLoader.getSubmodelPermutations.invoke(DESERIALIZER, variant, variant.getSubmodels()));
			} else {
				variants.put(EMModelLoader.getPropertyString(state.getProperties()), variant);
			}
		}
		variantsJson.clear();
	}

	private static JsonObject replaceObjectVariables(Object[] variables, JsonObject json) {
		JsonObject copy = new JsonObject();
		for(Entry<String, JsonElement> entry : json.entrySet()) {
			if(entry.getValue().isJsonNull()) {
				copy.add(entry.getKey(), JsonNull.INSTANCE);
			} else if(entry.getValue().isJsonPrimitive()) {
				copy.add(entry.getKey(), replacePrimitiveVariables(variables, entry.getValue().getAsJsonPrimitive()));
			} else if(entry.getValue().isJsonArray()) {
				copy.add(entry.getKey(), replaceArrayVariables(variables, entry.getValue().getAsJsonArray()));
			} else if(entry.getValue().isJsonObject()) {
				copy.add(entry.getKey(), replaceObjectVariables(variables, entry.getValue().getAsJsonObject()));
			}
		}
		return copy;
	}

	private static JsonElement replacePrimitiveVariables(Object[] variables, JsonPrimitive primitive) {
		if(primitive.isString()) {
			return new JsonPrimitive(MessageFormat.format(primitive.getAsString(), variables));
		} else if(primitive.isNumber()) {
			return new JsonPrimitive(primitive.getAsNumber());
		} else if(primitive.isBoolean()) {
			return new JsonPrimitive(primitive.getAsBoolean());
		}
		return JsonNull.INSTANCE;
	}

	private static JsonArray replaceArrayVariables(Object[] variables, JsonArray array) {
		JsonArray arrayCopy = new JsonArray();
		for(JsonElement element : array) {
			if(element.isJsonNull()) {
				arrayCopy.add(JsonNull.INSTANCE);
			} else if(element.isJsonPrimitive()) {
				arrayCopy.add(replacePrimitiveVariables(variables, element.getAsJsonPrimitive()));
			} else if(element.isJsonArray()) {
				arrayCopy.add(replaceArrayVariables(variables, element.getAsJsonArray()));
			} else if(element.isJsonObject()) {
				arrayCopy.add(replaceObjectVariables(variables, element.getAsJsonObject()));
			}
		}
		return arrayCopy;
	}
}
