package io.github.jamalam360.utility_belt.util;

import dev.architectury.platform.Platform;
import io.github.jamalam360.utility_belt.UtilityBelt;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class DynamicTags {
	private static final String LAMB_DYNAMIC_LIGHTS = "lambdynlights";
	
	public static List<ResourceLocation> getExtraItemsAllowedInUtilityBelt() {
		List<ResourceLocation> list = new ArrayList<>();
		
		if (Platform.isModLoaded(LAMB_DYNAMIC_LIGHTS)) {
			UtilityBelt.LOGGER.info("Registering extra Utility Belt entries for LambDynamicLights compatibility.");
			list.add(minecraft("torch"));
			list.add(minecraft("redstone_torch"));
			list.add(minecraft("soul_torch"));
			list.add(minecraft("lantern"));
		}
		
		return list;
	}
	
	private static ResourceLocation minecraft(String path) {
		return ResourceLocation.fromNamespaceAndPath("minecraft", path);
	}
}
