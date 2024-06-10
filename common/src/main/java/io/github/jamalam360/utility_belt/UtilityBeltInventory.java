package io.github.jamalam360.utility_belt;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record UtilityBeltInventory(List<ItemStack> items) {

    public static final UtilityBeltInventory EMPTY = new UtilityBeltInventory(NonNullList.withSize(4, ItemStack.EMPTY));

    public UtilityBeltInventory {
        if (items.size() != 4) {
            throw new IllegalArgumentException("Utility belt inventory must have exactly 4 items");
        }
    }

    public ItemStack getItem(int index) {
        return items.get(index);
    }

    public int getContainerSize() {
        return 4;
    }

    public UtilityBeltInventory clone() {
        return new UtilityBeltInventory(new ArrayList<>(items));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UtilityBeltInventory other) {
            for (int i = 0; i < items.size(); i++) {
                if (!ItemStack.matches(items.get(i), other.items.get(i))) {
                    return false;
                }
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return hashStackList(items);
    }

    @Override
    public String toString() {
        return invToString("UtilityBeltInventory[", items);
    }

    public static class Mutable extends SimpleContainer {

        public Mutable(UtilityBeltInventory inv) {
            super(4);

            for (int i = 0; i < inv.getContainerSize(); i++) {
                this.setItem(i, inv.getItem(i));
            }
        }

        public UtilityBeltInventory toImmutable() {
            return new UtilityBeltInventory(new ArrayList<>(this.getItems()));
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof UtilityBeltInventory other) {
                for (int i = 0; i < this.getItems().size(); i++) {
                    if (!ItemStack.matches(this.getItems().get(i), other.items.get(i))) {
                        return false;
                    }
                }
            }

            return false;
        }

        @Override
        public String toString() {
            return invToString("UtilityBeltInventory$Mutable[", this.getItems());
        }
    }

    private static String invToString(String prefix, List<ItemStack> items) {
        StringBuilder string = new StringBuilder(prefix);

        for (int i = 0; i < items.size(); i++) {
            string.append(items.get(i));
            if (i < items.size() - 1) {
                string.append(", ");
            }
        }

        return string.toString();
    }

    // Methods added for 1.20.6 --> 1.20.4 backporting ease
    private static int hashItemAndTag(@Nullable ItemStack arg) {
        if (arg != null) {
            int i = 31 + arg.getItem().hashCode();
            return 31 * i + (arg.getTag() == null ? 0 : arg.getTag().hashCode());
        } else {
            return 0;
        }
    }

    private static int hashStackList(List<ItemStack> list) {
        int i = 0;

        for (ItemStack itemstack : list) {
            i = i * 31 + hashItemAndTag(itemstack);
        }

        return i;
    }

    public static UtilityBeltInventory fromTag(ListTag listTag) {
        UtilityBeltInventory.Mutable inv = new UtilityBeltInventory.Mutable(UtilityBeltInventory.EMPTY);

        for (int i = 0; i < listTag.size(); ++i) {
            ItemStack itemStack = ItemStack.of(listTag.getCompound(i));
            if (!itemStack.isEmpty()) {
                inv.setItem(i, itemStack);
            }
        }

        return inv.toImmutable();
    }

    public ListTag toTag() {
        ListTag listTag = new ListTag();

        for (int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemStack = this.getItem(i);
            listTag.add(itemStack.save(new CompoundTag()));
        }

        return listTag;
    }
}
