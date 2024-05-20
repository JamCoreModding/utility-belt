package io.github.jamalam360.utility_belt;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class UtilityBeltInventory extends SimpleContainer {

    public static final Codec<UtilityBeltInventory> CODEC = Codec
          .list(ItemStack.CODEC)
          .xmap(UtilityBeltInventory::new, UtilityBeltInventory::getItems);

    public static final StreamCodec<RegistryFriendlyByteBuf, UtilityBeltInventory> STREAM_CODEC = StreamCodec
          .composite(
                ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list(4)),
                UtilityBeltInventory::getItems,
                UtilityBeltInventory::new
          );


    public UtilityBeltInventory() {
        super(4);
    }

    private UtilityBeltInventory(List<ItemStack> stacks) {
        super(4);

        for (int i = 0; i < stacks.size(); i++) {
            this.setItem(i, stacks.get(i));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UtilityBeltInventory other) {
            if (other.getContainerSize() == this.getContainerSize()) {
                for (int i = 0; i < this.getContainerSize(); i++) {
                    if (!ItemStack.matches(this.getItem(i), other.getItem(i))) {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < this.getContainerSize(); i++) {
            hash += this.getItem(i).hashCode();
        }

        return hash;
    }

    @Override
    public UtilityBeltInventory clone() {
        UtilityBeltInventory inv = new UtilityBeltInventory();
        for (int i = 0; i < this.getContainerSize(); i++) {
            inv.setItem(i, this.getItem(i).copy());
        }

        return inv;
    }
}
