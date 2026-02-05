package io.github.jamalam360.utility_belt.datagen;

import io.github.jamalam360.utility_belt.UtilityBelt;
import io.github.jamalam360.utility_belt.content.register.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class Recipes extends FabricRecipeProvider {
	public Recipes(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	public void buildRecipes(RecipeOutput output) {
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.POUCH_ITEM.get())
						.pattern("SDS").pattern("L L").pattern("SLS")
						.define('S', Items.STRING).define('L', Items.LEATHER).define('D', Items.DIAMOND)
						.unlockedBy("has_string", has(Items.STRING))
						.unlockedBy("has_leather", has(Items.LEATHER))
						.unlockedBy("has_diamond", has(Items.DIAMOND))
				.save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.UTILITY_BELT_ITEM.get())
				.pattern("LPL").pattern("S S").pattern("LPL")
				.define('L', Items.LEATHER).define('P', ModItems.POUCH_ITEM.get()).define('S', Items.STRING)
				.unlockedBy("has_leather", has(Items.LEATHER))
				.unlockedBy("has_pouch", has(ModItems.POUCH_ITEM.get()))
				.unlockedBy("has_string", has(Items.STRING))
				.save(output);

		this.createSmithingRecipe(output);
	}

	private void createSmithingRecipe(RecipeOutput output) {
		ResourceLocation resourceLocation = UtilityBelt.id("upgrade_utility_belt");

		SmithingTransformRecipeBuilder.smithing(
						Ingredient.EMPTY,
						Ingredient.of(ModItems.UTILITY_BELT_ITEM.get()),
						Ingredient.of(ModItems.POUCH_ITEM.get()),
						RecipeCategory.TOOLS,
						ModItems.UTILITY_BELT_ITEM.get()
				).unlocks("has_utility_belt", has(ModItems.UTILITY_BELT_ITEM.get()))
				.unlocks("has_pouch", has(ModItems.POUCH_ITEM.get()))
				.save(output, resourceLocation);
	}

	@Override
	public String getName() {
		return "utility_belt_recipes";
	}
}
