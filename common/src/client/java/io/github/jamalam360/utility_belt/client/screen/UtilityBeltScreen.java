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
	private static final ResourceLocation TEXTURE = UtilityBelt.id("textures/gui/utility_belt_gui.png");

	public UtilityBeltScreen(UtilityBeltMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, Component.translatable("container.utility_belt.utility_belt"));
	}

	@Override
	protected void init() {
		super.init();
		this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
		this.inventoryLabelY = this.imageHeight - 130;
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0F, 0F, this.imageWidth, this.imageHeight, 256, 256);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		this.renderBg(graphics, delta, mouseX, mouseY);
		super.render(graphics, mouseX, mouseY, delta);
		this.renderTooltip(graphics, mouseX, mouseY);
	}
}
