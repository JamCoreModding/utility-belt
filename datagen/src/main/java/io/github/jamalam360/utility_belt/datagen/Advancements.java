package io.github.jamalam360.utility_belt.datagen;

import io.github.jamalam360.utility_belt.content.register.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Advancements extends FabricAdvancementProvider {
	protected Advancements(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(output, registryLookup);
	}

	@Override
	public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
		Advancement.Builder.advancement()
				.display(ModItems.UTILITY_BELT_ITEM.get(), Component.translatable("advancements.utility_belt.utility_belt.title"), Component.translatable("advancements.utility_belt.utility_belt.description"), null, AdvancementType.TASK, true, true, false)
				.addCriterion("obtain_belt",  InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.UTILITY_BELT_ITEM.get()))
				.rewards(AdvancementRewards.Builder.experience(25))
				.save(consumer, "utility_belt:obtain_utility_belt");
	}
}
