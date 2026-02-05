package io.github.jamalam360.utility_belt.client.content.register;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.client.UtilityBeltClient;
import io.github.jamalam360.utility_belt.client.network.ClientNetworking;
import io.github.jamalam360.utility_belt.content.UtilityBeltItem;
import io.github.jamalam360.utility_belt.state.StateManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class ModInputs {
	private static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(UtilityBelt.id("utility_belt"));
	private static final KeyMapping SWAP_TOGGLE = new KeyMapping("key.utility_belt.swap_toggle", GLFW.GLFW_KEY_B, KEY_CATEGORY);
	private static final KeyMapping SWAP_HOLD = new KeyMapping("key.utility_belt.swap_hold", GLFW.GLFW_KEY_N, KEY_CATEGORY);
	private static final KeyMapping OPEN_SCREEN = new KeyMapping("key.utility_belt.open_screen", GLFW.GLFW_KEY_APOSTROPHE, KEY_CATEGORY);
	private static boolean isHoldingSwap = false;

	public static void init() {
		KeyMappingRegistry.register(SWAP_TOGGLE);
		KeyMappingRegistry.register(SWAP_HOLD);
		KeyMappingRegistry.register(OPEN_SCREEN);
		ClientTickEvent.CLIENT_POST.register(ModInputs::onEndClientTick);
		ClientRawInputEvent.MOUSE_SCROLLED.register(ModInputs::onMouseScrolled);
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

			if (UtilityBeltClient.CLIENT_CONFIG.get().invertScrolling) {
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

	private static void toggleInBelt(Minecraft client) {
		if (client.player == null || UtilityBeltItem.getBelt(client.player) == null) {
			return;
		}

		StateManager stateManager = StateManager.getStateManager(true);
		stateManager.setInBelt(client.player, !stateManager.isInBelt(client.player));
		client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ARMOR_EQUIP_LEATHER, client.level.random.nextFloat() + 0.50f));
		ClientNetworking.sendNewStateToServer(stateManager.isInBelt(client.player), stateManager.getSelectedBeltSlot(client.player), client.player.isCrouching());
	}
}
