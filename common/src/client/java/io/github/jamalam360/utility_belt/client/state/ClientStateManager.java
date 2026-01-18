package io.github.jamalam360.utility_belt.client.state;

import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
import io.github.jamalam360.utility_belt.state.StateManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ClientStateManager extends StateManager {
	private boolean isInUtilityBelt = false;
	private int selectedSlot = 0;

	@Override
	public boolean isInBelt(Player player) {
		return this.isInUtilityBelt;
	}

	@Override
	public void setInBelt(Player player, boolean inBelt) {
		this.isInUtilityBelt = inBelt;
	}

	@Override
	public int getSelectedBeltSlot(Player player) {
		return this.selectedSlot;
	}

	@Override
	public void setSelectedBeltSlot(Player player, int slot) {
		this.selectedSlot = slot;
	}

	@Override
	public UtilityBeltInventory getInventory(Player player) {
		ItemStack belt = UtilityBeltItem.getBelt(player);

		if (belt == null) {
			return UtilityBeltInventory.empty(UtilityBelt.COMMON_CONFIG.get().initialBeltSize);
		} else {
			return UtilityBeltItem.getInventory(belt);
		}
	}

	@Override
	public void setInventory(Player player, UtilityBeltInventory.Mutable inventory) {
		ItemStack belt = UtilityBeltItem.getBelt(player);

		if (belt != null) {
			UtilityBeltItem.setInventory(belt, inventory.toImmutable());
		}
	}
}
