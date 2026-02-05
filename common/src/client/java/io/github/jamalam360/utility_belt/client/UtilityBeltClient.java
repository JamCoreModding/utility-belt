package io.github.jamalam360.utility_belt.client;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
import io.github.jamalam360.utility_belt.client.network.ClientNetworking;
import io.github.jamalam360.utility_belt.client.render.BeltRenderer;
import io.github.jamalam360.utility_belt.client.screen.UtilityBeltScreen;
import io.github.jamalam360.utility_belt.client.state.ClientStateManager;
import io.github.jamalam360.utility_belt.state.StateManager;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class UtilityBeltClient {
	public static final ConfigManager<ClientConfig> CLIENT_CONFIG = new ConfigManager<>(UtilityBelt.MOD_ID, "client", ClientConfig.class);
	private static final String KEY_CATEGORY = "key.category.utility_belt.utility_belt";
	private static final KeyMapping SWAP_TOGGLE = new KeyMapping("key.utility_belt.swap_toggle", GLFW.GLFW_KEY_B, KEY_CATEGORY);
	private static final KeyMapping SWAP_HOLD = new KeyMapping("key.utility_belt.swap_hold", GLFW.GLFW_KEY_N, KEY_CATEGORY);
	private static final KeyMapping OPEN_SCREEN = new KeyMapping("key.utility_belt.open_screen", GLFW.GLFW_KEY_APOSTROPHE, KEY_CATEGORY);
	private static boolean isHoldingSwap = false;

	public static void init() {
		KeyMappingRegistry.register(SWAP_TOGGLE);
		KeyMappingRegistry.register(SWAP_HOLD);
		KeyMappingRegistry.register(OPEN_SCREEN);

		ClientTickEvent.CLIENT_POST.register(UtilityBeltClient::onEndClientTick);
		ClientRawInputEvent.MOUSE_SCROLLED.register(UtilityBeltClient::onMouseScrolled);
		ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register(UtilityBeltClient::onPlayerRespawn);
		ClientCommandRegistrationEvent.EVENT.register(UtilityBeltCommands::registerCommands);

		if (Platform.isDevelopmentEnvironment()) {
			ClientCommandRegistrationEvent.EVENT.register(UtilityBeltCommands::registerDevelopmentCommands);
		}

		UtilityBelt.MENU_TYPE.listen(menu -> MenuRegistry.registerScreenFactory(menu, UtilityBeltScreen::new));
		UtilityBelt.UTILITY_BELT_ITEM.listen(belt -> AccessoriesRendererRegistry.registerRenderer(belt, BeltRenderer::new));
		ClientNetworking.init();
		StateManager.setClientInstance(new ClientStateManager());
	}

	private static void onEndClientTick(Minecraft client) {
		if (SWAP_TOGGLE.consumeClick()) {
			toggleInBelt(client);
		}

		boolean clicked = SWAP_HOLD.isDown();
		if (!isHoldingSwap && clicked) {
			isHoldingSwap = true;
			toggleInBelt(client);
		} else if (isHoldingSwap && !clicked) {
			isHoldingSwap = false;
			toggleInBelt(client);
		}

		if (OPEN_SCREEN.consumeClick() && UtilityBeltItem.getBelt(client.player) != null) {
			ClientNetworking.sendOpenScreenToServer();
		}
	}

	private static EventResult onMouseScrolled(Minecraft client, double scrollX, double scrollY) {
		Player player = client.player;
		StateManager stateManager = StateManager.getStateManager(true);

		if (scrollY != 0 && stateManager.isInBelt(player)) {
			int slot = stateManager.getSelectedBeltSlot(player);
			ItemStack belt = UtilityBeltItem.getBelt(player);

			if (belt == null) {
				return EventResult.pass();
			}

			int beltSize = stateManager.getInventory(player).getContainerSize();

			if (CLIENT_CONFIG.get().invertScrolling) {
				scrollY = -scrollY;
			}

			if (scrollY == 1) {
				slot--;
				if (slot < 0) {
					slot = beltSize - 1;
				}
			} else if (scrollY == -1) {
				slot++;
				if (slot >= beltSize) {
					slot = 0;
				}
			}

			stateManager.setSelectedBeltSlot(player, slot);
			ClientNetworking.sendNewStateToServer(stateManager.isInBelt(player), stateManager.getSelectedBeltSlot(player), false);
			return EventResult.interruptTrue();
		}

		return EventResult.pass();
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

	private static void toggleInBelt(Minecraft client) {
		if (client.player == null || UtilityBeltItem.getBelt(client.player) == null) {
			return;
		}

		StateManager stateManager = StateManager.getStateManager(true);
		stateManager.setInBelt(client.player, !stateManager.isInBelt(client.player));
		playSwapSound(client);
		ClientNetworking.sendNewStateToServer(stateManager.isInBelt(client.player), stateManager.getSelectedBeltSlot(client.player), client.player.isCrouching());
	}

	private static void playSwapSound(Minecraft client) {
		client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ARMOR_EQUIP_LEATHER, client.level.random.nextFloat() + 0.50f));
	}
}
