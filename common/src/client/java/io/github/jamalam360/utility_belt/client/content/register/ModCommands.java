package io.github.jamalam360.utility_belt.client.content.register;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import dev.architectury.platform.Platform;
import io.github.jamalam360.utility_belt.content.UtilityBeltItem;
import io.github.jamalam360.utility_belt.client.network.ClientNetworking;
import io.github.jamalam360.utility_belt.content.register.ModComponents;
import io.github.jamalam360.utility_belt.state.StateManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.net.URI;

import static dev.architectury.event.events.client.ClientCommandRegistrationEvent.literal;

public class ModCommands {
	public static void init() {
		ClientCommandRegistrationEvent.EVENT.register(ModCommands::registerCommands);

		if (Platform.isDevelopmentEnvironment()) {
			ClientCommandRegistrationEvent.EVENT.register(ModCommands::registerDevelopmentCommands);
		}
	}

	private static void registerCommands(CommandDispatcher<ClientCommandRegistrationEvent.ClientCommandSourceStack> dispatcher, CommandBuildContext _ctx) {
		dispatcher.register(
				literal("utilitybelt")
						.then(
								literal("help")
										.executes(ctx -> {
											ctx.getSource().arch$sendSuccess(ModCommands::getHelpMessage, false);
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
		);
	}
	
	private static void registerDevelopmentCommands(CommandDispatcher<ClientCommandRegistrationEvent.ClientCommandSourceStack> dispatcher, CommandBuildContext _ctx) {
		dispatcher.register(
				literal("dumpstatec")
						.executes(ctx -> {
							var player = Minecraft.getInstance().player;

							StateManager stateManager = StateManager.getStateManager(player);

							System.out.println("In belt: " + stateManager.isInBelt(player));
							System.out.println("Selected slot: " + stateManager.getSelectedBeltSlot(player));
							System.out.println("Belt NBT: " + UtilityBeltItem.getBelt(player).get(ModComponents.UTILITY_BELT_INVENTORY.get()));

							StateManager stateManagerS = StateManager.getStateManager(false);
							System.out.println("In belt (client but server): " + stateManagerS.isInBelt(player));
							System.out.println("Selected slot (client but server): " + stateManagerS.getSelectedBeltSlot(player));
							System.out.println("Belt NBT (client but server): " + UtilityBeltItem.getBelt(player).get(ModComponents.UTILITY_BELT_INVENTORY.get()));
							return 0;
						})
		);
	}

	private static Component getHelpMessage() {
		return Component.literal("/utilitybelt help").withStyle(ChatFormatting.YELLOW)
				.append(Component.literal(" - Shows this help message").withStyle(ChatFormatting.GRAY))
				.append(Component.literal("\n"))
				.append(Component.literal("/utilitybelt fixme").withStyle(ChatFormatting.YELLOW))
				.append(Component.literal(" - Fixes your state. Useful if you manage to get stuck in the belt. If you use this command please report the circumstances to ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal("https://github.com/JamCoreModding/utility-belt").withStyle(s -> s.withUnderlined(true).withColor(ChatFormatting.BLUE).withClickEvent(new ClickEvent.OpenUrl(URI.create("https://github.com/JamCoreModding/utility-belt")))));
	}
}
