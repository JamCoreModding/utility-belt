package io.github.jamalam360.utility_belt;

import dev.architectury.platform.Platform;
import earth.terrarium.baubly.common.*;
import io.github.jamalam360.jamlib.JamLibPlatform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class UtilityBeltItem extends Item implements Bauble {

	private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);

	public UtilityBeltItem() {
		super(new Item.Properties().stacksTo(1));
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean handleStack(ItemStack stack, UtilityBeltInventory inv, Consumer<ItemStack> slotAccess) {
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
		return stack.getItem() instanceof TieredItem || stack.getItem() instanceof ProjectileWeaponItem || stack.getItem() instanceof FishingRodItem || stack.getItem() instanceof SpyglassItem || stack.getItem() instanceof TridentItem || stack.getItem() instanceof FlintAndSteelItem || stack.getItem() instanceof ShearsItem || stack.getItem() instanceof BrushItem || stack.isEmpty() || stack.is(UtilityBelt.ALLOWED_IN_UTILITY_BELT);
	}

	public static UtilityBeltInventory getInventoryFromTag(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		UtilityBeltInventory inv = new UtilityBeltInventory();

		if (!tag.contains("Inventory")) {
			tag.put("Inventory", inv.createTag());
		} else {
			inv.fromTag(tag.getList("Inventory", CompoundTag.TAG_COMPOUND));
		}

		return inv;
	}

	@Nullable
	public static ItemStack getBelt(Player player) {
		if (Platform.isForgeLike() && BaubleUtils.getBaubleContainer(player, DefaultSlotIdentifiers.BELT.curioId()) == null) {
			return null;
		} else if ((JamLibPlatform.getPlatform().isFabricLike()) && BaubleUtils.getBaubleContainer(player, DefaultSlotIdentifiers.BELT.trinketIds()[0]) == null) {
			return null;
		}

		Map<String, Container> baubles = BaubleUtils.getBaubleContainer(player, DefaultSlotIdentifiers.BELT);
		ItemStack stack = ItemStack.EMPTY;

		if (baubles.containsKey(DefaultSlotIdentifiers.BELT.curioId())) {
			stack = baubles.get(DefaultSlotIdentifiers.BELT.curioId()).getItem(0);
		} else if (baubles.containsKey(DefaultSlotIdentifiers.BELT.trinketIds()[0])) {
			stack = baubles.get(DefaultSlotIdentifiers.BELT.trinketIds()[0]).getItem(0);
		}

		if (stack.getItem() == UtilityBelt.UTILITY_BELT.get()) {
			return stack;
		} else {
			return null;
		}
	}

	@Override
	public boolean isBarVisible(ItemStack itemStack) {
		return getInventoryFromTag(itemStack).hasAnyMatching(s -> !s.isEmpty());
	}

	@Override
	public int getBarWidth(ItemStack itemStack) {
		long size = getInventoryFromTag(itemStack).getItems().stream().filter((s) -> !s.isEmpty()).count();
		return size == 4L ? 13 : (int) (size * 3);
	}

	@Override
	public int getBarColor(ItemStack itemStack) {
		return BAR_COLOR;
	}

	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
		UtilityBeltInventory inv = getInventoryFromTag(itemStack);

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				list.add(Component.literal("- ").append(stack.getHoverName()));
			}
		}
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack belt, Slot slot, ClickAction clickAction, Player player) {
		if (clickAction != ClickAction.SECONDARY || !slot.allowModification(player)) {
			return false;
		}

		ItemStack slotStack = slot.getItem();
		UtilityBeltInventory inv = getInventoryFromTag(belt);

		if (!handleStack(slotStack, inv, slot::set)) {
			return false;
		}

		playInsertSound(player);
		belt.getOrCreateTag().put("Inventory", inv.createTag());
		return true;
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack belt, ItemStack otherStack, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
		if (clickAction != ClickAction.SECONDARY || !slot.allowModification(player)) {
			return false;
		}

		UtilityBeltInventory inv = getInventoryFromTag(belt);

		if (!handleStack(otherStack, inv, slotAccess::set)) {
			return false;
		}

		playInsertSound(player);
		belt.getOrCreateTag().put("Inventory", inv.createTag());
		return true;
	}

	@Override
	public boolean canEquip(ItemStack stack, SlotInfo slot) {
		Container container = BaubleUtils.getBaubleContainer(slot.wearer(), slot.identifier());

		if (container != null && container.hasAnyMatching((s) -> s.getItem() == this)) {
			return false;
		}

		return slot.identifier().equals(DefaultSlotIdentifiers.BELT.curioId()) || slot.identifier().equals(DefaultSlotIdentifiers.BELT.trinketIds()[0]);
	}

	@Override
	public DropRule getDropRule(ItemStack stack, SlotInfo slot) {
		return DropRule.ALWAYS_DROP;
	}
}
