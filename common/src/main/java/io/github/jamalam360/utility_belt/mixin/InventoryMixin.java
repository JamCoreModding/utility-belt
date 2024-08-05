package io.github.jamalam360.utility_belt.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.jamalam360.utility_belt.state.StateManager;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public abstract class InventoryMixin {

    @Shadow
    @Final
    public Player player;
    
    @Inject(
          method = "getSelected",
          at = @At("HEAD"),
          cancellable = true
    )
    private void utilitybelt$getSelectedInUtilityBelt(CallbackInfoReturnable<ItemStack> cir) {
        StateManager stateManager = StateManager.getStateManager(player);
        if (stateManager.isInBelt(this.player)) {
            ItemStack belt = UtilityBeltItem.getBelt(this.player);

            if (belt == null) {
                return;
            }

            UtilityBeltInventory inv = stateManager.getInventory(this.player);
            cir.setReturnValue(inv.getItem(stateManager.getSelectedBeltSlot(this.player)));
        }
    }
    
    @ModifyExpressionValue(
            method = "getDestroySpeed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/NonNullList;get(I)Ljava/lang/Object;"
            )
    )
    private Object utilitybelt$useBeltStackForDestroySpeed(Object original) {
        StateManager stateManager = StateManager.getStateManager(player);
        if (stateManager.isInBelt(this.player)) {
            ItemStack belt = UtilityBeltItem.getBelt(this.player);

            if (belt != null) {
                UtilityBeltInventory inv = stateManager.getInventory(this.player);
                return inv.getItem(stateManager.getSelectedBeltSlot(this.player));
            }
        }
        
        return original;
    }

    @Inject(
          method = "tick",
          at = @At("RETURN")
    )
    private void utilitybelt$tick(CallbackInfo ci) {
        StateManager stateManager = StateManager.getStateManager(player);
        if (stateManager.isInBelt(this.player)) {
            ItemStack belt = UtilityBeltItem.getBelt(this.player);

            if (belt == null) {
                return;
            }

            UtilityBeltInventory inv = stateManager.getInventory(this.player);
            int selectedSlot = stateManager.getSelectedBeltSlot(this.player);

            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack stack = inv.getItem(i);
                if (!stack.isEmpty()) {
                    stack.inventoryTick(this.player.level(), this.player, i, i == selectedSlot);
                }
            }
        }
    }

    @Inject(method = "removeItem(Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private void utilitybelt$patchRemoveOneForHeldItems(ItemStack stack, CallbackInfo ci) {
        ItemStack belt = UtilityBeltItem.getBelt(this.player);
        if (belt != null) {
            StateManager stateManager = StateManager.getStateManager(player);
            UtilityBeltInventory.Mutable inv = stateManager.getMutableInventory(this.player);
            int found = -1;

            for (int i = 0; i < inv.getContainerSize(); i++) {
                if (ItemStack.matches(stack, inv.getItem(i))) {
                    found = i;
                    break;
                }
            }

            if (found != -1) {
                inv.setItem(found, ItemStack.EMPTY);
                stateManager.setInventory(this.player, inv);
                ci.cancel();
            }
        }
    }

    @Inject(method = "fillStackedContents", at = @At("HEAD"))
    private void utilitybelt$recipeFinderPatch(StackedContents contents, CallbackInfo ci) {
        ItemStack belt = UtilityBeltItem.getBelt(this.player);
        if (belt != null) {
            UtilityBeltInventory inv = StateManager.getStateManager(this.player).getInventory(this.player);

            for (int i = 0; i < inv.getContainerSize(); i++) {
                contents.accountSimpleStack(inv.getItem(i));
            }
        }
    }

    @Inject(method = "removeFromSelected", at = @At("HEAD"), cancellable = true)
    private void utilitybelt$dropStackIfUsingUtilityBelt(boolean entireStack, CallbackInfoReturnable<ItemStack> cir) {
        StateManager stateManager = StateManager.getStateManager(this.player);
        if (stateManager.isInBelt(this.player)) {
            int slot = stateManager.getSelectedBeltSlot(this.player);
            UtilityBeltInventory.Mutable inv = stateManager.getMutableInventory(this.player);
            ItemStack selected = inv.getItem(slot);

            if (this.player.isLocalPlayer()) {
                //TODO: is this needed?
                // Because of how the inventory is synced, we fake the return value on the clientside (not actually removing it)
                ItemStack fakeReturn = selected.copy();
                fakeReturn.setCount(entireStack ? selected.getCount() : 1);
                cir.setReturnValue(selected.isEmpty() ? ItemStack.EMPTY : fakeReturn);
            } else {
                ItemStack item = selected.isEmpty() ? ItemStack.EMPTY : inv.removeItem(slot, entireStack ? selected.getCount() : 1);
                stateManager.setInventory(this.player, inv);
                cir.setReturnValue(item);
            }
        }
    }

    @Inject(method = "clearContent", at = @At("HEAD"))
    private void utilitybelt$clearUtilityBelt(CallbackInfo ci) {
        ItemStack belt = UtilityBeltItem.getBelt(this.player);
        if (belt != null) {
            StateManager stateManager = StateManager.getStateManager(this.player);
            UtilityBeltInventory.Mutable inv = stateManager.getMutableInventory(this.player);
            inv.clearContent();
            stateManager.setInventory(this.player, inv);
        }
    }

    @Inject(method = "contains(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void utilitybelt$patchContainsStack(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        ItemStack belt = UtilityBeltItem.getBelt(this.player);
        if (belt != null) {
            UtilityBeltInventory inv = StateManager.getStateManager(this.player).getInventory(this.player);

            for (int i = 0; i < inv.getContainerSize(); i++) {
                if (!inv.getItem(i).isEmpty() && ItemStack.matches(inv.getItem(i), stack)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "contains(Lnet/minecraft/tags/TagKey;)Z", at = @At("HEAD"), cancellable = true)
    private void utilitybelt$patchContainsStack(TagKey<Item> key, CallbackInfoReturnable<Boolean> cir) {
        ItemStack belt = UtilityBeltItem.getBelt(this.player);
        if (belt != null) {
            UtilityBeltInventory inv = StateManager.getStateManager(this.player).getInventory(this.player);

            for (int i = 0; i < inv.getContainerSize(); i++) {
                if (!inv.getItem(i).isEmpty() && inv.getItem(i).is(key)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
    
    @Inject(
          method = "isEmpty",
          at = @At("HEAD"),
          cancellable = true
    )
    private void utilitybelt$patchIsEmpty(CallbackInfoReturnable<Boolean> cir) {
        ItemStack belt = UtilityBeltItem.getBelt(this.player);
        if (belt != null) {
            UtilityBeltInventory inv = StateManager.getStateManager(this.player).getInventory(this.player);

            for (int i = 0; i < inv.getContainerSize(); i++) {
                if (!inv.getItem(i).isEmpty()) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
    
    @Inject(
            method = "add(ILnet/minecraft/world/item/ItemStack;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void utilitybelt$tryPlaceStackInBelt(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (slot == -1 && UtilityBeltItem.isValidItem(stack)) {
            StateManager stateManager = StateManager.getStateManager(this.player);
            
            if (!stateManager.isInBelt(this.player)) {
                return;
            }
            
            UtilityBeltInventory.Mutable inv = stateManager.getMutableInventory(this.player);
            
            int selected = stateManager.getSelectedBeltSlot(this.player);
            int beltSlot = -1;
            
            if (inv.getItem(selected).isEmpty()) {
                beltSlot = selected;
            } else {
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    if (inv.getItem(i).isEmpty()) {
                        beltSlot = i;
                        break;
                    }
                }
            }
            
            if (beltSlot != -1) {
                inv.setItem(beltSlot, stack.copyAndClear());
                stateManager.setInventory(this.player, inv);
                cir.setReturnValue(true);
            }
        }
    }
}
