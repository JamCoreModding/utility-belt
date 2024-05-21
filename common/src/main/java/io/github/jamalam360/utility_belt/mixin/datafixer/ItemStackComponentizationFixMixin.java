package io.github.jamalam360.utility_belt.mixin.datafixer;

import com.mojang.serialization.Dynamic;
import io.github.jamalam360.utility_belt.UtilityBelt;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStackComponentizationFix.class)
public class ItemStackComponentizationFixMixin {
    @Inject(
          method = "fixItemStack",
          at = @At("TAIL")
    )
    private static void utilitybelt$moveNbtToComponent(ItemStackComponentizationFix.ItemStackData itemStackData, Dynamic<?> dynamic, CallbackInfo ci) {
        if (itemStackData.is(UtilityBelt.id("utility_belt").toString())) {
            System.out.println("fixed belt");
            itemStackData.moveTagToComponent("Inventory", "utility_belt:utility_belt_inventory", dynamic.createList(Stream.empty()));
        }
    }
}
