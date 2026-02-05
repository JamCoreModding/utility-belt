package io.github.jamalam360.utility_belt.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.content.register.ModComponents;
import io.github.jamalam360.utility_belt.content.register.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin extends ItemCombinerMenu {
	@Unique
	private static final ResourceLocation UPGRADE_RECIPE_LOCATION = UtilityBelt.id("upgrade_utility_belt");

	public SmithingMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
		super(type, containerId, playerInventory, access);
	}

	@ModifyExpressionValue(
			method = "createResult",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/List;"
			)
	)
	private List<RecipeHolder<SmithingRecipe>> utilitybelt$capBeltSize(List<RecipeHolder<SmithingRecipe>> original) {
		if (!original.isEmpty()) {
			RecipeHolder<SmithingRecipe> holder = original.getFirst();

			if (holder.id().equals(UPGRADE_RECIPE_LOCATION)) {
				ItemStack inputStack = this.getSlot(1).getItem();

				if (!inputStack.is(ModItems.UTILITY_BELT_ITEM.get())) {
					throw new IllegalStateException("Attempted to upgrade an item which is not a utility belt using the utility belt upgrade recipe.");
				}

				int currentSize = ModComponents.getBeltSize(inputStack);
				if (currentSize >= UtilityBelt.COMMON_CONFIG.get().maxBeltSize) {
					return List.of();
				}
			}
		}

		return original;
	}

	@WrapOperation(
			method = "createResult",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V"
			)
	)
	private void utilitybelt$upgradeBeltSlots(ResultContainer instance, int slot, ItemStack resultStack, Operation<Void> original, @Local(name = "list") List<RecipeHolder<SmithingRecipe>> list) {
		RecipeHolder<SmithingRecipe> holder = list.getFirst();
		if (holder.id().equals(UPGRADE_RECIPE_LOCATION)) {
			ItemStack inputStack = this.getSlot(1).getItem();

			if (!inputStack.is(ModItems.UTILITY_BELT_ITEM.get()) || !resultStack.is(ModItems.UTILITY_BELT_ITEM.get())) {
				throw new IllegalStateException("Attempted to upgrade an item which is not a utility belt using the utility belt upgrade recipe.");
			}

			int currentSize = ModComponents.getBeltSize(inputStack);
			ModComponents.setBeltSize(resultStack, currentSize + 1);
		}

		original.call(instance, slot, resultStack);
	}
}
