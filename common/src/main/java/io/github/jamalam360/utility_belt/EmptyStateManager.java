package io.github.jamalam360.utility_belt;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

import java.util.List;

final class EmptyStateManager extends StateManager {

    static final EmptyStateManager INSTANCE = new EmptyStateManager();

    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public boolean isInBelt(Player player) {
        LOGGER.warn("[UtilityBelt] Unable to tell if its in the belt as only a empty manger was found!");

        return false;
    }

    @Override
    public void setInBelt(Player player, boolean inBelt) {
        LOGGER.warn("[UtilityBelt] Unable to set the given such in belt as only a empty manger was found!");
    }

    @Override
    public int getSelectedBeltSlot(Player player) {
        LOGGER.warn("[UtilityBelt] Unable to get the given selected Belt slot as only a empty manger was found!");

        return 0;
    }

    @Override
    public void setSelectedBeltSlot(Player player, int slot) {
        LOGGER.warn("[UtilityBelt] Unable to set the given selected Belt slot as only a empty manger was found!");
    }

    @Override
    public UtilityBeltInventory getInventory(Player player) {
        LOGGER.warn("[UtilityBelt] Unable to get given Belt Inventory as only a empty manger was found!");

        return new UtilityBeltInventory(List.of());
    }

    @Override
    public void setInventory(Player player, UtilityBeltInventory.Mutable inventory) {
        LOGGER.warn("[UtilityBelt] Unable to set given Belt Inventory as only a empty manger was found!");
    }
}
