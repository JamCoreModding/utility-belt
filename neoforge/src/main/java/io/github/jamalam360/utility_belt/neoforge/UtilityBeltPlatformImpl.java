package io.github.jamalam360.utility_belt.neoforge;

import io.github.jamalam360.utility_belt.UtilityBelt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

public class UtilityBeltPlatformImpl {

    @Nullable
    public static ItemStack getStackInBeltSlot(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity).flatMap(i -> i.findFirstCurio(UtilityBelt.UTILITY_BELT_ITEM.get())).map(SlotResult::stack).orElse(null);
    }
}
