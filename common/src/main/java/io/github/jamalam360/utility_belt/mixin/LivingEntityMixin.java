package io.github.jamalam360.utility_belt.mixin;

import io.github.jamalam360.utility_belt.Duck;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements Duck.LivingEntity {

    @Shadow
    protected abstract void detectEquipmentUpdates();

    @Override
    public void utilitybelt$detectEquipmentUpdates() {
        this.detectEquipmentUpdates();
    }
}
