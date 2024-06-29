package io.github.jamalam360.utility_belt.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.jamalam360.utility_belt.StateManager;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.UtilityBeltInventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class BeltHotbarRenderer {

    private static final ResourceLocation UTILITY_BELT_WIDGET_TEXTURE = UtilityBelt
          .id("textures/gui/utility_belt_widget.png");
    private static final ResourceLocation HOTBAR_SELECTION_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_selection");

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Player player = Minecraft.getInstance().player;
        StateManager stateManager = StateManager.getClientInstance();

        if (player != null && stateManager.hasBelt(player) && (stateManager.isInBelt(player)
                                                               || UtilityBelt.CONFIG.get().displayUtilityBeltWhenNotSelected)) {
            int scaledHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            int x = switch (UtilityBelt.CONFIG.get().hotbarPosition) {
                case TOP_LEFT, MIDDLE_LEFT, BOTTOM_LEFT -> 2;
                case TOP_RIGHT, MIDDLE_RIGHT, BOTTOM_RIGHT -> Minecraft.getInstance().getWindow().getGuiScaledWidth() - 24;
            };
            int y = switch (UtilityBelt.CONFIG.get().hotbarPosition) {
                case TOP_LEFT, TOP_RIGHT -> 2;
                case MIDDLE_LEFT, MIDDLE_RIGHT -> scaledHeight / 2 - 44;
                case BOTTOM_LEFT, BOTTOM_RIGHT -> scaledHeight - 84;
            };

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);

            graphics.blit(UTILITY_BELT_WIDGET_TEXTURE, x, y, 0, 0, 22, 88);

            if (stateManager.isInBelt(player)) {
                graphics.blitSprite(HOTBAR_SELECTION_SPRITE, x - 1, y - 1 + stateManager.getSelectedBeltSlot(player) * 20, 0, 24, 23);
            }

            UtilityBeltInventory inv = stateManager.getInventory(player);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            int m = 1;

            for (int n = 0; n < inv.getContainerSize(); ++n) {
                renderHotbarItem(graphics, x, y + n * 20 + 3, deltaTracker.getGameTimeDeltaTicks(), player, inv.getItem(n), m++);
            }
        }
    }

    private static void renderHotbarItem(GuiGraphics graphics, int x, int y, float tickDelta, Player player, ItemStack stack, int seed) {
        if (!stack.isEmpty()) {
            float f = (float) stack.getPopTime() - tickDelta;
            if (f > 0.0F) {
                float g = 1.0F + f / 5.0F;
                graphics.pose().pushPose();
                graphics.pose().translate(12, y + 12, 0);
                graphics.pose().scale(1.0F / g, (g + 1.0F) / 2.0F, 1);
                graphics.pose().translate(-12, -(y + 12), 0);
            }

            graphics.renderItem(player, stack, x + 3, y, seed);
            if (f > 0.0F) {
                graphics.pose().popPose();
            }

            graphics.renderItemDecorations(Minecraft.getInstance().font, stack, x + 3, y);
        }
    }
}
