package io.github.jamalam360.utility_belt.mixin;

import io.github.jamalam360.utility_belt.Duck;
import io.github.jamalam360.utility_belt.StateManager;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
import io.github.jamalam360.utility_belt.server.ServerNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements Duck.LivingEntity {
	@Shadow protected abstract void detectEquipmentUpdates();

	@Shadow protected abstract Map<EquipmentSlot, ItemStack> collectEquipmentChanges();

	/**
	 * @reason Patches broadcastBreakEvent to broadcast the break event of the item in the belt if the player is switched to it
	 */
	@Inject(
			method = "broadcastBreakEvent(Lnet/minecraft/world/InteractionHand;)V",
			at = @At("HEAD")
	)
	private void utilitybelt$broadcastBreakEvent(InteractionHand interactionHand, CallbackInfo ci) {
		if (interactionHand == InteractionHand.MAIN_HAND && (Object) this instanceof ServerPlayer player) {
			StateManager stateManager = StateManager.getServerInstance();
			if (stateManager.isInBelt(player)) {
				ItemStack belt = UtilityBeltItem.getBelt(player);
				assert belt != null;
				UtilityBeltInventory inv = stateManager.getInventory(player);
				inv.setItem(stateManager.getSelectedBeltSlot(player), ItemStack.EMPTY);
			}
		}
	}

	@Override
	public void utilitybelt$detectEquipmentUpdates() {
		this.detectEquipmentUpdates();
	}
}
