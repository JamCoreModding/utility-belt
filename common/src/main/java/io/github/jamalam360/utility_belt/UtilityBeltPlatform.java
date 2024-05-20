package io.github.jamalam360.utility_belt;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class UtilityBeltPlatform {
    @Nullable
    @ExpectPlatform
    public static ItemStack getStackInBeltSlot(LivingEntity entity) {
        return null;
    }
}
