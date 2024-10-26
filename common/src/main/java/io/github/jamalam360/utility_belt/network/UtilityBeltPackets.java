package io.github.jamalam360.utility_belt.network;

import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class UtilityBeltPackets {

    public static final CustomPacketPayload.Type<C2SUpdateState> C2S_UPDATE_STATE = new CustomPacketPayload.Type<>(UtilityBelt.id("update_state"));
    public static final CustomPacketPayload.Type<C2SOpenScreen> C2S_OPEN_SCREEN = new CustomPacketPayload.Type<>(UtilityBelt.id("open_screen"));
    public static final CustomPacketPayload.Type<S2CSetBeltSlot> S2C_SET_BELT_SLOT = new CustomPacketPayload.Type<>(UtilityBelt.id("set_belt_slot"));
    public static final CustomPacketPayload.Type<S2CSetHotbarSlot> S2C_SET_HOTBAR_SLOT = new CustomPacketPayload.Type<>(UtilityBelt.id("set_hotbar_slot"));
    public static final CustomPacketPayload.Type<S2CUpdateBeltInventory> S2C_UPDATE_BELT_INVENTORY = new CustomPacketPayload.Type<>(UtilityBelt.id("update_belt_inventory"));
    public static final CustomPacketPayload.Type<S2CBeltUnequipped> S2C_BELT_UNEQUIPPED = new CustomPacketPayload.Type<>(UtilityBelt.id("belt_unequipped"));

    public record C2SUpdateState(boolean inBelt, int slot, boolean swapItems) implements CustomPacketPayload {

        public static final StreamCodec<RegistryFriendlyByteBuf, C2SUpdateState> STREAM_CODEC = StreamCodec.of(
              (buf, payload) -> {
                  buf.writeBoolean(payload.inBelt());
                  buf.writeInt(payload.slot());
                  buf.writeBoolean(payload.swapItems());
              },
              (buf) -> new C2SUpdateState(buf.readBoolean(), buf.readInt(), buf.readBoolean())
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return C2S_UPDATE_STATE;
        }
    }

    public record C2SOpenScreen() implements CustomPacketPayload {

        public static final StreamCodec<RegistryFriendlyByteBuf, C2SOpenScreen> STREAM_CODEC = StreamCodec.of(
              (buf, payload) -> {
              },
              (buf) -> new C2SOpenScreen()
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return C2S_OPEN_SCREEN;
        }
    }

    public record S2CSetBeltSlot(int slot) implements CustomPacketPayload {

        public static final StreamCodec<RegistryFriendlyByteBuf, S2CSetBeltSlot> STREAM_CODEC = StreamCodec.of(
              (buf, payload) -> buf.writeInt(payload.slot()),
              (buf) -> new S2CSetBeltSlot(buf.readInt())
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return S2C_SET_BELT_SLOT;
        }
    }

    public record S2CSetHotbarSlot(int slot) implements CustomPacketPayload {

        public static final StreamCodec<RegistryFriendlyByteBuf, S2CSetHotbarSlot> STREAM_CODEC = StreamCodec.of(
              (buf, payload) -> buf.writeInt(payload.slot()),
              (buf) -> new S2CSetHotbarSlot(buf.readInt())
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return S2C_SET_HOTBAR_SLOT;
        }
    }

    public record S2CUpdateBeltInventory(UtilityBeltInventory inventory) implements CustomPacketPayload {

        public static final StreamCodec<RegistryFriendlyByteBuf, S2CUpdateBeltInventory> STREAM_CODEC = UtilityBeltInventory.STREAM_CODEC.map(S2CUpdateBeltInventory::new, S2CUpdateBeltInventory::inventory);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return S2C_UPDATE_BELT_INVENTORY;
        }
    }
    
    public record S2CBeltUnequipped() implements CustomPacketPayload {
        
        public static final StreamCodec<RegistryFriendlyByteBuf, S2CBeltUnequipped> STREAM_CODEC = StreamCodec.unit(new S2CBeltUnequipped());
        
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return S2C_BELT_UNEQUIPPED;
        }
    }
}
