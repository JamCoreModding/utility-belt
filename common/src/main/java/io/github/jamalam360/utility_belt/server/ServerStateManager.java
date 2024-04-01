package io.github.jamalam360.utility_belt.server;

import io.github.jamalam360.utility_belt.StateManager;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

// Server == Logical Server
public class ServerStateManager extends StateManager {
	private final Map<UUID, PlayerState> playerStates = new Object2ObjectArrayMap<>();

	private PlayerState getState(Player player) {
		return playerStates.computeIfAbsent(player.getUUID(), uuid -> new PlayerState(false, 0));
	}

	@Override
	public void onStartTick(Player player) {
		if (this.hasBelt(player)) {
			getState(player).inventory = UtilityBeltItem.getInventoryFromTag(UtilityBeltItem.getBelt(player));
		} else {
			getState(player).inventory = null;
		}
	}

	public void getInventoryFromTag(Player player) {
		getState(player).inventory = UtilityBeltItem.getInventoryFromTag(UtilityBeltItem.getBelt(player));
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
			assert belt != null;
			state.inventory = UtilityBeltItem.getInventoryFromTag(belt);
		}

		return state.inventory;
	}

	private static class PlayerState {
		private boolean inBelt;
		private int selectedBeltSlot;
		@Nullable
		private UtilityBeltInventory inventory;

		public PlayerState(boolean inBelt, int selectedBeltSlot) {
			this.inBelt = inBelt;
			this.selectedBeltSlot = selectedBeltSlot;
			this.inventory = null;
		}
	}
}
