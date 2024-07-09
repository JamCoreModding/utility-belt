package io.github.jamalam360.utility_belt.server;

import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.MenuRegistry;
import io.github.jamalam360.utility_belt.Duck;
import io.github.jamalam360.utility_belt.StateManager;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
import io.github.jamalam360.utility_belt.UtilityBeltPackets;
import io.github.jamalam360.utility_belt.UtilityBeltPackets.C2SOpenScreen;
import io.github.jamalam360.utility_belt.UtilityBeltPackets.C2SUpdateState;
import io.github.jamalam360.utility_belt.UtilityBeltPackets.S2CSetBeltSlot;
import io.github.jamalam360.utility_belt.screen.UtilityBeltMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class ServerNetworking {

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UtilityBeltPackets.C2S_UPDATE_STATE, C2SUpdateState.STREAM_CODEC, ServerNetworking::handleUpdateState);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UtilityBeltPackets.C2S_OPEN_SCREEN, C2SOpenScreen.STREAM_CODEC, ServerNetworking::handleOpenScreen);
    }

    public static void sendInventoryToClient(ServerPlayer player, UtilityBeltInventory inventory) {
        NetworkManager.sendToPlayer(player, new UtilityBeltPackets.S2CUpdateBeltInventory(inventory));
    }

    private static void handleUpdateState(C2SUpdateState payload, NetworkManager.PacketContext ctx) {
        boolean inBelt = payload.inBelt();
        int slot = payload.slot();
        boolean swap = payload.swapItems();

        ctx.queue(() -> {
            var player = ctx.getPlayer();

            int beltSlot = slot;
            StateManager stateManager = StateManager.getStateManager(player);
            stateManager.setInBelt(player, inBelt);
            stateManager.setSelectedBeltSlot(player, beltSlot);
            player.swing(InteractionHand.MAIN_HAND, true);

            if (swap) {
                ItemStack belt = UtilityBeltItem.getBelt(player);

                if (belt == null) {
                    UtilityBelt.LOGGER.warn("Received swap request packet from client without a belt equipped");
                    return;
                }

                UtilityBeltInventory.Mutable inv = stateManager.getMutableInventory(player);
                ItemStack stackInHand = player.getInventory().getItem(player.getInventory().selected);
                int hotbarSlot = player.getInventory().selected;
                ItemStack stackInBelt = inv.getItem(beltSlot);

                if (!stackInHand.isEmpty() && !stateManager.isInBelt(player)) {
                    for (int i = 0; i < 10; i++) {
                        if (player.getInventory().getItem(i).isEmpty()) {
                            hotbarSlot = i;
                            stackInHand = player.getInventory().getItem(i);
                            break;
                        }
                    }
                } else if (!stackInBelt.isEmpty() && stateManager.isInBelt(player)) {
                    for (int i = 0; i < inv.getContainerSize(); i++) {
                        if (inv.getItem(i).isEmpty()) {
                            beltSlot = i;
                            stackInBelt = inv.getItem(i);
                            break;
                        }
                    }
                }

                if (UtilityBeltItem.isValidItem(stackInHand)) {
                    player.getInventory().setItem(hotbarSlot, stackInBelt);
                    inv.setItem(beltSlot, stackInHand);
                    stateManager.setInventory(player, inv);
                    ((Duck.LivingEntity) player).utilitybelt$detectEquipmentUpdates();

                    if (beltSlot != slot) {
                        stateManager.setSelectedBeltSlot(player, beltSlot);
                        NetworkManager.sendToPlayer((ServerPlayer) player, new S2CSetBeltSlot(beltSlot));
                    } else if (hotbarSlot != player.getInventory().selected) {
                        player.getInventory().selected = hotbarSlot;
                        NetworkManager.sendToPlayer((ServerPlayer) player, new S2CSetBeltSlot(hotbarSlot));
                    }
                }
            }
        });
    }

    private static void handleOpenScreen(C2SOpenScreen payload, NetworkManager.PacketContext ctx) {
        ctx.queue(() -> MenuRegistry.openMenu((ServerPlayer) ctx.getPlayer(), UtilityBeltMenu.Factory.INSTANCE));
    }
}
