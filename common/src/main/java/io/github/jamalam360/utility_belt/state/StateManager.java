package io.github.jamalam360.utility_belt.state;

import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
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
	
	public static void setClientInstance(StateManager clientInstance) {
		StateManager.clientInstance = clientInstance;
	}
	
	public static void setServerInstance(StateManager serverInstance) {
		StateManager.serverInstance = serverInstance;
	}

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
}
