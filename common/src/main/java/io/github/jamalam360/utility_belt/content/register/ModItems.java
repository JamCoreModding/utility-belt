package io.github.jamalam360.utility_belt.content.register;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.content.UtilityBeltItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class ModItems {
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(UtilityBelt.MOD_ID, Registries.ITEM);
	public static final RegistrySupplier<Item> UTILITY_BELT_ITEM = ITEMS.register("utility_belt", UtilityBeltItem::new);
	public static final RegistrySupplier<Item> POUCH_ITEM = ITEMS.register("pouch", () -> new Item(new Item.Properties()));
	public static final TagKey<Item> ALLOWED_IN_UTILITY_BELT = TagKey.create(Registries.ITEM, UtilityBelt.id("allowed_in_utility_belt"));

	public static void init() {
        ITEMS.register();
		UTILITY_BELT_ITEM.listen((belt) -> CreativeTabRegistry.append(CreativeModeTabs.TOOLS_AND_UTILITIES, belt));
        POUCH_ITEM.listen((pouch) -> CreativeTabRegistry.append(CreativeModeTabs.TOOLS_AND_UTILITIES, pouch));
	}
}
