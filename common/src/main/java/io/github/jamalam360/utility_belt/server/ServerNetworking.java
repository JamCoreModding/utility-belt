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

    public static void sendNewInventoryToClient(ServerPlayer player) {
//		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
//		UtilityBeltInventory inv = StateManager.getServerInstance().getInventory(player);
//		CompoundTag tag = new CompoundTag();
//		tag.put("Inventory", inv.createTag());
//		buf.writeNbt(tag);
//		NetworkManager.sendToPlayer(player, UtilityBelt.S2C_UPDATE_INV, buf);
    }

    private static void handleUpdateState(C2SUpdateState payload, NetworkManager.PacketContext ctx) {
        boolean inBelt = payload.inBelt();
        int slot = payload.slot();
        boolean swap = payload.swapItems();

        ctx.queue(() -> {
            int beltSlot = slot;
            StateManager stateManager = StateManager.getServerInstance();
            stateManager.setInBelt(ctx.getPlayer(), inBelt);
            stateManager.setSelectedBeltSlot(ctx.getPlayer(), beltSlot);
            ctx.getPlayer().swing(InteractionHand.MAIN_HAND, true);

            if (swap) {
                ItemStack belt = UtilityBeltItem.getBelt(ctx.getPlayer());

                if (belt == null) {
                    UtilityBelt.LOGGER.warn("Received swap request packet from client without a belt equipped");
                    return;
                }

                UtilityBeltInventory inv = stateManager.getInventory(ctx.getPlayer());
                ItemStack stackInHand = ctx.getPlayer().getInventory().getItem(ctx.getPlayer().getInventory().selected);
                int hotbarSlot = ctx.getPlayer().getInventory().selected;
                ItemStack stackInBelt = inv.getItem(beltSlot);

                if (!stackInHand.isEmpty() && !stateManager.isInBelt(ctx.getPlayer())) {
                    for (int i = 0; i < 10; i++) {
                        if (ctx.getPlayer().getInventory().getItem(i).isEmpty()) {
                            hotbarSlot = i;
                            stackInHand = ctx.getPlayer().getInventory().getItem(i);
                            break;
                        }
                    }
                } else if (!stackInBelt.isEmpty() && stateManager.isInBelt(ctx.getPlayer())) {
                    for (int i = 0; i < inv.getContainerSize(); i++) {
                        if (inv.getItem(i).isEmpty()) {
                            beltSlot = i;
                            stackInBelt = inv.getItem(i);
                            break;
                        }
                    }
                }

                if (UtilityBeltItem.isValidItem(stackInHand)) {
                    ctx.getPlayer().getInventory().setItem(hotbarSlot, stackInBelt);
                    inv.setItem(beltSlot, stackInHand);
                    ((Duck.LivingEntity) ctx.getPlayer()).utilitybelt$detectEquipmentUpdates();

                    if (beltSlot != slot) {
                        stateManager.setSelectedBeltSlot(ctx.getPlayer(), beltSlot);
                        NetworkManager.sendToPlayer((ServerPlayer) ctx.getPlayer(), new S2CSetBeltSlot(beltSlot));
                    } else if (hotbarSlot != ctx.getPlayer().getInventory().selected) {
                        ctx.getPlayer().getInventory().selected = hotbarSlot;
                        NetworkManager.sendToPlayer((ServerPlayer) ctx.getPlayer(), new S2CSetBeltSlot(hotbarSlot));
                    }
                }
            }
        });
    }

    private static void handleOpenScreen(C2SOpenScreen payload, NetworkManager.PacketContext ctx) {
        ctx.queue(() -> MenuRegistry.openMenu((ServerPlayer) ctx.getPlayer(), UtilityBeltMenu.Factory.INSTANCE));
    }
}
