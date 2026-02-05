package io.github.jamalam360.utility_belt.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.MenuRegistry;
import io.github.jamalam360.utility_belt.util.Duck;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.util.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.content.UtilityBeltItem;
import io.github.jamalam360.utility_belt.network.UtilityBeltPackets.C2SUpdateState;
import io.github.jamalam360.utility_belt.network.UtilityBeltPackets.S2CSetBeltSlot;
import io.github.jamalam360.utility_belt.network.UtilityBeltPackets.S2CSetHotbarSlot;
import io.github.jamalam360.utility_belt.content.UtilityBeltMenu;
import io.github.jamalam360.utility_belt.state.StateManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class ServerNetworking {

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UtilityBeltPackets.C2S_UPDATE_STATE, ServerNetworking::handleUpdateState);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UtilityBeltPackets.C2S_OPEN_SCREEN, ServerNetworking::handleOpenScreen);
    }
    
    public static void sendInventoryToClient(ServerPlayer player, UtilityBeltInventory inventory) {
        NetworkManager.sendToPlayer(player, UtilityBeltPackets.S2C_UPDATE_BELT_INVENTORY, new UtilityBeltPackets.S2CUpdateBeltInventory(inventory).toBuf());
    }
    
    public static void sendBeltUnequippedToClient(ServerPlayer player) {
        NetworkManager.sendToPlayer(player, UtilityBeltPackets.S2C_BELT_UNEQUIPPED, new UtilityBeltPackets.S2CBeltUnequipped().toBuf());
    }

    private static void handleUpdateState(FriendlyByteBuf buf, NetworkManager.PacketContext ctx) {
        C2SUpdateState payload = C2SUpdateState.fromBuf(buf);
        boolean inBelt = payload.inBelt();
        int slot = payload.slot();
        boolean swap = payload.swapItems();

        ctx.queue(() -> {
            var player = ctx.getPlayer();
            int beltSlot = slot;
            StateManager stateManager = StateManager.getStateManager(player);

            if (beltSlot < 0 || beltSlot >= stateManager.getInventory(player).getContainerSize()) {
                UtilityBelt.LOGGER.warn("Suspicious request from client to set an invalid belt slot: {}", beltSlot);
                return;
            }

            if (swap) {
                ItemStack belt = UtilityBeltItem.getBelt(player);

                if (belt == null) {
                    UtilityBelt.LOGGER.warn("Suspicious swap request packet from client without a belt equipped");
                    return;
                }

                UtilityBeltInventory.Mutable inv = stateManager.getMutableInventory(player);
                int hotbarSlot = player.getInventory().selected;
                ItemStack stackInHand = player.getInventory().getItem(hotbarSlot);
                ItemStack stackInBelt = inv.getItem(beltSlot);

                if (!stackInHand.isEmpty() && !stackInBelt.isEmpty()) {
                    if (stateManager.isInBelt(player)) {
                    for (int i = 0; i < 10; i++) {
                        if (player.getInventory().getItem(i).isEmpty()) {
                            hotbarSlot = i;
                            stackInHand = player.getInventory().getItem(i);
                            break;
                        }
                    }
                    } else {
                        for (int i = 0; i < inv.getContainerSize(); i++) {
                            if (inv.getItem(i).isEmpty()) {
                                beltSlot = i;
                                stackInBelt = inv.getItem(i);
                                break;
                            }
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
                        NetworkManager.sendToPlayer((ServerPlayer) player, UtilityBeltPackets.S2C_SET_BELT_SLOT, new S2CSetBeltSlot(beltSlot).toBuf());
                    } else if (hotbarSlot != player.getInventory().selected) {
                        player.getInventory().selected = hotbarSlot;
                        NetworkManager.sendToPlayer((ServerPlayer) player, UtilityBeltPackets.S2C_SET_HOTBAR_SLOT, new S2CSetHotbarSlot(hotbarSlot).toBuf());
                    }
                }
            }

            stateManager.setInBelt(player, inBelt);
            stateManager.setSelectedBeltSlot(player, beltSlot);
            player.swing(InteractionHand.MAIN_HAND, true);
        });
    }

    private static void handleOpenScreen(FriendlyByteBuf buf, NetworkManager.PacketContext ctx) {
        ctx.queue(() -> MenuRegistry.openMenu((ServerPlayer) ctx.getPlayer(), UtilityBeltMenu.Factory.INSTANCE));
    }
}
