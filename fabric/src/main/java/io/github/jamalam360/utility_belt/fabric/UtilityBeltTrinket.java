package io.github.jamalam360.utility_belt.fabric;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketEnums.DropRule;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
import io.github.jamalam360.utility_belt.UtilityBeltPlatform;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class UtilityBeltTrinket implements Trinket {

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        ((UtilityBeltItem) stack.getItem()).onEquip(entity, stack);
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        // Trinkets is calling this even when the item is not being unequipped
        if (UtilityBeltPlatform.getStackInBeltSlot(entity) == null) {
            ((UtilityBeltItem) stack.getItem()).onUnequip(entity);
        }
    }

    @Override
    public DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return DropRule.DROP;
    }
}
