package io.github.jamalam360.utility_belt.state;

import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.util.UtilityBeltInventory;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public final class EmptyStateManager extends StateManager {

    public static final EmptyStateManager INSTANCE = new EmptyStateManager();


    @Override
    public boolean isInBelt(Player player) {
        UtilityBelt.LOGGER.warn("Suspicious call to EmptyStateManager#isInBelt");
        return false;
    }

    @Override
    public void setInBelt(Player player, boolean inBelt) {
        UtilityBelt.LOGGER.warn("Suspicious call to EmptyStateManager#setInBelt");
    }

    @Override
    public int getSelectedBeltSlot(Player player) {
        UtilityBelt.LOGGER.warn("Suspicious call to EmptyStateManager#getSelectedBeltSlot");
        return 0;
    }

    @Override
    public void setSelectedBeltSlot(Player player, int slot) {
        UtilityBelt.LOGGER.warn("Suspicious call to EmptyStateManager#setSelectedBeltSlot");
    }

    @Override
    public UtilityBeltInventory getInventory(Player player) {
        UtilityBelt.LOGGER.warn("Suspicious call to EmptyStateManager#getInventory");
        return new UtilityBeltInventory(List.of());
    }

    @Override
    public void setInventory(Player player, UtilityBeltInventory.Mutable inventory) {
        UtilityBelt.LOGGER.warn("Suspicious call to EmptyStateManager#setInventory");
    }
}
