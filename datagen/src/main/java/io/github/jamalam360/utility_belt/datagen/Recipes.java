package io.github.jamalam360.utility_belt.datagen;

import io.github.jamalam360.utility_belt.UtilityBelt;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.TransmuteResult;

import java.util.Optional;
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
				shaped(RecipeCategory.TOOLS, UtilityBelt.POUCH_ITEM.get())
						.pattern("SDS").pattern("L L").pattern("SLS")
						.define('S', Items.STRING).define('L', Items.LEATHER).define('D', Items.DIAMOND)
						.unlockedBy("has_string", has(Items.STRING))
						.unlockedBy("has_leather", has(Items.LEATHER))
						.unlockedBy("has_diamond", has(Items.DIAMOND))
						.save(this.output);

				shaped(RecipeCategory.TOOLS, UtilityBelt.UTILITY_BELT_ITEM.get())
						.pattern("LPL").pattern("S S").pattern("LPL")
						.define('L', Items.LEATHER).define('P', UtilityBelt.POUCH_ITEM.get()).define('S', Items.STRING)
						.unlockedBy("has_leather", has(Items.LEATHER))
						.unlockedBy("has_pouch", has(UtilityBelt.POUCH_ITEM.get()))
						.unlockedBy("has_string", has(Items.STRING))
						.save(this.output);

				this.createSmithingRecipe();
			}

			private void createSmithingRecipe() {
				ResourceLocation resourceLocation = UtilityBelt.id("upgrade_utility_belt");
				ResourceKey<Recipe<?>> resourceKey = ResourceKey.create(Registries.RECIPE, resourceLocation);
				Advancement.Builder builder = this.output.advancement()
						.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
						.rewards(AdvancementRewards.Builder.recipe(resourceKey))
						.requirements(AdvancementRequirements.Strategy.OR);
				builder.addCriterion("has_utility_belt", this.has(UtilityBelt.UTILITY_BELT_ITEM.get()));
				builder.addCriterion("has_pouch", this.has(UtilityBelt.POUCH_ITEM.get()));
				SmithingTransformRecipe smithingTransformRecipe = new SmithingTransformRecipe(
						Optional.empty(), Ingredient.of(UtilityBelt.UTILITY_BELT_ITEM.get()), Optional.of(Ingredient.of(UtilityBelt.POUCH_ITEM.get())), new TransmuteResult(UtilityBelt.UTILITY_BELT_ITEM.get())
				);
				output.accept(resourceKey, smithingTransformRecipe, builder.build(resourceKey.location().withPrefix("recipes/" + RecipeCategory.TOOLS.getFolderName() + "/")));
			}
		};
	}

	@Override
	public String getName() {
		return "utility_belt_recipes";
	}
}
