package io.github.jamalam360.utility_belt.client;

import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ClientNetworking {
	public static void init() {
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, UtilityBelt.S2C_UPDATE_BELT_INVENTORY, ClientNetworking::handleUpdateBeltInventory);
	}

	public static void sendNewStateToServer(boolean inBelt, int slot, boolean swap) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeBoolean(inBelt);
		buf.writeInt(slot);
		buf.writeBoolean(swap);
		NetworkManager.sendToServer(UtilityBelt.C2S_UPDATE_STATE, buf);
	}

	public static void sendOpenScreenToServer() {
		NetworkManager.sendToServer(UtilityBelt.C2S_OPEN_SCREEN, new FriendlyByteBuf(Unpooled.buffer()));
	}

	private static void handleUpdateBeltInventory(FriendlyByteBuf buf, NetworkManager.PacketContext ctx) {
		CompoundTag inv = buf.readNbt();
		assert inv != null;
		ctx.queue(() -> {
			Player player = ctx.getPlayer();
			ItemStack belt = UtilityBeltItem.getBelt(player);

			if (belt != null) {
				CompoundTag tag = belt.getOrCreateTag();
				tag.put("Inventory", inv.getList("Inventory", CompoundTag.TAG_COMPOUND));
			} else {
				UtilityBelt.LOGGER.warn("Received belt inventory update packet without a belt equipped");
			}
		});
	}
}
