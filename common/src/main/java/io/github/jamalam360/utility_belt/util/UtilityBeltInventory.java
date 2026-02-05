package io.github.jamalam360.utility_belt.util;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record UtilityBeltInventory(List<ItemStack> items) {
    public static UtilityBeltInventory empty(int size) {
        return new UtilityBeltInventory(NonNullList.withSize(size, ItemStack.EMPTY));
    }

    public UtilityBeltInventory copyWithSize(int newSize) {
        if (newSize == this.getContainerSize()) {
            return this;
        }

        UtilityBeltInventory copy = new UtilityBeltInventory(NonNullList.withSize(newSize, ItemStack.EMPTY));
        for (int i = 0; i < Math.min(this.getContainerSize(), newSize); i++) {
            copy.items.set(i, this.getItem(i));
        }

        return copy;
    }

    public ItemStack getItem(int index) {
        return items.get(index);
    }

    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UtilityBeltInventory other) {
            return listMatches(items, other.items);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return hashStackList(items);
    }

    public record Mutable(NonNullList<ItemStack> items) implements Container {
        public Mutable(UtilityBeltInventory inv) {
            this(NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY));

            for (int i = 0; i < inv.getContainerSize(); i++) {
                this.setItem(i, inv.getItem(i));
            }
        }

        public UtilityBeltInventory toImmutable() {
            return new UtilityBeltInventory(List.copyOf(this.items));
        }

        @Override
        public ItemStack getItem(int i) {
            return this.items.get(i);
        }

        @Override
        public void setItem(int i, ItemStack stack) {
            this.items.set(i, stack);
        }

        @Override
        public void setChanged() {
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        public ItemStack removeItem(int i, int size) {
            ItemStack stack = this.items.get(i);
            ItemStack removed = stack.copy();

            if (stack.getCount() == size) {
                this.setItem(i, ItemStack.EMPTY);
            } else {
                this.setItem(i, stack.copyWithCount(size));
                removed = stack.copyWithCount(size);
            }

            return removed;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return this.items.remove(slot);
        }

        @Override
        public void clearContent() {
            for (int i = 0; i < this.getContainerSize(); i++) {
                this.setItem(i, ItemStack.EMPTY);
            }
        }

        @Override
        public int getContainerSize() {
            return this.items.size();
        }

        @Override
        public boolean isEmpty() {
            return this.items.isEmpty();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Mutable other) {
                return listMatches(this.items, other.items);
            }

            return false;
        }


        @Override
        public int hashCode() {
            return hashStackList(items);
        }
    }

    private static boolean listMatches(List<ItemStack> l1, List<ItemStack> l2) {
        if (l1.size() != l2.size()) {
            return false;
        }

        for (int i = 0; i < l1.size(); i++) {
            if (!ItemStack.matches(l1.get(i), l2.get(i))) {
                return false;
            }
        }

        return true;
    }

    private static int hashStackList(List<ItemStack> items) {
        int result = 1;

        for (ItemStack item : items) {
            if (item == null || item.isEmpty()) {
                result = 31 * result;
                continue;
            }

            int itemHashCode = item.getItem().hashCode();
            itemHashCode = 31 * itemHashCode + item.getCount();

            CompoundTag tag = item.getTag();
            if (tag != null) {
                itemHashCode = 31 * itemHashCode + tag.hashCode();
            }

            result = 31 * result + itemHashCode;
        }

        return result;
    }
}
