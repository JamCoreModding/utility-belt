package io.github.jamalam360.utility_belt.mixin;

import io.github.jamalam360.utility_belt.Duck;
import io.github.jamalam360.utility_belt.StateManager;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
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

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements Duck.LivingEntity {

    @Shadow
    protected abstract void detectEquipmentUpdates();

    @SuppressWarnings("ConstantValue")
    @Inject(
          method = "broadcastBreakEvent",
          at = @At("HEAD")
    )
    private void utilitybelt$broadcastBreakEvent(EquipmentSlot slot, CallbackInfo ci) {
        if (slot == EquipmentSlot.MAINHAND && (Object) this instanceof ServerPlayer player) {
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
