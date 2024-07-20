package io.github.jamalam360.utility_belt.client.network;

import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.utility_belt.state.StateManager;
import io.github.jamalam360.utility_belt.UtilityBeltInventory.Mutable;
import io.github.jamalam360.utility_belt.network.UtilityBeltPackets;
import io.github.jamalam360.utility_belt.network.UtilityBeltPackets.C2SOpenScreen;
import io.github.jamalam360.utility_belt.network.UtilityBeltPackets.C2SUpdateState;
import io.github.jamalam360.utility_belt.network.UtilityBeltPackets.S2CSetBeltSlot;
import io.github.jamalam360.utility_belt.network.UtilityBeltPackets.S2CSetHotbarSlot;
import io.github.jamalam360.utility_belt.network.UtilityBeltPackets.S2CUpdateBeltInventory;
import io.github.jamalam360.utility_belt.client.UtilityBeltClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.player.Player;

@Environment(EnvType.CLIENT)
public class ClientNetworking {

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UtilityBeltPackets.S2C_SET_BELT_SLOT, S2CSetBeltSlot.STREAM_CODEC, ClientNetworking::handleSetBeltSlot);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UtilityBeltPackets.S2C_SET_HOTBAR_SLOT, S2CSetHotbarSlot.STREAM_CODEC, ClientNetworking::handleSetHotbarSlot);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UtilityBeltPackets.S2C_UPDATE_BELT_INVENTORY, S2CUpdateBeltInventory.STREAM_CODEC, ClientNetworking::handleUpdateBeltInventory);
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
            StateManager.getStateManager(player).setSelectedBeltSlot(player, payload.slot());
        });
    }

    private static void handleSetHotbarSlot(S2CSetHotbarSlot payload, NetworkManager.PacketContext ctx) {
        ctx.queue(() -> ctx.getPlayer().getInventory().selected = payload.slot());
    }

    private static void handleUpdateBeltInventory(S2CUpdateBeltInventory payload, NetworkManager.PacketContext ctx) {
        ctx.queue(() -> {
            Player player = ctx.getPlayer();
            StateManager.getStateManager(player).setInventory(player, new Mutable(payload.inventory()));
        });
    }
}
