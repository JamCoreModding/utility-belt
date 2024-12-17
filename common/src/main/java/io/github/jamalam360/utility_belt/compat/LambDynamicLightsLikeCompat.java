package io.github.jamalam360.utility_belt.compat;

import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.state.StateManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

// LambDynamicLights and RyoamicLights (planned but not yet implemented)
public class LambDynamicLightsLikeCompat {
	public static void init(LambDynamicLightsLike lights) {
		lights.registerDynamicLightHandler(EntityType.PLAYER, (player) -> getLuminanceInBelt(player, lights));
	}

	private static int getLuminanceInBelt(Player player, LambDynamicLightsLike lights) {
		boolean submerged = isEyeSubmergedInFluid(player, lights);
		int luminance = 0;

		StateManager stateManager = StateManager.getStateManager(player);
		if (stateManager.hasBelt(player)) {
			UtilityBeltInventory inv = stateManager.getInventory(player);

			for (ItemStack stack : inv.items()) {
				luminance = Math.max(luminance, lights.getLuminanceFromItemStack(stack, submerged));
			}
		}

		return luminance;
	}

	// From LambDynLights mod
	private static boolean isEyeSubmergedInFluid(LivingEntity entity, LambDynamicLightsLike lights) {
		if (!lights.isWaterSensitiveCheckEnabled()) {
			return false;
		} else {
			BlockPos eyePos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
			return !entity.level().getFluidState(eyePos).isEmpty();
		}
	}

	public interface LambDynamicLightsLike {
		<T extends LivingEntity> void registerDynamicLightHandler(EntityType<T> type, Function<T, Integer> handler);

		int getLuminanceFromItemStack(@NotNull ItemStack stack, boolean submergedInWater);

		boolean isWaterSensitiveCheckEnabled();
	}
}
