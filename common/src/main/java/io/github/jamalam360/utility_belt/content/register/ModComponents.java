package io.github.jamalam360.utility_belt.content.register;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.util.UtilityBeltInventory;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;

public class ModComponents {
	private static final DeferredRegister<DataComponentType<?>> COMPONENT_TYPES = DeferredRegister.create(UtilityBelt.MOD_ID, Registries.DATA_COMPONENT_TYPE);
	public static final RegistrySupplier<DataComponentType<UtilityBeltInventory>> UTILITY_BELT_INVENTORY = COMPONENT_TYPES.register("utility_belt_inventory", () ->
	      DataComponentType.<UtilityBeltInventory>builder().persistent(UtilityBeltInventory.CODEC).cacheEncoding().build()
	);
	public static final RegistrySupplier<DataComponentType<Integer>> UTILITY_BELT_SIZE = COMPONENT_TYPES.register("utility_belt_size", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).cacheEncoding().build());

	public static void init() {
		COMPONENT_TYPES.register();
	}

	public static int getBeltSize(ItemStack stack) {
		if (!stack.has(UTILITY_BELT_SIZE.get())) {
			stack.set(UTILITY_BELT_SIZE.get(), UtilityBelt.COMMON_CONFIG.get().initialBeltSize);
		}

		return stack.get(UTILITY_BELT_SIZE.get());
	}

	public static UtilityBeltInventory getBeltInventory(ItemStack stack) {
		int size = getBeltSize(stack);
		if (!stack.has(UTILITY_BELT_INVENTORY.get())) {
			stack.set(UTILITY_BELT_INVENTORY.get(), UtilityBeltInventory.empty(size));
		}

		UtilityBeltInventory inv = stack.get(UTILITY_BELT_INVENTORY.get());
		if (size != inv.getContainerSize()) {
			inv = inv.copyWithSize(size);
			stack.set(ModComponents.UTILITY_BELT_INVENTORY.get(), inv);
		}

		return inv;
	}

	// This should only be called by the state managers, or when the belt is NOT equipped
	public static void setBeltInventory(ItemStack stack, UtilityBeltInventory inv) {
		stack.set(UTILITY_BELT_INVENTORY.get(), inv);
	}

	public static void setBeltSize(ItemStack stack, int newSize) {
		stack.set(UTILITY_BELT_SIZE.get(), newSize);
		setBeltInventory(stack, getBeltInventory(stack).copyWithSize(newSize));
	}
}
