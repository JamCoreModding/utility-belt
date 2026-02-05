package io.github.jamalam360.utility_belt.neoforge;

import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.client.UtilityBeltClient;
import io.github.jamalam360.utility_belt.client.content.render.BeltHotbarRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@Mod(value = UtilityBelt.MOD_ID, dist = Dist.CLIENT)
public class UtilityBeltNeoForgeClient {
	public UtilityBeltNeoForgeClient(IEventBus modEventBus) {
		UtilityBeltClient.init();
		modEventBus.register(this);
	}

	@SubscribeEvent
	public void onRegisterGuiLayers(RegisterGuiLayersEvent ev) {
		ev.registerBelow(VanillaGuiLayers.CHAT, UtilityBelt.id("hotbar"), BeltHotbarRenderer::render);
	}
}
