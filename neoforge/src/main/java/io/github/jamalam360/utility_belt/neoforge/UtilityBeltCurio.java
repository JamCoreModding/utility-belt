package io.github.jamalam360.utility_belt.neoforge;

import io.github.jamalam360.utility_belt.UtilityBeltItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class UtilityBeltCurio implements ICurioItem {

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        ((UtilityBeltItem) stack.getItem()).onUnequip(slotContext.entity());
    }

    @Override
    public @NotNull DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return DropRule.ALWAYS_DROP;
    }
}
