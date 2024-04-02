package io.github.jamalam360.utility_belt.server;

import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.MenuRegistry;
import io.github.jamalam360.utility_belt.*;
import io.github.jamalam360.utility_belt.screen.UtilityBeltMenu;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class ServerNetworking {
	public static void init() {
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, UtilityBelt.C2S_UPDATE_STATE, ServerNetworking::handleUpdateState);
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, UtilityBelt.C2S_OPEN_SCREEN, ServerNetworking::handleOpenScreen);
	}

	public static void sendNewInventoryToClient(ServerPlayer player) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		UtilityBeltInventory inv = StateManager.getServerInstance().getInventory(player);
		CompoundTag tag = new CompoundTag();
		tag.put("Inventory", inv.createTag());
		buf.writeNbt(tag);
		NetworkManager.sendToPlayer(player, UtilityBelt.S2C_UPDATE_INV, buf);
	}

	private static void handleUpdateState(FriendlyByteBuf buf, NetworkManager.PacketContext ctx) {
		boolean inBelt = buf.readBoolean();
		int slot = buf.readInt();
		boolean swap = buf.readBoolean();

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
				}
			}
		});
	}

	private static void handleOpenScreen(FriendlyByteBuf buf, NetworkManager.PacketContext ctx) {
		ctx.queue(() -> MenuRegistry.openMenu((ServerPlayer) ctx.getPlayer(), UtilityBeltMenu.Factory.INSTANCE));
	}
}
