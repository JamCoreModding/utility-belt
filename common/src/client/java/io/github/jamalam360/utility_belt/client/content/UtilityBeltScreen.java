package io.github.jamalam360.utility_belt.client.content;

import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.content.UtilityBeltMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class UtilityBeltScreen extends AbstractContainerScreen<UtilityBeltMenu> {
	private static final ResourceLocation WIDGETS = UtilityBelt.id("textures/gui/widgets.png");

	public UtilityBeltScreen(UtilityBeltMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, Component.translatable("container.utility_belt.utility_belt"));
	}

	@Override
	protected void init() {
		this.imageHeight = 16 + this.menu.getBeltRows() * 18 + 96;
		super.init();
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
		int rows = this.menu.getBeltRows();
		graphics.blit(WIDGETS, this.leftPos, this.topPos, 0, 0, 176, 16);
		graphics.blit(WIDGETS, this.leftPos, this.topPos + 16 + rows * 18, 0, 34, 176, 96);

		for (int i = 0; i < rows; i++) {
			graphics.blit(WIDGETS, this.leftPos, this.topPos + 16 + i * 18, 0, 16, 176, 18);
		}

        int x = 0;
        int y = 0;
        while ((x + y * 9) < this.menu.getBeltInventorySize()) {
            graphics.blit(WIDGETS, this.leftPos + 7 + x * 18, this.topPos + 16 + y * 18, 176, 0, 18, 18);

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
