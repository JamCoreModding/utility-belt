package io.github.jamalam360.utility_belt.datagen;

import io.github.jamalam360.utility_belt.content.register.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class Advancements extends FabricAdvancementProvider {
	protected Advancements(FabricDataOutput output) {
		super(output);
	}


	@Override
	public void generateAdvancement(Consumer<Advancement> consumer) {
		Advancement.Builder.advancement()
				.display(ModItems.UTILITY_BELT_ITEM.get(), Component.translatable("advancements.utility_belt.utility_belt.title"), Component.translatable("advancements.utility_belt.utility_belt.description"), null, FrameType.TASK, true, true, false)
				.addCriterion("obtain_belt",  InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.UTILITY_BELT_ITEM.get()))
				.rewards(AdvancementRewards.Builder.experience(25))
				.save(consumer, "utility_belt:obtain_utility_belt");
	}
}
