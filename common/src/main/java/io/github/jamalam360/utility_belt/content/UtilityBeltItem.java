package io.github.jamalam360.utility_belt.content;

import io.github.jamalam360.utility_belt.content.register.ModComponents;
import io.github.jamalam360.utility_belt.content.register.ModItems;
import io.github.jamalam360.utility_belt.util.UtilityBeltInventory;
import io.github.jamalam360.utility_belt.util.UtilityBeltInventory.Mutable;
import io.github.jamalam360.utility_belt.network.ServerNetworking;
import io.github.jamalam360.utility_belt.state.StateManager;
import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class UtilityBeltItem extends AccessoryItem {

	private static final int BAR_COLOR = Mth.color((int) (0.4 * 255), (int) (0.4 * 255), (int) (1.0 * 255));

	public UtilityBeltItem() {
		super(new Item.Properties().stacksTo(1));
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean handleStack(ItemStack stack, UtilityBeltInventory.Mutable inv, Consumer<ItemStack> slotAccess) {
		if (stack.isEmpty()) {
			for (int i = 0; i < inv.getContainerSize(); i++) {
				if (!inv.getItem(i).isEmpty()) {
					ItemStack removed = inv.removeItem(i, stack.getCount());
					slotAccess.accept(removed);
					return true;
				}
			}
		} else if (isValidItem(stack)) {
			for (int i = 0; i < inv.getContainerSize(); i++) {
				if (inv.getItem(i).isEmpty()) {
					inv.setItem(i, stack);
					slotAccess.accept(ItemStack.EMPTY);
					return true;
				}
			}
		}

		return false;
	}

	private static void playInsertSound(Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}

	public static boolean isValidItem(ItemStack stack) {
		return
				stack.getItem() instanceof AxeItem ||
						stack.getItem() instanceof BrushItem ||
						stack.getItem() instanceof FishingRodItem ||
						stack.getItem() instanceof FlintAndSteelItem ||
						stack.getItem() instanceof FoodOnAStickItem<?> ||
						stack.getItem() instanceof HoeItem ||
						stack.getItem() instanceof ProjectileWeaponItem ||
						stack.getItem() instanceof ShearsItem ||
						stack.getItem() instanceof ShovelItem ||
						stack.getItem() instanceof SpyglassItem ||
						stack.getItem() instanceof TridentItem ||
						stack.getItem() instanceof SwordItem ||
						stack.getItem() instanceof TieredItem ||
						stack.isEmpty() ||
						stack.is(ModItems.ALLOWED_IN_UTILITY_BELT);
	}

	@Nullable
	public static ItemStack getBelt(Player player) {
		if (player.accessoriesCapability() == null) {
			return null;
		}

		@Nullable SlotEntryReference slot = player.accessoriesCapability().getFirstEquipped(ModItems.UTILITY_BELT_ITEM.get());

		if (slot != null) {
			return slot.stack();
		} else {
			return null;
		}
	}

	@Override
	public boolean isBarVisible(ItemStack itemStack) {
		return ModComponents.getBeltInventory(itemStack).items().stream().anyMatch(s -> !s.isEmpty());
	}

	@Override
	public int getBarWidth(ItemStack itemStack) {
		UtilityBeltInventory inv = ModComponents.getBeltInventory(itemStack);
		int size = Math.toIntExact(inv.items().stream().filter((s) -> !s.isEmpty()).count());
		return Math.min(1 + 12 * (size / inv.getContainerSize()), 13);
	}

	@Override
	public int getBarColor(ItemStack itemStack) {
		return BAR_COLOR;
	}

	@Override
	public void appendHoverText(ItemStack belt, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
		super.appendHoverText(belt, level, tooltipComponents, flag);
		UtilityBeltInventory inv = ModComponents.getBeltInventory(belt);
		tooltipComponents.add(Component.translatable("text.utility_belt.tooltip.size", inv.getContainerSize()));

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				tooltipComponents.add(Component.literal("- ").append(stack.getHoverName()));
			}
		}
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack belt, Slot slot, ClickAction clickAction, Player player) {
		if (clickAction != ClickAction.SECONDARY || !slot.allowModification(player)) {
			return false;
		}

		ItemStack slotStack = slot.getItem();
		UtilityBeltInventory.Mutable inv = new Mutable(ModComponents.getBeltInventory(belt));

		if (!handleStack(slotStack, inv, slot::set)) {
			return false;
		}

		playInsertSound(player);
		ModComponents.setBeltInventory(belt, inv.toImmutable());
		return true;
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack belt, ItemStack otherStack, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
		if (clickAction != ClickAction.SECONDARY || !slot.allowModification(player)) {
			return false;
		}

		UtilityBeltInventory.Mutable inv = new Mutable(ModComponents.getBeltInventory(belt));

		if (!handleStack(otherStack, inv, slotAccess::set)) {
			return false;
		}

		playInsertSound(player);
		ModComponents.setBeltInventory(belt, inv.toImmutable());
		return true;
	}

	@Override
	public void onDestroyed(ItemEntity itemEntity) {
		super.onDestroyed(itemEntity);
		UtilityBeltInventory inv = ModComponents.getBeltInventory(itemEntity.getItem());
		
		for (ItemStack stack : inv.items()) {
			if (!stack.isEmpty() && itemEntity.level() instanceof ServerLevel server) {
				itemEntity.spawnAtLocation(stack);
			}
		}
	}

	@Override
	public void onUnequip(ItemStack stack, SlotReference reference) {
		if (reference.entity() instanceof ServerPlayer player) {
			StateManager.getStateManager(player).setInBelt(player, false);
			StateManager.getStateManager(player).setSelectedBeltSlot(player, 0);
			ServerNetworking.sendBeltUnequippedToClient(player);
		}
	}

	@Override
	public void onEquip(ItemStack stack, SlotReference reference) {
		if (reference.entity() instanceof Player player && !player.level().isClientSide()) {
			StateManager.getStateManager(player).setInventory(player, new Mutable(ModComponents.getBeltInventory(stack)));
		}
	}
}
