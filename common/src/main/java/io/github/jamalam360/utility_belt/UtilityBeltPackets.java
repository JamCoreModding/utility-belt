package io.github.jamalam360.utility_belt;

import net.minecraft.resources.ResourceLocation;

public class UtilityBeltPackets {

    public static final ResourceLocation C2S_UPDATE_STATE = UtilityBelt.id("update_state");
    public static final ResourceLocation C2S_OPEN_SCREEN = UtilityBelt.id("open_screen");
    public static final ResourceLocation S2C_SET_BELT_SLOT = UtilityBelt.id("set_belt_slot");
    public static final ResourceLocation S2C_SET_HOTBAR_SLOT = UtilityBelt.id("set_hotbar_slot");
    public static final ResourceLocation S2C_UPDATE_BELT_INVENTORY = UtilityBelt.id("update_belt_inventory");

    public record C2SUpdateState(boolean inBelt, int slot, boolean swapItems) {
    }

    public record C2SOpenScreen() {
    }

    public record S2CSetBeltSlot(int slot) {
    }

    public record S2CSetHotbarSlot(int slot) {
    }

    public record S2CUpdateBeltInventory(UtilityBeltInventory inventory) {
    }
}
