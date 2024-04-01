package io.github.jamalam360.utility_belt;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import io.github.jamalam360.jamlib.JamLib;
import io.github.jamalam360.jamlib.JamLibPlatform;
import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.utility_belt.client.UtilityBeltClient;
import io.github.jamalam360.utility_belt.screen.UtilityBeltMenu;
import io.github.jamalam360.utility_belt.screen.UtilityBeltScreen;
import io.github.jamalam360.utility_belt.server.ServerNetworking;
import io.github.jamalam360.utility_belt.server.ServerStateManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilityBelt {
	public static final String MOD_ID = "utility_belt";
	public static final String MOD_NAME = "Utility Belt";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
	public static final ConfigManager<Config> CONFIG = new ConfigManager<>(MOD_ID, Config.class);
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);
	public static final RegistrySupplier<Item> UTILITY_BELT = ITEMS.register("utility_belt", UtilityBeltItem::new);
	public static final MenuType<UtilityBeltMenu> MENU_TYPE = new MenuType<>(UtilityBeltMenu::new, FeatureFlagSet.of());
	public static final TagKey<Item> ALLOWED_IN_UTILITY_BELT = TagKey.create(Registries.ITEM, id("allowed_in_utility_belt"));
	public static final ResourceLocation C2S_UPDATE_STATE = id("update_state");
	public static final ResourceLocation C2S_OPEN_SCREEN = id("open_screen");
	public static final ResourceLocation S2C_UPDATE_BELT_INVENTORY = id("update_belt_inventory");

	public static void init() {
		JamLib.checkForJarRenaming(UtilityBelt.class);
		ITEMS.register();
		//noinspection UnstableApiUsage
		CreativeTabRegistry.append(CreativeModeTabs.TOOLS_AND_UTILITIES, UTILITY_BELT.get());
		ServerNetworking.init();
		StateManager.setServerInstance(new ServerStateManager());

		if (Platform.isDevelopmentEnvironment()) {
			CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> {
				dispatcher.register(
						Commands.literal("utilitybeltdev")
								.then(Commands.literal("debug")
										.executes(context -> {
											CommandSourceStack source = context.getSource();
											System.out.println(source.getPlayer().level().isClientSide);
											System.out.println(source.getPlayer().getMainHandItem());
											System.out.println(StateManager.getServerInstance().getInventory(source.getPlayer()).getItem(0).getTag());
											source.sendSuccess(() -> Component.literal(UtilityBeltItem.getBelt(source.getPlayer()).getTag().toString()), false);
											return 0;
										})
								));
			});
		}

		EnvExecutor.runInEnv(Env.CLIENT, () -> UtilityBeltClient::init);
		LOGGER.info(MOD_NAME + " initialized on " + JamLibPlatform.getPlatform());
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
