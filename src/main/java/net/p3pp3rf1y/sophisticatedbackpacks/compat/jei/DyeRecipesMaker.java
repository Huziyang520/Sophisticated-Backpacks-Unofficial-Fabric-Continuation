package net.p3pp3rf1y.sophisticatedbackpacks.compat.jei;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.util.ColorHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Synthetic dye recipes so JEI can show how a backpack gets colored.
 * <p>
 * 26.3 recipes are built from a common info record and a book info record, and the pattern holds
 * {@code Optional} slots rather than a dense ingredient list.
 */
public class DyeRecipesMaker {
	private DyeRecipesMaker() {
	}

	public static List<RecipeHolder<CraftingRecipe>> getRecipes() {
		List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
		addSingleColorRecipes(recipes);
		addMultipleColorsRecipe(recipes);

		return recipes;
	}

	private static void addMultipleColorsRecipe(List<RecipeHolder<CraftingRecipe>> recipes) {
		List<Optional<Ingredient>> ingredients = new ArrayList<>();
		ingredients.add(Optional.of(ofTag(ConventionalItemTags.YELLOW_DYES)));
		ingredients.add(Optional.of(Ingredient.of(ModItems.BACKPACK.get())));
		ingredients.add(Optional.empty());
		ingredients.add(Optional.of(ofTag(ConventionalItemTags.LIME_DYES)));
		ingredients.add(Optional.of(ofTag(ConventionalItemTags.BLUE_DYES)));
		ingredients.add(Optional.of(ofTag(ConventionalItemTags.BLACK_DYES)));

		ItemStack backpackOutput = new ItemStack(ModItems.BACKPACK.get());
		int clothColor = ColorHelper.calculateColor(BackpackWrapper.DEFAULT_MAIN_COLOR, BackpackWrapper.DEFAULT_MAIN_COLOR, List.of(
				DyeColor.YELLOW, DyeColor.LIME
		));
		int trimColor = ColorHelper.calculateColor(BackpackWrapper.DEFAULT_ACCENT_COLOR, BackpackWrapper.DEFAULT_ACCENT_COLOR, List.of(
				DyeColor.BLUE, DyeColor.BLACK
		));

		BackpackWrapper.fromStack(backpackOutput).setColors(clothColor, trimColor);

		ShapedRecipePattern pattern = new ShapedRecipePattern(3, 1, ingredients, Optional.empty());
		Identifier id = Identifier.fromNamespaceAndPath(SophisticatedBackpacks.MOD_ID, "multiple_colors");
		recipes.add(new RecipeHolder<>(net.minecraft.resources.ResourceKey.create(Registries.RECIPE, id), new ShapedRecipe(
				net.minecraft.data.recipes.RecipeBuilder.createCraftingCommonInfo(true),
				new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, ""),
				pattern,
				ItemStackTemplate.fromNonEmptyStack(backpackOutput))));
	}

	private static void addSingleColorRecipes(List<RecipeHolder<CraftingRecipe>> recipes) {
		for (DyeColor color : DyeColor.values()) {
			ItemStack backpackOutput = new ItemStack(ModItems.BACKPACK.get());
			BackpackWrapper.fromStack(backpackOutput).setColors(color.getTextureDiffuseColor(), color.getTextureDiffuseColor());
			List<Optional<Ingredient>> ingredients = new ArrayList<>();
			ingredients.add(Optional.of(Ingredient.of(ModItems.BACKPACK.get())));
			ingredients.add(Optional.of(ofTag(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "dyes/" + color.getName())))));

			ShapedRecipePattern pattern = new ShapedRecipePattern(1, 2, ingredients, Optional.empty());
			Identifier id = Identifier.fromNamespaceAndPath(SophisticatedBackpacks.MOD_ID, "single_color_" + color.getSerializedName());
			recipes.add(new RecipeHolder<>(net.minecraft.resources.ResourceKey.create(Registries.RECIPE, id), new ShapedRecipe(
					net.minecraft.data.recipes.RecipeBuilder.createCraftingCommonInfo(true),
					new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, ""),
					pattern,
					ItemStackTemplate.fromNonEmptyStack(backpackOutput))));
		}
	}

	/**
	 * Builds an ingredient matching every item in a tag.
	 * <p>
	 * 26.3 only accepts a holder set here, so the tag is flattened against the item registry.
	 */
	private static Ingredient ofTag(TagKey<Item> tag) {
		List<Holder<Item>> holders = new ArrayList<>();
		BuiltInRegistries.ITEM.getTagOrEmpty(tag).forEach(holders::add);
		return Ingredient.of(HolderSet.direct(holders));
	}
}