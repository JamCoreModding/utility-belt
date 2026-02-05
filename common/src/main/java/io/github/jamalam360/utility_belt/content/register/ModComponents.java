package io.github.jamalam360.utility_belt.content.register;

import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.util.UtilityBeltInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public class ModComponents {
	private static final String KEY_SIZE = "utility_belt:size";
	private static final String KEY_INVENTORY = "utility_belt:inventory";

	public static void init() {
	}

	public static int getBeltSize(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		if (!tag.contains(KEY_SIZE)) {
			tag.putInt(KEY_SIZE, UtilityBelt.COMMON_CONFIG.get().initialBeltSize);
			stack.setTag(tag);
		}

		return tag.getInt(KEY_SIZE);
	}

	public static UtilityBeltInventory getBeltInventory(ItemStack stack) {
		int size = getBeltSize(stack);
		CompoundTag tag = stack.getOrCreateTag();
		if (!tag.contains(KEY_INVENTORY)) {
			tag.put(KEY_INVENTORY, saveInventoryToTag(UtilityBeltInventory.empty(size)));
			stack.setTag(tag);
		}

		UtilityBeltInventory inv = loadInventoryFromTag(tag.getList(KEY_INVENTORY, 10));
		if (size != inv.getContainerSize()) {
			inv = inv.copyWithSize(size);
			tag.put(KEY_INVENTORY, saveInventoryToTag(inv));
			stack.setTag(tag);
		}

		return inv;
	}

	// This should only be called by the state managers, or when the belt is NOT equipped
	public static void setBeltInventory(ItemStack stack, UtilityBeltInventory inv) {
		CompoundTag tag = stack.getOrCreateTag();
		tag.put(KEY_INVENTORY, saveInventoryToTag(inv));
		stack.setTag(tag);
	}

	public static void setBeltSize(ItemStack stack, int newSize) {
		CompoundTag tag = stack.getOrCreateTag();
		tag.putInt(KEY_SIZE, newSize);
		stack.setTag(tag);
		setBeltInventory(stack, getBeltInventory(stack).copyWithSize(newSize));
	}

	private static ListTag saveInventoryToTag(UtilityBeltInventory inv) {
		ListTag tag = new ListTag();

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack itemStack = inv.getItem(i);
			CompoundTag itemTag = new CompoundTag();
			itemStack.save(itemTag);
			tag.add(itemTag);
		}

		return tag;
	}

	private static UtilityBeltInventory loadInventoryFromTag(ListTag tag) {
		UtilityBeltInventory.Mutable inv = new UtilityBeltInventory.Mutable(UtilityBeltInventory.empty(tag.size()));

		for (int i = 0; i < tag.size(); i++) {
			CompoundTag compoundTag = tag.getCompound(i);
			inv.setItem(i, ItemStack.of(compoundTag));
		}

		return inv.toImmutable();
	}
}
