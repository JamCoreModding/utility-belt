package io.github.jamalam360.utility_belt;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public record UtilityBeltInventory(List<ItemStack> items) {

    public static final UtilityBeltInventory EMPTY = new UtilityBeltInventory(NonNullList.withSize(4, ItemStack.EMPTY));
    public static final Codec<UtilityBeltInventory> CODEC = Codec
          .list(ItemStack.OPTIONAL_CODEC)
          .xmap(UtilityBeltInventory::new, UtilityBeltInventory::items);

    public static final StreamCodec<RegistryFriendlyByteBuf, UtilityBeltInventory> STREAM_CODEC = StreamCodec
          .composite(
                ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(4)),
                UtilityBeltInventory::items,
                UtilityBeltInventory::new
          );

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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UtilityBeltInventory other) {
            return ItemStack.listMatches(items, other.items);
        }

        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int hashCode() {
        return ItemStack.hashStackList(items);
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
                return ItemStack.listMatches(this.getItems(), other.items);
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
            string.append(" {");
            string.append(items.get(i).getComponents().get(DataComponents.DAMAGE));
            string.append("}");
            if (i < items.size() - 1) {
                string.append(", ");
            }
        }

        return string.toString();
    }
}
