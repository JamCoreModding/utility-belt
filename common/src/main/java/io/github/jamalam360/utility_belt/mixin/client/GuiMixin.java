package io.github.jamalam360.utility_belt.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.jamalam360.utility_belt.StateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public abstract class GuiMixin {
	@Shadow
	protected abstract Player getCameraPlayer();

	@WrapWithCondition(
			method = "renderItemHotbar",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V",
					ordinal = 1
			)
	)
	private boolean utilitybelt$disableHotbarHighlight(GuiGraphics instance, ResourceLocation arg, int i, int j, int k, int l) {
		return !StateManager.getClientInstance().isInBelt(this.getCameraPlayer());
	}
}
