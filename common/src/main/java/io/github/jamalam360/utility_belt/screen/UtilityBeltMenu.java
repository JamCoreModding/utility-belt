package io.github.jamalam360.utility_belt.screen;

import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.UtilityBeltInventory.Mutable;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
import io.github.jamalam360.utility_belt.state.StateManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UtilityBeltMenu extends AbstractContainerMenu {

    private final Mutable inventory;

    public UtilityBeltMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new Mutable(UtilityBeltInventory.EMPTY));
    }

    public UtilityBeltMenu(int syncId, Inventory playerInventory, UtilityBeltInventory.Mutable inventory) {
        super(UtilityBelt.MENU_TYPE.get(), syncId);
        this.inventory = inventory;

        int m;
        int l;

        for (l = 0; l < this.inventory.getContainerSize(); ++l) {
            this.addSlot(new Slot(inventory, l, 53 + l * 18, 17) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return UtilityBeltItem.isValidItem(stack);
                }
            });
        }

        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 48 + m * 18));
            }
        }

        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 106));
        }
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        if (slot.getContainerSlot() < this.inventory.getContainerSize()) {
            return UtilityBeltItem.isValidItem(stack);
        }

        return super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (index < this.inventory.getContainerSize()) {
                if (!this.moveItemStackTo(originalStack, this.inventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 0, this.inventory.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        this.markDirty(player);
        return newStack;
    }

    @Override
    public void clicked(int slotIndex, int button, ClickType clickType, Player player) {
        super.clicked(slotIndex, button, clickType, player);

        if (slotIndex < this.inventory.getContainerSize()) {
            this.markDirty(player);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return UtilityBeltItem.getBelt(player) != null;
    }

    @Override
    public void removed(Player player) {
        this.markDirty(player);
        super.removed(player);
    }

    private void markDirty(Player player) {
        ItemStack belt = UtilityBeltItem.getBelt(player);

        if (belt == null) {
            return;
        }

        StateManager.getStateManager(player).setInventory(player, this.inventory);
    }

    public static class Factory implements MenuProvider {

        public static final Factory INSTANCE = new Factory();

        @Override
        public @NotNull Component getDisplayName() {
            return Component.translatable("container.utility_belt.utility_belt");
        }

        @Nullable
        @Override
        public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
            return new UtilityBeltMenu(i, inventory, new Mutable(UtilityBeltItem.getInventory(UtilityBeltItem.getBelt(player))));
        }
    }
}
