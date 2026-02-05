package io.github.jamalam360.utility_belt.content;

import io.github.jamalam360.utility_belt.content.register.ModComponents;
import io.github.jamalam360.utility_belt.content.register.ModMenus;
import io.github.jamalam360.utility_belt.util.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.util.UtilityBeltInventory.Mutable;
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
        this(syncId, playerInventory, new Mutable(UtilityBeltInventory.empty(ModComponents.getBeltSize(UtilityBeltItem.getBelt(playerInventory.player)))));
    }

    public UtilityBeltMenu(int syncId, Inventory playerInventory, UtilityBeltInventory.Mutable inventory) {
        super(ModMenus.MENU_TYPE.get(), syncId);
        this.inventory = inventory;
        int rows = this.getBeltRows();

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 106 + (rows - 1) * 18));
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + (i + 1) * 9, 8 + j * 18, 48 + (rows - 1) * 18 + i * 18));
            }
        }

        int x = 0;
        int y = 0;
        while ((x + y * 9) < this.inventory.getContainerSize()) {
            this.addSlot(new Slot(inventory, x + y * 9, 8 + x * 18, 17 + y * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return UtilityBeltItem.isValidItem(stack);
                }
            });

            x += 1;
            if (x == 9) {
                x = 0;
                y += 1;
            }
        }
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        if (this.isBeltSlot(slot.index)) {
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
            if (this.isBeltSlot(index)) {
                if (!this.moveItemStackTo(originalStack, 0, 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 36, 36 + this.inventory.getContainerSize(), false)) {
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

        if (this.isBeltSlot(slotIndex)) {
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

    public int getBeltInventorySize() {
        return this.inventory.getContainerSize();
    }

    public int getBeltRows() {
		return Math.max(1, (int) Math.ceil((float) this.getBeltInventorySize() / 9));
	}

    private boolean isBeltSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= this.slots.size()) {
            return false;
        }

	    return this.getSlot(slotIndex).container instanceof Mutable;
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
            return new UtilityBeltMenu(i, inventory, new Mutable(ModComponents.getBeltInventory(UtilityBeltItem.getBelt(player))));
        }
    }
}
