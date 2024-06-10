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
import io.github.jamalam360.utility_belt.screen.UtilityBeltMenu;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class ServerNetworking {

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UtilityBeltPackets.C2S_UPDATE_STATE, (buf, ctx) -> ServerNetworking.handleUpdateState(new C2SUpdateState(buf.readBoolean(), buf.readInt(), buf.readBoolean()), ctx));
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UtilityBeltPackets.C2S_OPEN_SCREEN, (buf, ctx) -> ServerNetworking.handleOpenScreen(new C2SOpenScreen(), ctx));
    }

    public static void sendInventoryToClient(ServerPlayer player, UtilityBeltInventory inventory) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        CompoundTag tag = new CompoundTag();
        tag.put("Inventory", inventory.toTag());
        buf.writeNbt(tag);
        NetworkManager.sendToPlayer(player, UtilityBeltPackets.S2C_UPDATE_BELT_INVENTORY, buf);
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

                UtilityBeltInventory.Mutable inv = stateManager.getMutableInventory(ctx.getPlayer());
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
                    stateManager.setInventory(ctx.getPlayer(), inv);
                    ((Duck.LivingEntity) ctx.getPlayer()).utilitybelt$detectEquipmentUpdates();

                    if (beltSlot != slot) {
                        stateManager.setSelectedBeltSlot(ctx.getPlayer(), beltSlot);
                        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                        buf.writeInt(beltSlot);
                        NetworkManager.sendToPlayer((ServerPlayer) ctx.getPlayer(), UtilityBeltPackets.S2C_SET_BELT_SLOT, buf);
                    } else if (hotbarSlot != ctx.getPlayer().getInventory().selected) {
                        ctx.getPlayer().getInventory().selected = hotbarSlot;
                        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                        buf.writeInt(hotbarSlot);
                        NetworkManager.sendToPlayer((ServerPlayer) ctx.getPlayer(), UtilityBeltPackets.S2C_SET_HOTBAR_SLOT, buf);
                    }
                }
            }
        });
    }

    private static void handleOpenScreen(C2SOpenScreen payload, NetworkManager.PacketContext ctx) {
        ctx.queue(() -> MenuRegistry.openMenu((ServerPlayer) ctx.getPlayer(), UtilityBeltMenu.Factory.INSTANCE));
    }
}
