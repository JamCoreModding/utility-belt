package io.github.jamalam360.utility_belt.state;

import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class ServerStateManager extends StateManager {
	private final Map<UUID, PlayerState> playerStates = new Object2ObjectArrayMap<>();

	private PlayerState getState(Player player) {
		return playerStates.computeIfAbsent(player.getUUID(), uuid -> new PlayerState(false, 0));
	}

	@Override
	public boolean isInBelt(Player player) {
		return getState(player).inBelt;
	}

	@Override
	public void setInBelt(Player player, boolean inBelt) {
		getState(player).inBelt = inBelt;
	}

	@Override
	public int getSelectedBeltSlot(Player player) {
		return getState(player).selectedBeltSlot;
	}

	@Override
	public void setSelectedBeltSlot(Player player, int slot) {
		getState(player).selectedBeltSlot = slot;
	}

	@Override
	public UtilityBeltInventory getInventory(Player player) {
		PlayerState state = getState(player);

		if (state.inventory == null) {
			ItemStack belt = UtilityBeltItem.getBelt(player);

			if (belt == null) {
				return UtilityBeltInventory.empty(UtilityBelt.COMMON_CONFIG.get().initialBeltSize);
			}

			state.inventory = UtilityBeltItem.getInventory(belt);
		}

		return state.inventory;
	}

	@Override
	public void setInventory(Player player, UtilityBeltInventory.Mutable inventory) {
		getState(player).inventory = inventory.toImmutable();
	}

	private static class PlayerState {
		boolean inBelt;
		int selectedBeltSlot;
		@Nullable
		UtilityBeltInventory inventory;

		PlayerState(boolean inBelt, int selectedBeltSlot) {
			this.inBelt = inBelt;
			this.selectedBeltSlot = selectedBeltSlot;
			this.inventory = null;
		}
	}
}
