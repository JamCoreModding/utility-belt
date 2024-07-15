package io.github.jamalam360.utility_belt.client;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.*;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import io.github.jamalam360.utility_belt.StateManager;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.UtilityBeltItem;
import io.github.jamalam360.utility_belt.screen.UtilityBeltScreen;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
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
		UtilityBelt.UTILITY_BELT_ITEM.listen(belt -> AccessoriesRendererRegistry.registerRenderer(belt, BeltRenderer::new));
		ClientTickEvent.CLIENT_POST.register(UtilityBeltClient::onEndClientTick);
		ClientRawInputEvent.MOUSE_SCROLLED.register(UtilityBeltClient::onMouseScrolled);
		ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register(UtilityBeltClient::onPlayerRespawn);
		UtilityBelt.MENU_TYPE.listen(menu -> MenuRegistry.registerScreenFactory(UtilityBelt.MENU_TYPE.get(), UtilityBeltScreen::new));
		KeyMappingRegistry.register(SWAP_TOGGLE);
		KeyMappingRegistry.register(SWAP_HOLD);
		KeyMappingRegistry.register(OPEN_SCREEN);
		ClientNetworking.init();
		StateManager.setClientInstance(new ClientStateManager());

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
											Player player = Minecraft.getInstance().player;

											StateManager stateManager = StateManager.getStateManager(player);
											stateManager.setInBelt(player, false);
											stateManager.setSelectedBeltSlot(player, 0);
											ClientNetworking.sendNewStateToServer(false, 0, false);
											ctx.getSource().arch$sendSuccess(() -> Component.literal("Reset state"), false);
											return 0;
										})
						)
		));

		if (Platform.isDevelopmentEnvironment()) {
			ClientCommandRegistrationEvent.EVENT.register(((dispatcher, c) ->
					dispatcher.register(
							literal("dumpstatec")
									.executes(ctx -> {
										var player = Minecraft.getInstance().player;

										StateManager stateManager = StateManager.getStateManager(player);

										System.out.println("In belt: " + stateManager.isInBelt(player));
										System.out.println("Selected slot: " + stateManager.getSelectedBeltSlot(player));
										System.out.println("Belt NBT: " + UtilityBeltItem.getBelt(player).get(UtilityBelt.UTILITY_BELT_INVENTORY_COMPONENT_TYPE.get()));

										StateManager stateManagerS = StateManager.getStateManager(false);
										System.out.println("In belt (client but server): " + stateManagerS.isInBelt(player));
										System.out.println("Selected slot (client but server): " + stateManagerS.getSelectedBeltSlot(player));
										System.out.println("Belt NBT (client but server): " + UtilityBeltItem.getBelt(player).get(UtilityBelt.UTILITY_BELT_INVENTORY_COMPONENT_TYPE.get()));
										return 0;
									})
					)
			));
		}
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

			stateManager.setSelectedBeltSlot(player, slot);
			ClientNetworking.sendNewStateToServer(stateManager.isInBelt(player), stateManager.getSelectedBeltSlot(player), false);
			return EventResult.interruptTrue();
		}

		return EventResult.pass();
	}

	private static void onPlayerRespawn(LocalPlayer oldPlayer, LocalPlayer newPlayer) {
		if (oldPlayer == Minecraft.getInstance().player || newPlayer == Minecraft.getInstance().player) {
			StateManager stateManager = StateManager.getStateManager(true);
			stateManager.setInBelt(Minecraft.getInstance().player, false);
			stateManager.setSelectedBeltSlot(Minecraft.getInstance().player, 0);
			ClientNetworking.sendNewStateToServer(false, 0, false);
		}
	}

	/**
	 * @see io.github.jamalam360.utility_belt.mixin.client.ClientPacketListenerMixin
	 */
	public static void onClientConnect() {
		StateManager stateManager = StateManager.getStateManager(true);
		stateManager.setInBelt(Minecraft.getInstance().player, false);
		stateManager.setSelectedBeltSlot(Minecraft.getInstance().player, 0);
		ClientNetworking.sendNewStateToServer(false, 0, false);
	}

	/**
	 * @see io.github.jamalam360.utility_belt.mixin.client.ClientPacketListenerMixin
	 */
	public static void onClientDisconnect() {
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
