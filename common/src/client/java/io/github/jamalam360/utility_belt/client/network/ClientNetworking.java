package io.github.jamalam360.utility_belt.client.network;

import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.util.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.util.UtilityBeltInventory.Mutable;
import io.github.jamalam360.utility_belt.client.UtilityBeltClient;
import io.github.jamalam360.utility_belt.network.UtilityBeltPackets;
import io.github.jamalam360.utility_belt.network.UtilityBeltPackets.*;
import io.github.jamalam360.utility_belt.state.StateManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class ClientNetworking {

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UtilityBeltPackets.S2C_SET_BELT_SLOT, ClientNetworking::handleSetBeltSlot);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UtilityBeltPackets.S2C_SET_HOTBAR_SLOT, ClientNetworking::handleSetHotbarSlot);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UtilityBeltPackets.S2C_UPDATE_BELT_INVENTORY, ClientNetworking::handleUpdateBeltInventory);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UtilityBeltPackets.S2C_BELT_UNEQUIPPED, ClientNetworking::handleBeltUnequipped);
    }

    public static void sendNewStateToServer(boolean inBelt, int slot, boolean swapItems) {
        if (swapItems && !UtilityBeltClient.CLIENT_CONFIG.get().useSneakSwapping) {
            swapItems = false;
        }

        UtilityBeltPackets.C2SUpdateState packet = new C2SUpdateState(inBelt, slot, swapItems);
        NetworkManager.sendToServer(UtilityBeltPackets.C2S_UPDATE_STATE, packet.toBuf());
    }

    public static void sendOpenScreenToServer() {
        NetworkManager.sendToServer(UtilityBeltPackets.C2S_OPEN_SCREEN, new C2SOpenScreen().toBuf());
    }

    private static void handleSetBeltSlot(FriendlyByteBuf buf, NetworkManager.PacketContext ctx) {
        S2CSetBeltSlot payload = S2CSetBeltSlot.fromBuf(buf);
        ctx.queue(() -> {
            Player player = ctx.getPlayer();
            StateManager manager = StateManager.getStateManager(player);
            UtilityBeltInventory inventory = manager.getInventory(player);
            
            if (payload.slot() < 0 || payload.slot() >= inventory.getContainerSize()) {
                UtilityBelt.LOGGER.warn("Suspicious request from server to set an invalid belt slot: {}", payload.slot());
                return;
            }

            manager.setSelectedBeltSlot(player, payload.slot());
        });
    }

    private static void handleSetHotbarSlot(FriendlyByteBuf buf, NetworkManager.PacketContext ctx) {
        S2CSetHotbarSlot payload = S2CSetHotbarSlot.fromBuf(buf);
        ctx.queue(() -> ctx.getPlayer().getInventory().selected = payload.slot());
    }

    private static void handleUpdateBeltInventory(FriendlyByteBuf buf, NetworkManager.PacketContext ctx) {
        S2CUpdateBeltInventory payload = S2CUpdateBeltInventory.fromBuf(buf);
        ctx.queue(() -> {
            Player player = ctx.getPlayer();
            StateManager.getStateManager(player).setInventory(player, new Mutable(payload.inventory()));
        });
    }
    
    private static void handleBeltUnequipped(FriendlyByteBuf buf, NetworkManager.PacketContext ctx) {
        ctx.queue(() -> {
            Player player = ctx.getPlayer();
            StateManager.getStateManager(player).setInBelt(player, false);
            StateManager.getStateManager(player).setSelectedBeltSlot(player, 0);
        });
    }
}
