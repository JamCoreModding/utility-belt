package io.github.jamalam360.utility_belt.content.register;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.content.UtilityBeltMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;

public class ModMenus {
	private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(UtilityBelt.MOD_ID, Registries.MENU);
	public static final RegistrySupplier<MenuType<UtilityBeltMenu>> MENU_TYPE = MENUS.register("utility_belt", () -> new MenuType<>(UtilityBeltMenu::new, FeatureFlagSet.of()));

	public static void init() {
		MENUS.register();
	}
}
