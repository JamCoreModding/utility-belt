package io.github.jamalam360.utility_belt.client.screen;

import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.screen.UtilityBeltMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class UtilityBeltScreen extends AbstractContainerScreen<UtilityBeltMenu> {
	private static final ResourceLocation BACKGROUND_TOP_SPRITE = UtilityBelt.id("utility_belt_gui_top");
	private static final ResourceLocation BACKGROUND_SLOT_ROW_SPRITE = UtilityBelt.id("utility_belt_gui_slot_row");
	private static final ResourceLocation BACKGROUND_BOTTOM_SPRITE = UtilityBelt.id("utility_belt_gui_bottom");
	private static final ResourceLocation SLOT_SPRITE = UtilityBelt.id("slot");

	public UtilityBeltScreen(UtilityBeltMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, Component.translatable("container.utility_belt.utility_belt"));
	}

	@Override
	protected void init() {
		super.init();
		this.inventoryLabelY = this.imageHeight - 130 + (this.menu.getBeltRows() - 1) * 18;
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
		int rows = this.menu.getBeltRows();
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_TOP_SPRITE, this.leftPos, this.topPos, 176, 16);
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_BOTTOM_SPRITE, this.leftPos, this.topPos + 16 + rows * 18, 176, 96);

		for (int i = 0; i < rows; i++) {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SLOT_ROW_SPRITE, this.leftPos, this.topPos + 16 + i * 18, 176, 18);
		}

        int x = 0;
        int y = 0;
        while ((x + y * 9) < this.menu.getBeltInventorySize()) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_SPRITE, this.leftPos + 7 + x * 18, this.topPos + 16 + y * 18, 18, 18);

            x += 1;
            if (x == 9) {
                x = 0;
                y += 1;
            }
        }
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		this.renderBg(graphics, delta, mouseX, mouseY);
		super.render(graphics, mouseX, mouseY, delta);
		this.renderTooltip(graphics, mouseX, mouseY);
	}
}
