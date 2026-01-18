package io.github.jamalam360.utility_belt.client.render;

import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.client.UtilityBeltClient;
import io.github.jamalam360.utility_belt.state.StateManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BeltHotbarRenderer {

    private static final ResourceLocation HOTBAR_SLOT_TOP_SPRITE = UtilityBelt
        .id("utility_belt_hotbar_slot_top");
    private static final ResourceLocation HOTBAR_SLOT_MIDDLE_SPRITE = UtilityBelt
        .id("utility_belt_hotbar_slot_middle");
    private static final ResourceLocation HOTBAR_SLOT_BOTTOM_SPRITE = UtilityBelt
        .id("utility_belt_hotbar_slot_bottom");
    private static final ResourceLocation HOTBAR_SELECTION_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_selection");

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Player player = Minecraft.getInstance().player;
        
        if (Minecraft.getInstance().options.hideGui || player == null) {
            return;
        }
        
        StateManager stateManager = StateManager.getStateManager(player);

        if (stateManager.hasBelt(player) && (stateManager.isInBelt(player)
                                                               || UtilityBeltClient.CLIENT_CONFIG.get().displayUtilityBeltWhenNotSelected)) {
            UtilityBeltInventory inv = stateManager.getInventory(player);
            int scaledHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            int hotbarHeight = inv.getContainerSize() * 20 + 2;
            int x = switch (UtilityBeltClient.CLIENT_CONFIG.get().hotbarPosition) {
                case TOP_LEFT, MIDDLE_LEFT, BOTTOM_LEFT -> 2;
                case TOP_RIGHT, MIDDLE_RIGHT, BOTTOM_RIGHT -> Minecraft.getInstance().getWindow().getGuiScaledWidth() - 24;
            };
            int y = switch (UtilityBeltClient.CLIENT_CONFIG.get().hotbarPosition) {
                case TOP_LEFT, TOP_RIGHT -> 2;
                case MIDDLE_LEFT, MIDDLE_RIGHT -> scaledHeight / 2 - (hotbarHeight / 2);
                case BOTTOM_LEFT, BOTTOM_RIGHT -> scaledHeight - hotbarHeight - 2;
            };
            
            x += UtilityBeltClient.CLIENT_CONFIG.get().hotbarOffsetX;
            y += UtilityBeltClient.CLIENT_CONFIG.get().hotbarOffsetY;

            int m = 1;
            int slotY = y;
            for (int n = 0; n < inv.getContainerSize(); n++) {
                if (n == 0) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SLOT_TOP_SPRITE, x, slotY, 22, 21);
                    slotY += 21;
                } else if (n == inv.getContainerSize() - 1) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SLOT_BOTTOM_SPRITE, x, slotY, 22, 21);
                    slotY += 21;
                } else {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SLOT_MIDDLE_SPRITE, x, slotY, 22, 20);
                    slotY += 20;
                }

                renderHotbarItem(graphics, x, y + 3 + n * 20, deltaTracker.getGameTimeDeltaTicks(), player, inv.getItem(n), m++);
            }

            if (stateManager.isInBelt(player)) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SELECTION_SPRITE, x - 1, y - 1 + stateManager.getSelectedBeltSlot(player) * 20, 24, 23);
            }
        }
    }

    private static void renderHotbarItem(GuiGraphics graphics, int x, int y, float tickDelta, Player player, ItemStack stack, int seed) {
        if (!stack.isEmpty()) {
            float f = (float) stack.getPopTime() - tickDelta;
            if (f > 0.0F) {
                float g = 1.0F + f / 5.0F;
                graphics.pose().pushMatrix();
                graphics.pose().translate(12, y + 12);
                graphics.pose().scale(1.0F / g, (g + 1.0F) / 2.0F);
                graphics.pose().translate(-12, -(y + 12));
            }

            graphics.renderItem(player, stack, x + 3, y, seed);
            if (f > 0.0F) {
                graphics.pose().popMatrix();
            }

            graphics.renderItemDecorations(Minecraft.getInstance().font, stack, x + 3, y);
        }
    }
}
