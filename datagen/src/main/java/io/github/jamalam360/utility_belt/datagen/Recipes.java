package io.github.jamalam360.utility_belt.datagen;

import io.github.jamalam360.utility_belt.UtilityBelt;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class Recipes extends FabricRecipeProvider {
	public Recipes(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
		return new RecipeProvider(provider, recipeOutput) {
			@Override
			public void buildRecipes() {
				shaped(RecipeCategory.TOOLS, UtilityBelt.UTILITY_BELT_ITEM.get())
						.pattern("SDS").pattern("L L").pattern("SLS")
						.define('S', Items.STRING).define('D', Items.DIAMOND).define('L', Items.LEATHER)
						.unlockedBy("has_diamond", has(Items.DIAMOND))
						.unlockedBy("has_leather", has(Items.LEATHER))
						.unlockedBy("has_string", has(Items.STRING))
						.save(recipeOutput);
			}
		};
	}

	@Override
	public String getName() {
		return "utility_belt_recipes";
	}
}
