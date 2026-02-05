package io.github.jamalam360.utility_belt.forge;

import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.client.content.render.BeltHotbarRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = UtilityBelt.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class UtilityBeltForgeClient {
	@SubscribeEvent
	public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent ev) {
		ev.registerBelow(VanillaGuiOverlay.CHAT_PANEL.id(), "utility_belt_hotbar", (gui, graphics, partialTick, width, height) -> BeltHotbarRenderer.render(graphics, partialTick));
	}
}
