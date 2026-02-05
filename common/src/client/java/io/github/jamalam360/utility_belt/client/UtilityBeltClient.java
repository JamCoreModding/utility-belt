package io.github.jamalam360.utility_belt.client;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.registry.client.gui.MenuScreenRegistry;
import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.client.content.ClientConfig;
import io.github.jamalam360.utility_belt.client.content.UtilityBeltScreen;
import io.github.jamalam360.utility_belt.client.content.register.ModCommands;
import io.github.jamalam360.utility_belt.client.content.register.ModInputs;
import io.github.jamalam360.utility_belt.client.content.render.BeltRenderer;
import io.github.jamalam360.utility_belt.client.network.ClientNetworking;
import io.github.jamalam360.utility_belt.client.state.ClientStateManager;
import io.github.jamalam360.utility_belt.content.register.ModItems;
import io.github.jamalam360.utility_belt.content.register.ModMenus;
import io.github.jamalam360.utility_belt.state.StateManager;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class UtilityBeltClient {
	public static final ConfigManager<ClientConfig> CLIENT_CONFIG = new ConfigManager<>(UtilityBelt.MOD_ID, "client", ClientConfig.class);

	public static void init() {
		ModCommands.init();
		ModInputs.init();
		ClientNetworking.init();
		StateManager.setClientInstance(new ClientStateManager());
		ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register(UtilityBeltClient::onPlayerRespawn);
		ModMenus.MENU_TYPE.listen(menu -> MenuScreenRegistry.registerScreenFactory(menu, UtilityBeltScreen::new));
		ModItems.UTILITY_BELT_ITEM.listen(belt -> AccessoriesRendererRegistry.bindItemToRenderer(belt, UtilityBelt.id("utility_belt"), BeltRenderer::new));
	}

	private static void onPlayerRespawn(LocalPlayer oldPlayer, LocalPlayer newPlayer) {
		if (oldPlayer == Minecraft.getInstance().player || newPlayer == Minecraft.getInstance().player) {
			resetClientState();
		}
	}

	public static void resetClientState() {
		StateManager stateManager = StateManager.getStateManager(true);
		stateManager.setInBelt(Minecraft.getInstance().player, false);
		stateManager.setSelectedBeltSlot(Minecraft.getInstance().player, 0);
	}
}
