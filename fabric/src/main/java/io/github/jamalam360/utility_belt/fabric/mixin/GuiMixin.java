package io.github.jamalam360.utility_belt.fabric.mixin;

import io.github.jamalam360.utility_belt.client.render.BeltHotbarRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
	@Inject(
			method = "renderChat",
			at = @At("HEAD")
	)
	private void utilitybelt$renderUtilityBeltHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		// Ensure we render BEFORE chat, so chat goes on top of the hotbar.
		// This is why we cannot use the Architectury event.
		BeltHotbarRenderer.render(guiGraphics, deltaTracker);
	}
}
