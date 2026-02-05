package io.github.jamalam360.utility_belt.mixin;

import io.github.jamalam360.utility_belt.util.Duck;
import io.github.jamalam360.utility_belt.util.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.state.StateManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements Duck.LivingEntity {

    @Shadow
    protected abstract void detectEquipmentUpdates();

    @Override
    public void utilitybelt$detectEquipmentUpdates() {
        this.detectEquipmentUpdates();
    }

	@SuppressWarnings("ConstantValue")
	@Inject(
			method = "setItemInHand",
			at = @At("HEAD")
	)
	private void utilitybelt$setItemInHand(InteractionHand hand, ItemStack stack, CallbackInfo ci) {
		if (hand == InteractionHand.MAIN_HAND && (Object) this instanceof ServerPlayer player) {
			StateManager stateManager = StateManager.getStateManager(player);
			if (stateManager.isInBelt(player)) {
				UtilityBeltInventory.Mutable inv = stateManager.getMutableInventory(player);
				inv.setItem(stateManager.getSelectedBeltSlot(player), stack);
				stateManager.setInventory(player, inv);
			}
		}
	}
}
