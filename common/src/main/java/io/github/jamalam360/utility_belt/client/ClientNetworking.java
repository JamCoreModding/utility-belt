package io.github.jamalam360.utility_belt.client;

import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.utility_belt.StateManager;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ClientNetworking {
	public static void init() {
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, UtilityBelt.S2C_UPDATE_INV, ClientNetworking::handleUpdateInventory);
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, UtilityBelt.S2C_SET_BELT_SLOT, ClientNetworking::handleSetBeltSlot);
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, UtilityBelt.S2C_SET_HOTBAR_SLOT, ClientNetworking::handleSetHotbarSlot);
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

	private static void handleUpdateInventory(FriendlyByteBuf buf, NetworkManager.PacketContext ctx) {
		CompoundTag tag = buf.readNbt();
		ItemStack belt = UtilityBeltItem.getBelt(ctx.getPlayer());
		belt.getTag().put("Inventory", tag.getList("Inventory", CompoundTag.TAG_COMPOUND));
	}

	private static void handleSetBeltSlot(FriendlyByteBuf buf, NetworkManager.PacketContext ctx) {
		int slot = buf.readInt();
		StateManager.getClientInstance().setSelectedBeltSlot(ctx.getPlayer(), slot);
	}

	private static void handleSetHotbarSlot(FriendlyByteBuf buf, NetworkManager.PacketContext ctx) {
		ctx.getPlayer().getInventory().selected = buf.readInt();
	}
}
