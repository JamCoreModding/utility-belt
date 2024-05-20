package io.github.jamalam360.utility_belt;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import io.github.jamalam360.jamlib.JamLib;
import io.github.jamalam360.jamlib.JamLibPlatform;
import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.utility_belt.client.UtilityBeltClient;
import io.github.jamalam360.utility_belt.screen.UtilityBeltMenu;
import io.github.jamalam360.utility_belt.server.ServerNetworking;
import io.github.jamalam360.utility_belt.server.ServerStateManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilityBelt {
	public static final String MOD_ID = "utility_belt";
	public static final String MOD_NAME = "Utility Belt";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
	public static final ConfigManager<Config> CONFIG = new ConfigManager<>(MOD_ID, Config.class);
	public static final TagKey<Item> ALLOWED_IN_UTILITY_BELT = TagKey.create(Registries.ITEM, id("allowed_in_utility_belt"));
	public static final ResourceLocation C2S_UPDATE_STATE = id("update_state");
	public static final ResourceLocation C2S_OPEN_SCREEN = id("open_screen");
	public static final ResourceLocation S2C_UPDATE_INV = id("update_inv");
	public static final ResourceLocation S2C_SET_BELT_SLOT = id("set_belt_slot");
	public static final ResourceLocation S2C_SET_HOTBAR_SLOT = id("set_hotbar_slot");
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);
	public static final RegistrySupplier<Item> UTILITY_BELT_ITEM = ITEMS.register("utility_belt", UtilityBeltItem::new);
	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(MOD_ID, Registries.MENU);
	public static final RegistrySupplier<MenuType<UtilityBeltMenu>> MENU_TYPE = MENUS.register("utility_belt", () -> new MenuType<>(UtilityBeltMenu::new, FeatureFlagSet.of()));

	public static void init() {
		JamLib.checkForJarRenaming(UtilityBelt.class);
		ITEMS.register();
		MENUS.register();
		UTILITY_BELT_ITEM.listen((belt) -> CreativeTabRegistry.append(CreativeModeTabs.TOOLS_AND_UTILITIES, belt));
		ServerNetworking.init();
		StateManager.setServerInstance(new ServerStateManager());
		EnvExecutor.runInEnv(Env.CLIENT, () -> UtilityBeltClient::init);

		if (Platform.isDevelopmentEnvironment()) {
			CommandRegistrationEvent.EVENT.register(((dispatcher, registry, selection) ->
					dispatcher.register(
							Commands.literal("dumpstate")
									.executes(ctx -> {
										CommandSourceStack source = ctx.getSource();
										StateManager stateManager = StateManager.getServerInstance();
										System.out.println("In belt: " + stateManager.isInBelt(source.getPlayerOrException()));
										System.out.println("Selected slot: " + stateManager.getSelectedBeltSlot(source.getPlayerOrException()));
										System.out.println("Belt NBT: " + UtilityBeltItem.getBelt(source.getPlayerOrException()).getTag());
										return 0;
									})
					)
			));
		}

		LOGGER.info(MOD_NAME + " initialized on " + JamLibPlatform.getPlatform());
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
