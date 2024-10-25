package io.github.jamalam360.utility_belt.fabric.compat;

import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandler;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.state.StateManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class LambDynamicLightsCompat implements DynamicLightsInitializer {
	@Override
	public void onInitializeDynamicLights(ItemLightSourceManager manager) {
		UtilityBelt.LOGGER.info("Initializing LambDynamicLights compat for Utility Belt.");
		DynamicLightHandlers.registerDynamicLightHandler(EntityType.PLAYER, DynamicLightHandler.makeHandler(this::getLuminanceInBelt, player -> false));
	}

	private int getLuminanceInBelt(Player player) {
		boolean submerged = isEyeSubmergedInFluid(player);
		int luminance = 0;

		StateManager stateManager = StateManager.getStateManager(player);
		if (stateManager.hasBelt(player)) {
			UtilityBeltInventory inv = stateManager.getInventory(player);

			for (ItemStack stack : inv.items()) {
				luminance = Math.max(luminance, LambDynLights.getLuminanceFromItemStack(stack, submerged));
			}
		}

		return luminance;
	}

	// From LambDynLights mod
	private static boolean isEyeSubmergedInFluid(LivingEntity entity) {
		if (!LambDynLights.get().config.getWaterSensitiveCheck().get()) {
			return false;
		} else {
			BlockPos eyePos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
			return !entity.level().getFluidState(eyePos).isEmpty();
		}
	}
}
