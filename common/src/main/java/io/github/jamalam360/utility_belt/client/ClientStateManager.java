package io.github.jamalam360.utility_belt.client;

import io.github.jamalam360.utility_belt.StateManager;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ClientStateManager extends StateManager {
	private boolean isInUtilityBelt = false;
	private int selectedSlot = 0;

	@Override
	public boolean isInBelt(Player player) {
		assert player == Minecraft.getInstance().player;
		return this.isInUtilityBelt;
	}

	@Override
	public void setInBelt(Player player, boolean inBelt) {
		assert player == Minecraft.getInstance().player;
		this.isInUtilityBelt = inBelt;
	}

	@Override
	public int getSelectedBeltSlot(Player player) {
		assert player == Minecraft.getInstance().player;
		return this.selectedSlot;
	}

	@Override
	public void setSelectedBeltSlot(Player player, int slot) {
		assert player == Minecraft.getInstance().player;
		this.selectedSlot = slot;
	}

	@Override
	public UtilityBeltInventory getInventory(Player player) {
		ItemStack belt = UtilityBeltItem.getBelt(player);
		assert belt != null;
		return UtilityBeltItem.getInventoryFromTag(belt);
	}
}
