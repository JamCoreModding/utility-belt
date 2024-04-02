package io.github.jamalam360.utility_belt.client;

import io.github.jamalam360.utility_belt.StateManager;
import io.github.jamalam360.utility_belt.server.ServerStateManager;
import net.minecraft.world.entity.player.Player;

// A patch fix. Eventually I want to remove the client/server state manager distinction.
public class ClientDelegatingServerStateManager extends ServerStateManager {
	@Override
	public boolean isInBelt(Player player) {
		return StateManager.getClientInstance().isInBelt(player);
	}

	@Override
	public void setInBelt(Player player, boolean inBelt) {
		StateManager.getClientInstance().setInBelt(player, inBelt);
	}

	@Override
	public int getSelectedBeltSlot(Player player) {
		return StateManager.getClientInstance().getSelectedBeltSlot(player);
	}

	@Override
	public void setSelectedBeltSlot(Player player, int slot) {
		StateManager.getClientInstance().setSelectedBeltSlot(player, slot);
	}
}
