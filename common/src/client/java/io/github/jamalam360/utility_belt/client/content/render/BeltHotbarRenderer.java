package io.github.jamalam360.utility_belt.client.content.render;

import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.util.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.client.UtilityBeltClient;
import io.github.jamalam360.utility_belt.state.StateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BeltHotbarRenderer {
	private static final ResourceLocation WIDGETS = UtilityBelt.id("textures/gui/widgets.png");
    private static final ResourceLocation VANILLA_WIDGETS = new ResourceLocation("textures/gui/widgets.png");

    public static void render(GuiGraphics graphics, float partialTick) {
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
                    graphics.blit(WIDGETS, x, slotY, 0, 130, 22, 21);
                    slotY += 21;
                } else if (n == inv.getContainerSize() - 1) {
                    graphics.blit(WIDGETS, x, slotY, 0, 171, 22, 21);
                    slotY += 21;
                } else {
                    graphics.blit(WIDGETS, x, slotY, 0, 151, 22, 20);
                    slotY += 20;
                }

                renderHotbarItem(graphics, x + 3, y + 3 + n * 20, partialTick, player, inv.getItem(n), m++);
            }

            if (stateManager.isInBelt(player)) {
                graphics.blit(VANILLA_WIDGETS, x - 1, y - 1 + stateManager.getSelectedBeltSlot(player) * 20, 0, 22, 24, 22);
            }
        }
    }

    private static void renderHotbarItem(GuiGraphics graphics, int x, int y, float partialTick, Player player, ItemStack stack, int seed) {
        if (!stack.isEmpty()) {
			float f = stack.getPopTime() - partialTick;
			if (f > 0.0F) {
				float g = 1.0F + f / 5.0F;
				graphics.pose().pushPose();
				graphics.pose().translate((float)(x + 8), (float)(y + 12), 0.0F);
				graphics.pose().scale(1.0F / g, (g + 1.0F) / 2.0F, 1.0F);
				graphics.pose().translate((float)(-(x + 8)), (float)(-(y + 12)), 0.0F);
			}

			graphics.renderItem(player, stack, x, y, seed);
			if (f > 0.0F) {
				graphics.pose().popPose();
			}

			graphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y);
        }
    }
}
