package io.github.jamalam360.utility_belt.fabric;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.jamalam360.utility_belt.UtilityBelt;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class UtilityBeltPlatformImpl {

    @Nullable
    public static ItemStack getStackInBeltSlot(LivingEntity entity) {
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(entity);

        if (component.isPresent()) {
            List<Tuple<SlotReference, ItemStack>> l = component.get().getEquipped(UtilityBelt.UTILITY_BELT_ITEM.get());
            if (!l.isEmpty()) {
                return l.get(0).getB();
            }
        }

        return null;
    }
}
