package io.github.jamalam360.utility_belt.client.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.jamalam360.utility_belt.state.StateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public abstract class GuiMixin {
	@Shadow
	protected abstract Player getCameraPlayer();

	@WrapWithCondition(
			method = "renderItemHotbar",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIII)V",
					ordinal = 1
			)
	)
	private boolean utilitybelt$disableHotbarHighlight(GuiGraphics instance, Function<ResourceLocation, RenderType> renderTypeGetter, ResourceLocation sprite, int x, int y, int width, int height) {
		return !StateManager.getStateManager(true).isInBelt(this.getCameraPlayer());
	}
}
