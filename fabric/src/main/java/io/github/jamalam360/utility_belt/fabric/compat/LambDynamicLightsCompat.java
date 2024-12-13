package io.github.jamalam360.utility_belt.fabric.compat;

import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandler;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.compat.LambDynamicLightsLikeCompat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class LambDynamicLightsCompat implements DynamicLightsInitializer {
	@Override
	public void onInitializeDynamicLights(ItemLightSourceManager manager) {
		UtilityBelt.LOGGER.info("Initializing LambDynamicLights compat for Utility Belt.");
		LambDynamicLightsLikeCompat.init(new LambDynamicLightsLikeCompat.LambDynamicLightsLike() {
			@Override
			public <T extends LivingEntity> void registerDynamicLightHandler(EntityType<T> type, Function<T, Integer> handler) {
				DynamicLightHandlers.registerDynamicLightHandler(type, DynamicLightHandler.makeHandler(handler, player -> false));
			}

			@Override
			public int getLuminanceFromItemStack(@NotNull ItemStack stack, boolean submergedInWater) {
				return LambDynLights.getLuminanceFromItemStack(stack, submergedInWater);
			}

			@Override
			public boolean isWaterSensitiveCheckEnabled() {
				return LambDynLights.get().config.getWaterSensitiveCheck().get();
			}
		});
	}
}
