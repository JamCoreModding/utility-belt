package io.github.jamalam360.utility_belt.server;

import dev.architectury.networking.NetworkManager;
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

	public static void sendNewBeltInventoryToClient(ServerPlayer holder) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		UtilityBeltInventory inv = StateManager.getServerInstance().getInventory(holder);
		CompoundTag tag = new CompoundTag();
		tag.put("Inventory", inv.createTag());
		buf.writeNbt(tag);
		NetworkManager.sendToPlayer(holder, UtilityBelt.S2C_UPDATE_BELT_INVENTORY, buf);
	}

	private static void handleUpdateState(FriendlyByteBuf buf, NetworkManager.PacketContext ctx) {
		boolean inBelt = buf.readBoolean();
		int slot = buf.readInt();
		boolean swap = buf.readBoolean();

		ctx.queue(() -> {
			StateManager stateManager = StateManager.getServerInstance();
			stateManager.setInBelt(ctx.getPlayer(), inBelt);
			stateManager.setSelectedBeltSlot(ctx.getPlayer(), slot);
			ctx.getPlayer().swing(InteractionHand.MAIN_HAND);

			if (swap) {
				ItemStack belt = UtilityBeltItem.getBelt(ctx.getPlayer());

				if (belt == null) {
					UtilityBelt.LOGGER.warn("Received swap packet from client without a belt equipped");
					return;
				}

				UtilityBeltInventory inv = stateManager.getInventory(ctx.getPlayer());
				ItemStack stackInHand = ctx.getPlayer().getInventory().getItem(ctx.getPlayer().getInventory().selected);
				ItemStack stackInBelt = inv.getItem(slot);

				if (UtilityBeltItem.isValidItem(stackInHand)) {
					ctx.getPlayer().setItemInHand(ctx.getPlayer().getUsedItemHand(), stackInBelt);
					inv.setItem(slot, stackInHand);
					((Duck.LivingEntity) ctx.getPlayer()).utilitybelt$detectEquipmentUpdates();
				}
			}
		});
	}

	private static void handleOpenScreen(FriendlyByteBuf buf, NetworkManager.PacketContext ctx) {
		ctx.queue(() -> ctx.getPlayer().openMenu(UtilityBeltMenu.Factory.INSTANCE));
	}
}
