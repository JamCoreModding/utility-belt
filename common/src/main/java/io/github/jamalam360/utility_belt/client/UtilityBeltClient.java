package io.github.jamalam360.utility_belt.client;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import earth.terrarium.baubly.client.BaublyClient;
import io.github.jamalam360.utility_belt.StateManager;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
import io.github.jamalam360.utility_belt.screen.UtilityBeltScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import static dev.architectury.event.events.client.ClientCommandRegistrationEvent.literal;

@Environment(EnvType.CLIENT)
public class UtilityBeltClient {
	private static final KeyMapping SWAP_TOGGLE = new KeyMapping("key.utility_belt.swap_toggle", GLFW.GLFW_KEY_B, "key.category.utility_belt");
	private static final KeyMapping SWAP_HOLD = new KeyMapping("key.utility_belt.swap_hold", GLFW.GLFW_KEY_N, "key.category.utility_belt");
	private static final KeyMapping OPEN_SCREEN = new KeyMapping("key.utility_belt.open_screen", GLFW.GLFW_KEY_APOSTROPHE, "key.category.utility_belt");
	private static boolean isHoldingSwap = false;

	public static void init() {
		ClientGuiEvent.RENDER_HUD.register(BeltHotbarRenderer::render);
		ClientTickEvent.CLIENT_POST.register(UtilityBeltClient::onEndClientTick);
		ClientRawInputEvent.MOUSE_SCROLLED.register(UtilityBeltClient::onMouseScrolled);
		BaublyClient.registerBaubleRenderer(UtilityBelt.UTILITY_BELT.get(), new BeltRenderer());
		MenuRegistry.registerScreenFactory(UtilityBelt.MENU_TYPE, UtilityBeltScreen::new);
		KeyMappingRegistry.register(SWAP_TOGGLE);
		KeyMappingRegistry.register(SWAP_HOLD);
		KeyMappingRegistry.register(OPEN_SCREEN);
		ClientNetworking.init();

		ClientCommandRegistrationEvent.EVENT.register((dispatcher, c) -> dispatcher.register(
				literal("utilitybelt")
						.then(
								literal("help")
										.executes(ctx -> {
											ctx.getSource().arch$sendSuccess(UtilityBeltClient::getHelpMessage, false);
											return 0;
										})
						)
						.then(
								literal("fixme")
										.executes(ctx -> {
											StateManager stateManager = StateManager.getClientInstance();
											stateManager.setInBelt(Minecraft.getInstance().player, false);
											stateManager.setSelectedBeltSlot(Minecraft.getInstance().player, 0);
											ClientNetworking.sendNewStateToServer(false, 0, false);
											ctx.getSource().arch$sendSuccess(() -> Component.literal("Reset state"), false);
											return 0;
										})
						)
		));
	}

	public static void onJoinServer() {
		//TODO: probably is not needed
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
		StateManager stateManager = StateManager.getClientInstance();

		if (scrollY != 0 && stateManager.isInBelt(client.player)) {
			int slot = stateManager.getSelectedBeltSlot(client.player);
			ItemStack belt = UtilityBeltItem.getBelt(client.player);
			assert belt != null;
			int beltSize = stateManager.getInventory(client.player).getContainerSize();

			if (UtilityBelt.CONFIG.get().invertScrolling) {
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

			stateManager.setSelectedBeltSlot(client.player, slot);
			ClientNetworking.sendNewStateToServer(stateManager.isInBelt(client.player), stateManager.getSelectedBeltSlot(client.player), false);
			return EventResult.interruptTrue();
		}

		return EventResult.pass();
	}

	private static void toggleInBelt(Minecraft client) {
		assert client.player != null;
		StateManager stateManager = StateManager.getClientInstance();
		stateManager.setInBelt(client.player, !stateManager.isInBelt(client.player));
		playSwapSound(client);
		ClientNetworking.sendNewStateToServer(stateManager.isInBelt(client.player), stateManager.getSelectedBeltSlot(client.player), client.player.isCrouching());
	}

	private static void playSwapSound(Minecraft client) {
		assert client.level != null;
		client.getSoundManager().play(SimpleSoundInstance.forUI(
				SoundEvents.ARMOR_EQUIP_LEATHER, client.level.random.nextFloat() + 0.50f));
	}

	private static Component getHelpMessage() {
		return Component.literal("/utilitybelt help").withStyle(ChatFormatting.YELLOW)
				.append(Component.literal(" - Shows this help message").withStyle(ChatFormatting.GRAY))
				.append(Component.literal("\n"))
				.append(Component.literal("/utilitybelt fixme").withStyle(ChatFormatting.YELLOW))
				.append(Component.literal(" - Fixes your state. Useful if you manage to get stuck in the belt. If you use this command please report the circumstances to ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal("https://github.com/JamCoreModding/utility-belt").withStyle(s -> s.withUnderlined(true).withColor(ChatFormatting.BLUE).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/JamCoreModding/utility-belt"))));
	}
}
