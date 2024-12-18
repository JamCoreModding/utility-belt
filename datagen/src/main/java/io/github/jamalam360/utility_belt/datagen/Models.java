package io.github.jamalam360.utility_belt.datagen;

import io.github.jamalam360.utility_belt.UtilityBelt;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;

public class Models extends FabricModelProvider {
	public Models(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators gen) {

	}

	@Override
	public void generateItemModels(ItemModelGenerators gen) {
		gen.generateFlatItem(UtilityBelt.UTILITY_BELT_ITEM.get(), ModelTemplates.FLAT_ITEM);
	}
}
