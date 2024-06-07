package io.github.jamalam360.utility_belt.mixin;

import io.github.jamalam360.utility_belt.StateManager;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
import io.github.jamalam360.utility_belt.server.ServerNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
	@SuppressWarnings("ConstantValue")
	@Inject(
			method = "setItemSlot",
			at = @At("HEAD")
	)
	private void utilitybelt$setItemInHand(EquipmentSlot equipmentSlot, ItemStack itemStack, CallbackInfo ci) {
		if (equipmentSlot == EquipmentSlot.MAINHAND && (Object) this instanceof ServerPlayer player) {
			StateManager stateManager = StateManager.getServerInstance();
			if (stateManager.isInBelt(player)) {
				UtilityBeltInventory.Mutable inv = stateManager.getMutableInventory(player);
				inv.setItem(stateManager.getSelectedBeltSlot(player), itemStack);
				stateManager.setInventory(player, inv);
			}
		}
	}
}
