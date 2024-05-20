package io.github.jamalam360.utility_belt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class UtilityBeltInventory extends SimpleContainer {
	public UtilityBeltInventory() {
		super(4);
	}

	@Override
	public void fromTag(ListTag listTag) {
		this.clearContent();

		for (int i = 0; i < listTag.size(); ++i) {
			this.setItem(i, ItemStack.of(listTag.getCompound(i)));
		}
	}

	@Override
	public ListTag createTag() {
		ListTag listTag = new ListTag();

		for (int i = 0; i < this.getContainerSize(); ++i) {
			listTag.add(this.getItem(i).save(new CompoundTag()));
		}

		return listTag;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UtilityBeltInventory other) {
			if (other.getContainerSize() == this.getContainerSize()) {
				for (int i = 0; i < this.getContainerSize(); i++) {
					if (!ItemStack.matches(this.getItem(i), other.getItem(i))) {
						return false;
					}
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public UtilityBeltInventory clone() {
		UtilityBeltInventory inv = new UtilityBeltInventory();
		for (int i = 0; i < this.getContainerSize(); i++) {
			inv.setItem(i, this.getItem(i).copy());
		}

		return inv;
	}
}
