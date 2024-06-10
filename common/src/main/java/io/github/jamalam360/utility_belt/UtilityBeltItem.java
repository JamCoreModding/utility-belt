package io.github.jamalam360.utility_belt;

import io.github.jamalam360.utility_belt.UtilityBeltInventory.Mutable;
import io.github.jamalam360.utility_belt.client.ClientNetworking;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SpyglassItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class UtilityBeltItem extends Item {

    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);

    public UtilityBeltItem() {
        super(new Item.Properties().stacksTo(1));
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
        return stack.getItem() instanceof TieredItem || stack.getItem() instanceof ProjectileWeaponItem || stack.getItem() instanceof FishingRodItem || stack.getItem() instanceof SpyglassItem || stack.getItem() instanceof TridentItem || stack.getItem() instanceof FlintAndSteelItem || stack.getItem() instanceof ShearsItem || stack.getItem() instanceof BrushItem || stack.isEmpty() || stack.is(UtilityBelt.ALLOWED_IN_UTILITY_BELT);
    }

    // Going to keep this name as is until 1.20.4 support is dropped, to keep the diffs smaller
    public static UtilityBeltInventory getInventoryFromTag(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();

        if (!tag.contains("Inventory")) {
            setInventory(stack, UtilityBeltInventory.EMPTY);
            return getInventoryFromTag(stack);
        }

        return UtilityBeltInventory.fromTag(tag.getList("Inventory", Tag.TAG_COMPOUND));
    }

    // This should only be called by the state managers, or when the belt is NOT equipped
    public static void setInventory(ItemStack stack, UtilityBeltInventory inv) {
        stack.getOrCreateTag().put("Inventory", inv.toTag());
    }

    @Nullable
    public static ItemStack getBelt(Player player) {
        ItemStack stack = UtilityBeltPlatform.getStackInBeltSlot(player);

        if (stack != null && stack.getItem() == UtilityBelt.UTILITY_BELT_ITEM.get()) {
            return stack;
        } else {
            return null;
        }
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return getInventoryFromTag(itemStack).items().stream().anyMatch(s -> !s.isEmpty());
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        long size = getInventoryFromTag(itemStack).items().stream().filter((s) -> !s.isEmpty()).count();
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
        UtilityBeltInventory.Mutable inv = new Mutable(getInventoryFromTag(belt));

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

        UtilityBeltInventory.Mutable inv = new Mutable(getInventoryFromTag(belt));

        if (!handleStack(otherStack, inv, slotAccess::set)) {
            return false;
        }

        playInsertSound(player);
        setInventory(belt, inv.toImmutable());
        return true;
    }

    public void onEquip(LivingEntity wearer, ItemStack belt) {
        if (wearer instanceof Player player && !player.level().isClientSide) {
            StateManager.getServerInstance().setInventory(player, new Mutable(getInventoryFromTag(belt)));
        }
    }

    public void onUnequip(LivingEntity wearer) {
        if (wearer instanceof Player player && player.level().isClientSide) {
            StateManager.getClientInstance().setInBelt(player, false);
            StateManager.getClientInstance().setSelectedBeltSlot(player, 0);
            ClientNetworking.sendNewStateToServer(false, 0, false);
        }
    }
}
