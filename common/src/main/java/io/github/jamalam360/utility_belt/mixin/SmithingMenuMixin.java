package io.github.jamalam360.utility_belt.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin extends ItemCombinerMenu {
	@Unique
	private static final ResourceLocation UPGRADE_RECIPE_LOCATION = UtilityBelt.id("upgrade_utility_belt");

	public SmithingMenuMixin(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slotDefinition) {
		super(menuType, containerId, inventory, access, slotDefinition);
	}

	@ModifyExpressionValue(
			method = "createResult",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"
			)
	)
	private Optional<RecipeHolder<SmithingRecipe>> utilitybelt$capBeltSize(Optional<RecipeHolder<SmithingRecipe>> original) {
		if (original.isPresent()) {
			RecipeHolder<SmithingRecipe> holder = original.get();

			if (holder.id().location().equals(UPGRADE_RECIPE_LOCATION)) {
				ItemStack inputStack = this.getSlot(1).getItem();

				if (!inputStack.is(UtilityBelt.UTILITY_BELT_ITEM.get())) {
					throw new IllegalStateException("Attempted to upgrade an item which is not a utility belt using the utility belt upgrade recipe.");
				}

				int currentSize = UtilityBeltItem.getInventorySize(inputStack);
				if (currentSize >= UtilityBelt.COMMON_CONFIG.get().maxBeltSize) {
					return Optional.empty();
				}
			}
		}

		return original;
	}

	@WrapOperation(
			method = "method_64653",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V"
			)
	)
	private void utilitybelt$upgradeBeltSlots(ResultContainer instance, int slot, ItemStack resultStack, Operation<Void> original, SmithingRecipeInput input, RecipeHolder<SmithingRecipe> holder) {
		if (holder.id().location().equals(UPGRADE_RECIPE_LOCATION)) {
			ItemStack inputStack = this.getSlot(1).getItem();

			if (!inputStack.is(UtilityBelt.UTILITY_BELT_ITEM.get()) || !resultStack.is(UtilityBelt.UTILITY_BELT_ITEM.get())) {
				throw new IllegalStateException("Attempted to upgrade an item which is not a utility belt using the utility belt upgrade recipe.");
			}

			int currentSize = UtilityBeltItem.getInventorySize(inputStack);
			UtilityBeltInventory currentInventory = UtilityBeltItem.getInventory(inputStack);
			resultStack.set(UtilityBelt.UTILITY_BELT_SIZE_COMPONENT_TYPE.get(), currentSize + 1);
			resultStack.set(UtilityBelt.UTILITY_BELT_INVENTORY_COMPONENT_TYPE.get(), currentInventory.copyWithSize(currentSize + 1));
		}

		original.call(instance, slot, resultStack);
	}
}
