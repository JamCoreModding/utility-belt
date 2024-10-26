package io.github.jamalam360.utility_belt;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.jamalam360.jamlib.JamLib;
import io.github.jamalam360.jamlib.JamLibPlatform;
import io.github.jamalam360.utility_belt.network.ServerNetworking;
import io.github.jamalam360.utility_belt.screen.UtilityBeltMenu;
import io.github.jamalam360.utility_belt.state.ServerStateManager;
import io.github.jamalam360.utility_belt.state.StateManager;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilityBelt {

    public static final String MOD_ID = "utility_belt";
    public static final String MOD_NAME = "Utility Belt";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final TagKey<Item> ALLOWED_IN_UTILITY_BELT = TagKey.create(Registries.ITEM, id("allowed_in_utility_belt"));

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(MOD_ID, Registries.MENU);
    private static final DeferredRegister<DataComponentType<?>> COMPONENT_TYPES = DeferredRegister.create(MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<Item> UTILITY_BELT_ITEM = ITEMS.register("utility_belt", UtilityBeltItem::new);
    public static final RegistrySupplier<DataComponentType<UtilityBeltInventory>> UTILITY_BELT_INVENTORY_COMPONENT_TYPE = COMPONENT_TYPES.register("utility_belt_inventory", () ->
          DataComponentType.<UtilityBeltInventory>builder().persistent(UtilityBeltInventory.CODEC).cacheEncoding().build()
    );
    public static final RegistrySupplier<MenuType<UtilityBeltMenu>> MENU_TYPE = MENUS.register("utility_belt", () -> new MenuType<>(UtilityBeltMenu::new, FeatureFlagSet.of()));
    
    public static void init() {
        JamLib.checkForJarRenaming(UtilityBelt.class);
        COMPONENT_TYPES.register();
        ITEMS.register();
        MENUS.register();
        UTILITY_BELT_ITEM.listen((belt) -> CreativeTabRegistry.append(CreativeModeTabs.TOOLS_AND_UTILITIES, belt));
        ServerNetworking.init();
        StateManager.setServerInstance(new ServerStateManager());
        LOGGER.info(MOD_NAME + " initialized on " + JamLibPlatform.getPlatform());
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
    
    @FunctionalInterface
    public interface UtilityBeltUnequipEvent {
        void onUnequip(ItemStack stack, SlotReference reference);
    }
}
