package io.github.jamalam360.utility_belt.network;

import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.util.UtilityBeltInventory;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class UtilityBeltPackets {
    public static final ResourceLocation C2S_UPDATE_STATE = UtilityBelt.id("update_state");
    public static final ResourceLocation C2S_OPEN_SCREEN = UtilityBelt.id("open_screen");
    public static final ResourceLocation S2C_SET_BELT_SLOT = UtilityBelt.id("set_belt_slot");
    public static final ResourceLocation S2C_SET_HOTBAR_SLOT = UtilityBelt.id("set_hotbar_slot");
    public static final ResourceLocation S2C_UPDATE_BELT_INVENTORY = UtilityBelt.id("update_belt_inventory");
    public static final ResourceLocation S2C_BELT_UNEQUIPPED = UtilityBelt.id("belt_unequipped");

    public record C2SUpdateState(boolean inBelt, int slot, boolean swapItems) {
        public static C2SUpdateState fromBuf(FriendlyByteBuf buf) {
            return new C2SUpdateState(buf.readBoolean(), buf.readInt(), buf.readBoolean());
        }

        public FriendlyByteBuf toBuf() {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeBoolean(this.inBelt());
            buf.writeInt(this.slot());
            buf.writeBoolean(this.swapItems());
            return buf;
        }
    }

    public record C2SOpenScreen() {
        public FriendlyByteBuf toBuf() {
	        return new FriendlyByteBuf(Unpooled.buffer());
        }
    }

    public record S2CSetBeltSlot(int slot) {
        public static S2CSetBeltSlot fromBuf(FriendlyByteBuf buf) {
            return new S2CSetBeltSlot(buf.readInt());
        }

        public FriendlyByteBuf toBuf() {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeInt(this.slot());
            return buf;
        }
    }

    public record S2CSetHotbarSlot(int slot)  {
        public static S2CSetHotbarSlot fromBuf(FriendlyByteBuf buf) {
            return new S2CSetHotbarSlot(buf.readInt());
        }

        public FriendlyByteBuf toBuf() {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeInt(this.slot());
            return buf;
        }
    }

    public record S2CUpdateBeltInventory(UtilityBeltInventory inventory)  {
        public static S2CUpdateBeltInventory fromBuf(FriendlyByteBuf buf) {
            int size = buf.readInt();
            UtilityBeltInventory.Mutable inv = new UtilityBeltInventory.Mutable(UtilityBeltInventory.empty(size));
            for (int i = 0; i < size; i++) {
                inv.setItem(i, buf.readItem());
            }

            return new S2CUpdateBeltInventory(inv.toImmutable());
        }

        public FriendlyByteBuf toBuf() {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeInt(this.inventory().getContainerSize());
            for (int i = 0; i < this.inventory().getContainerSize(); i++) {
                buf.writeItem(this.inventory().getItem(i));
            }

            return buf;
        }
    }
    
    public record S2CBeltUnequipped() {
        public FriendlyByteBuf toBuf() {
	        return new FriendlyByteBuf(Unpooled.buffer());
        }
    }
}
