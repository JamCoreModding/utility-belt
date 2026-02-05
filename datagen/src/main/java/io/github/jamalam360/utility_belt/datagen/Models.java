package io.github.jamalam360.utility_belt.datagen;

import io.github.jamalam360.utility_belt.content.register.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;

public class Models extends FabricModelProvider {
	public Models(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators gen) {
	}

	@Override
	public void generateItemModels(ItemModelGenerators gen) {
		gen.generateFlatItem(ModItems.UTILITY_BELT_ITEM.get(), ModelTemplates.FLAT_ITEM);
		gen.generateFlatItem(ModItems.POUCH_ITEM.get(), ModelTemplates.FLAT_ITEM);
	}
}
