package io.github.jamalam360.utility_belt;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class StateManager {
	private static StateManager clientInstance = EmptyStateManager.INSTANCE;
	private static StateManager serverInstance = EmptyStateManager.INSTANCE;

	public static StateManager getStateManager(LivingEntity entity) {
		return getStateManager(entity.level());
	}

	public static StateManager getStateManager(Level level) {
		return getStateManager(level.isClientSide());
	}

	public static StateManager getStateManager(boolean isClientSide) {
		return isClientSide ? clientInstance : serverInstance;
	}

	@Deprecated
	public static StateManager getClientInstance() {
		return clientInstance;
	}

	public static void setClientInstance(StateManager clientInstance) {
		StateManager.clientInstance = clientInstance;
	}

	@Deprecated
	public static StateManager getServerInstance() {
		return serverInstance;
	}

	public static void setServerInstance(StateManager serverInstance) {
		StateManager.serverInstance = serverInstance;
	}

	//--

	public boolean hasBelt(Player player) {
		ItemStack belt = UtilityBeltItem.getBelt(player);
		return belt != null && belt.is(UtilityBelt.UTILITY_BELT_ITEM.get());
	}

	public abstract boolean isInBelt(Player player);

	public abstract void setInBelt(Player player, boolean inBelt);

	public abstract int getSelectedBeltSlot(Player player);

	public abstract void setSelectedBeltSlot(Player player, int slot);

	public abstract UtilityBeltInventory getInventory(Player player);

	public UtilityBeltInventory.Mutable getMutableInventory(Player player) {
		return new UtilityBeltInventory.Mutable(this.getInventory(player));
	}

	public abstract void setInventory(Player player, UtilityBeltInventory.Mutable inventory);

	public boolean isEmptyManager() {
		return this == EmptyStateManager.INSTANCE;
	}
}
