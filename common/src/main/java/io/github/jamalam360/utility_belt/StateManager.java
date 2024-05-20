package io.github.jamalam360.utility_belt;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class StateManager {
	private static StateManager clientInstance;
	private static StateManager serverInstance;

	public static StateManager getClientInstance() {
		return clientInstance;
	}

	public static void setClientInstance(StateManager clientInstance) {
		StateManager.clientInstance = clientInstance;
	}

	public static StateManager getServerInstance() {
		return serverInstance;
	}

	public static void setServerInstance(StateManager serverInstance) {
		StateManager.serverInstance = serverInstance;
	}

	public boolean hasBelt(Player player) {
		ItemStack belt = UtilityBeltItem.getBelt(player);
		return belt != null && belt.is(UtilityBelt.UTILITY_BELT_ITEM.get());
	}

	public void onStartTick(Player player) {
	}

	public abstract boolean isInBelt(Player player);

	public abstract void setInBelt(Player player, boolean inBelt);

	public abstract int getSelectedBeltSlot(Player player);

	public abstract void setSelectedBeltSlot(Player player, int slot);

	public abstract UtilityBeltInventory getInventory(Player player);
}
