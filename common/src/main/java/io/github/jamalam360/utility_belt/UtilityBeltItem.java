package io.github.jamalam360.utility_belt;

import io.github.jamalam360.utility_belt.UtilityBeltInventory.Mutable;
import io.github.jamalam360.utility_belt.network.ServerNetworking;
import io.github.jamalam360.utility_belt.state.StateManager;
import io.wispforest.accessories.api.core.AccessoryItem;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class UtilityBeltItem extends AccessoryItem {

	private static final int BAR_COLOR = ARGB.color((int) (0.4 * 255), (int) (0.4 * 255), (int) (1.0 * 255));

	public UtilityBeltItem() {
		super(new Item.Properties().stacksTo(1).component(UtilityBelt.UTILITY_BELT_INVENTORY_COMPONENT_TYPE.get(), UtilityBeltInventory.EMPTY).setId(ResourceKey.create(Registries.ITEM, UtilityBelt.id("utility_belt"))));
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean handleStack(ItemStack stack, UtilityBeltInventory.Mutable inv, Consumer<ItemStack> slotAccess) {
		if (stack.isEmpty()) {
			for (int i = 0; i < inv.getContainerSize(); i++) {
				if (!inv.getItem(i).isEmpty()) {
					ItemStack removed = inv.removeItemNoUpdate(i);
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
						stack.getItem() instanceof MaceItem ||
						stack.getItem() instanceof ProjectileWeaponItem ||
						stack.getItem() instanceof ShearsItem ||
						stack.getItem() instanceof ShovelItem ||
						stack.getItem() instanceof SpyglassItem ||
						stack.getItem() instanceof TridentItem ||
						stack.has(DataComponents.TOOL) || // TODO: check if these two checks are valid
						stack.has(DataComponents.WEAPON) ||
						stack.isEmpty() ||
						stack.is(UtilityBelt.ALLOWED_IN_UTILITY_BELT);
	}

	public static UtilityBeltInventory getInventory(ItemStack stack) {
		if (!stack.has(UtilityBelt.UTILITY_BELT_INVENTORY_COMPONENT_TYPE.get())) {
			stack.set(UtilityBelt.UTILITY_BELT_INVENTORY_COMPONENT_TYPE.get(), UtilityBeltInventory.EMPTY);
		}

		return stack.get(UtilityBelt.UTILITY_BELT_INVENTORY_COMPONENT_TYPE.get());
	}

	// This should only be called by the state managers, or when the belt is NOT equipped
	public static void setInventory(ItemStack stack, UtilityBeltInventory inv) {
		stack.set(UtilityBelt.UTILITY_BELT_INVENTORY_COMPONENT_TYPE.get(), inv);
	}

	@Nullable
	public static ItemStack getBelt(Player player) {
		if (player.accessoriesCapability() == null) {
			return null;
		}

		@Nullable SlotEntryReference slot = player.accessoriesCapability().getFirstEquipped(UtilityBelt.UTILITY_BELT_ITEM.get());

		if (slot != null) {
			return slot.stack();
		} else {
			return null;
		}
	}

	@Override
	public boolean isBarVisible(ItemStack itemStack) {
		return getInventory(itemStack).items().stream().anyMatch(s -> !s.isEmpty());
	}

	@Override
	public int getBarWidth(ItemStack itemStack) {
		long size = getInventory(itemStack).items().stream().filter((s) -> !s.isEmpty()).count();
		return size == 4L ? 13 : (int) (size * 3);
	}

	@Override
	public int getBarColor(ItemStack itemStack) {
		return BAR_COLOR;
	}

	@Override
	public void appendHoverText(ItemStack belt, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
		super.appendHoverText(belt, context, tooltipDisplay, tooltipAdder, flag);
		UtilityBeltInventory inv = getInventory(belt);

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				tooltipAdder.accept(Component.literal("- ").append(stack.getHoverName()));
			}
		}
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack belt, Slot slot, ClickAction clickAction, Player player) {
		if (clickAction != ClickAction.SECONDARY || !slot.allowModification(player)) {
			return false;
		}

		ItemStack slotStack = slot.getItem();
		UtilityBeltInventory.Mutable inv = new Mutable(getInventory(belt));

		if (!handleStack(slotStack, inv, slot::set)) {
			return false;
		}

		playInsertSound(player);
		setInventory(belt, inv.toImmutable());
		return true;
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack belt, ItemStack otherStack, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
		if (clickAction != ClickAction.SECONDARY || !slot.allowModification(player)) {
			return false;
		}

		UtilityBeltInventory.Mutable inv = new Mutable(getInventory(belt));

		if (!handleStack(otherStack, inv, slotAccess::set)) {
			return false;
		}

		playInsertSound(player);
		setInventory(belt, inv.toImmutable());
		return true;
	}

	@Override
	public void onDestroyed(ItemEntity itemEntity) {
		super.onDestroyed(itemEntity);
		UtilityBeltInventory inv = getInventory(itemEntity.getItem());
		
		for (ItemStack stack : inv.items()) {
			if (!stack.isEmpty() && itemEntity.level() instanceof ServerLevel server) {
				itemEntity.spawnAtLocation(server, stack);
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
			StateManager.getStateManager(player).setInventory(player, new Mutable(getInventory(stack)));
		}
	}
}
