package io.github.jamalam360.utility_belt.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.jamalam360.utility_belt.util.Duck;
import io.github.jamalam360.utility_belt.util.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.state.StateManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
	@WrapOperation(
			method = "setItemInHand",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;setItemSlot(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V"
			)
	)
	private void utilitybelt$setItemInHand(LivingEntity instance, EquipmentSlot equipmentSlot, ItemStack itemStack, Operation<Void> original) {
		if ((Object) this instanceof Player player) {
			StateManager stateManager = StateManager.getStateManager(player);
			if (stateManager.isInBelt(player)) {
				UtilityBeltInventory.Mutable inv = stateManager.getMutableInventory(player);
				inv.setItem(stateManager.getSelectedBeltSlot(player), itemStack);
				stateManager.setInventory(player, inv);
			}
		} else {
			original.call(instance, equipmentSlot, itemStack);
		}
	}
}
