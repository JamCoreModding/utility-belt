package io.github.jamalam360.utility_belt.client;

import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.utility_belt.StateManager;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.UtilityBeltInventory.Mutable;
import io.github.jamalam360.utility_belt.UtilityBeltPackets;
import io.github.jamalam360.utility_belt.UtilityBeltPackets.S2CSetBeltSlot;
import io.github.jamalam360.utility_belt.UtilityBeltPackets.S2CSetHotbarSlot;
import io.github.jamalam360.utility_belt.UtilityBeltPackets.S2CUpdateBeltInventory;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

@Environment(EnvType.CLIENT)
public class ClientNetworking {

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UtilityBeltPackets.S2C_SET_BELT_SLOT, (buf, ctx) -> ClientNetworking.handleSetBeltSlot(new S2CSetBeltSlot(buf.readInt()), ctx));
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UtilityBeltPackets.S2C_SET_HOTBAR_SLOT, (buf, ctx) -> ClientNetworking.handleSetHotbarSlot(new S2CSetHotbarSlot(buf.readInt()), ctx));
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UtilityBeltPackets.S2C_UPDATE_BELT_INVENTORY, (buf, ctx) -> ClientNetworking.handleUpdateBeltInventory(new S2CUpdateBeltInventory(UtilityBeltInventory.fromTag(buf.readNbt().getList("Inventory", Tag.TAG_COMPOUND))), ctx));
    }

    public static void sendNewStateToServer(boolean inBelt, int slot, boolean swapItems) {
        if (swapItems && !UtilityBelt.CONFIG.get().useSneakSwapping) {
            swapItems = false;
        }

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(inBelt);
        buf.writeInt(slot);
        buf.writeBoolean(swapItems);
        NetworkManager.sendToServer(UtilityBeltPackets.C2S_UPDATE_STATE, buf);
    }

    public static void sendOpenScreenToServer() {
        NetworkManager.sendToServer(UtilityBeltPackets.C2S_OPEN_SCREEN, new FriendlyByteBuf(Unpooled.buffer()));
    }

    private static void handleSetBeltSlot(S2CSetBeltSlot payload, NetworkManager.PacketContext ctx) {
        StateManager.getClientInstance().setSelectedBeltSlot(ctx.getPlayer(), payload.slot());
    }

    private static void handleSetHotbarSlot(S2CSetHotbarSlot payload, NetworkManager.PacketContext ctx) {
        ctx.getPlayer().getInventory().selected = payload.slot();
    }

    private static void handleUpdateBeltInventory(S2CUpdateBeltInventory payload, NetworkManager.PacketContext ctx) {
        StateManager.getClientInstance().setInventory(ctx.getPlayer(), new Mutable(payload.inventory()));
    }
}
