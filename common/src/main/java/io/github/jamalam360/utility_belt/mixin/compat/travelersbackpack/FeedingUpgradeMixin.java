package io.github.jamalam360.utility_belt.mixin.compat.travelersbackpack;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.jamalam360.utility_belt.state.StateManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

/// Fixes [#65](https://github.com/JamCoreModding/utility-belt/issues/65).
/// Modifies FeedingUpgrade#tryFeedingStack to not use [getMainHandItem](https://github.com/Tiviacz1337/Travelers-Backpack/blob/e5c9330e4eb4665880e1557c2220886f2f9e038c/src/main/java/com/tiviacz/travelersbackpack/inventory/upgrades/feeding/FeedingUpgrade.java#L127).
@Pseudo
@Mixin(targets = "com.tiviacz.travelersbackpack.inventory.upgrades.feeding.FeedingUpgrade", remap = false)
public class FeedingUpgradeMixin {
	@ModifyExpressionValue(
			method = "tryFeedingStack",
			at = @At(
					value = "INVOKE",
					remap = true,
					target = "Lnet/minecraft/world/entity/player/Player;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"
			)
	)
	private ItemStack utilitybelt$modifyMainHandItem(ItemStack original, Level level, int hungerLevel, Player player, Integer slot, ItemStack stack, @Coerce Object backpackStorage) {
		if (StateManager.getStateManager(player).isInBelt(player)) {
			return player.getInventory().getItem(player.getInventory().getSelectedSlot());
		} else {
			return original;
		}
	}
}
