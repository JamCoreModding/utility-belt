package io.github.jamalam360.utility_belt.util;

import dev.architectury.platform.Platform;
import io.github.jamalam360.utility_belt.UtilityBelt;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class DynamicTags {
	private static final String LAMB_DYNAMIC_LIGHTS = "lambdynlights";
	private static final String RYOAMIC_LIGHTS = "ryoamiclights";
	private static List<ResourceLocation> extraItems = null;
	
	public static List<ResourceLocation> getExtraItemsAllowedInUtilityBelt() {
		if (extraItems == null) {
			List<ResourceLocation> list = new ArrayList<>();

			if (Platform.isModLoaded(LAMB_DYNAMIC_LIGHTS) || Platform.isModLoaded(RYOAMIC_LIGHTS)) {
				UtilityBelt.LOGGER.info("Registering extra Utility Belt entries for dynamic lights compatibility.");
				list.add(minecraft("torch"));
				list.add(minecraft("redstone_torch"));
				list.add(minecraft("soul_torch"));
				list.add(minecraft("lantern"));
			}
			
			extraItems = list;
		}
		
		return extraItems;
	}
	
	private static ResourceLocation minecraft(String path) {
		return ResourceLocation.fromNamespaceAndPath("minecraft", path);
	}
}
