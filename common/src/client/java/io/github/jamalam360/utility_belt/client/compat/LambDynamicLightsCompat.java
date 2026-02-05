package io.github.jamalam360.utility_belt.client.compat;

import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.util.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.state.StateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class LambDynamicLightsCompat implements DynamicLightsInitializer {
	@Override
	public void onInitializeDynamicLights(DynamicLightsContext ctx) {
		UtilityBelt.LOGGER.info("Initializing LambDynamicLights compatibility");
		ctx.entityLightSourceManager().onRegisterEvent().register((registrar) -> registrar.register(EntityType.PLAYER, UtilityBeltEntityLuminance.INSTANCE));
	}

	private static class UtilityBeltEntityLuminance implements EntityLuminance {
		public static final UtilityBeltEntityLuminance INSTANCE = new UtilityBeltEntityLuminance();
		public static final EntityLuminance.Type TYPE = EntityLuminance.Type.registerSimple(
				UtilityBelt.id("belt_luminance"),
				INSTANCE
		);

		private UtilityBeltEntityLuminance() {
		}

		@Override
		public @NotNull Type type() {
			return TYPE;
		}

		@Override
		public @Range(from = 0L, to = 15L) int getLuminance(@NotNull ItemLightSourceManager itemLightSourceManager, @NotNull Entity entity) {
			if (!(entity instanceof Player player)) {
				return 0;
			}

			int luminance = 0;
			StateManager stateManager = StateManager.getStateManager(player);
			if (stateManager.hasBelt(player)) {
				UtilityBeltInventory inv = stateManager.getInventory(player);

				for (ItemStack stack : inv.items()) {
					luminance = Math.max(luminance, itemLightSourceManager.getLuminance(stack));
				}
			}

			return luminance;
		}
	}
}
