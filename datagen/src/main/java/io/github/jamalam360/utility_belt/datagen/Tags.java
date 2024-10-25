package io.github.jamalam360.utility_belt.datagen;

import io.github.jamalam360.utility_belt.UtilityBelt;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class Tags extends FabricTagProvider.ItemTagProvider {

	public Tags(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
		super(output, completableFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider wrapperLookup) {
		getOrCreateTagBuilder(UtilityBelt.ALLOWED_IN_UTILITY_BELT)
				.setReplace(false)
				.addOptional(ResourceLocation.fromNamespaceAndPath("create", "wrench"));
		
		getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("accessories", "belt")))
				.setReplace(false)
				.add(UtilityBelt.UTILITY_BELT_ITEM.getId());
	}
}
