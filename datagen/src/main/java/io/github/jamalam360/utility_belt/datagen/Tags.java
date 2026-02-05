package io.github.jamalam360.utility_belt.datagen;

import io.github.jamalam360.utility_belt.content.register.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.concurrent.CompletableFuture;

public class Tags extends FabricTagProvider.ItemTagProvider {

	public Tags(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
		super(output, completableFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider wrapperLookup) {
		getOrCreateRawBuilder(ModItems.ALLOWED_IN_UTILITY_BELT)
				.addOptionalElement(new ResourceLocation("create", "wrench"));
		
		getOrCreateRawBuilder(TagKey.create(Registries.ITEM, new ResourceLocation("accessories", "belt")))
				.addElement(ModItems.UTILITY_BELT_ITEM.getId());
	}
}
