package io.github.jamalam360.utility_belt.client.network;

import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.UtilityBeltInventory.Mutable;
import io.github.jamalam360.utility_belt.client.UtilityBeltClient;
import io.github.jamalam360.utility_belt.network.UtilityBeltPackets;
import io.github.jamalam360.utility_belt.network.UtilityBeltPackets.*;
import io.github.jamalam360.utility_belt.state.StateManager;
import net.minecraft.world.entity.player.Player;

public class ClientNetworking {

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UtilityBeltPackets.S2C_SET_BELT_SLOT, S2CSetBeltSlot.STREAM_CODEC, ClientNetworking::handleSetBeltSlot);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UtilityBeltPackets.S2C_SET_HOTBAR_SLOT, S2CSetHotbarSlot.STREAM_CODEC, ClientNetworking::handleSetHotbarSlot);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UtilityBeltPackets.S2C_UPDATE_BELT_INVENTORY, S2CUpdateBeltInventory.STREAM_CODEC, ClientNetworking::handleUpdateBeltInventory);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UtilityBeltPackets.S2C_BELT_UNEQUIPPED, S2CBeltUnequipped.STREAM_CODEC, ClientNetworking::handleBeltUnequipped);
    }

    public static void sendNewStateToServer(boolean inBelt, int slot, boolean swapItems) {
        if (swapItems && !UtilityBeltClient.CONFIG.get().useSneakSwapping) {
            swapItems = false;
        }

        UtilityBeltPackets.C2SUpdateState packet = new C2SUpdateState(inBelt, slot, swapItems);
        NetworkManager.sendToServer(packet);
    }

    public static void sendOpenScreenToServer() {
        NetworkManager.sendToServer(new C2SOpenScreen());
    }

    private static void handleSetBeltSlot(S2CSetBeltSlot payload, NetworkManager.PacketContext ctx) {
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

    private static void handleSetHotbarSlot(S2CSetHotbarSlot payload, NetworkManager.PacketContext ctx) {
        ctx.queue(() -> ctx.getPlayer().getInventory().setSelectedSlot(payload.slot()));
    }

    private static void handleUpdateBeltInventory(S2CUpdateBeltInventory payload, NetworkManager.PacketContext ctx) {
        ctx.queue(() -> {
            Player player = ctx.getPlayer();
            StateManager.getStateManager(player).setInventory(player, new Mutable(payload.inventory()));
        });
    }
    
    private static void handleBeltUnequipped(S2CBeltUnequipped packet, NetworkManager.PacketContext ctx) {
        ctx.queue(() -> {
            Player player = ctx.getPlayer();
            StateManager.getStateManager(player).setInBelt(player, false);
            StateManager.getStateManager(player).setSelectedBeltSlot(player, 0);
        });
    }
}
