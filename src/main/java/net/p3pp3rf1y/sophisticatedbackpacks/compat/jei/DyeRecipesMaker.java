package net.p3pp3rf1y.sophisticatedbackpacks.compat.jei;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.util.ColorHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DyeRecipesMaker {
	private DyeRecipesMaker() {
	}

	/**
	 * 26.3 的 Ingredient 不再支持按 TagKey 直接构造，这里从注册表的标签集合里取对应的 HolderSet。
	 */
	private static Optional<Ingredient> tagIngredient(TagKey<Item> tag) {
		return BuiltInRegistries.ITEM.getTags().filter(named -> named.key().equals(tag)).findFirst().map(Ingredient::of);
	}

	private static ShapedRecipe createRecipe(String group, ShapedRecipePattern pattern, ItemStack output) {
		return new ShapedRecipe(new Recipe.CommonInfo(true), new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, group), pattern,
				new ItemStackTemplate(output.getItem()));
	}

	public static List<RecipeHolder<CraftingRecipe>> getRecipes() {
		List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
		addSingleColorRecipes(recipes);
		addMultipleColorsRecipe(recipes);

		return recipes;
	}

	private static void addMultipleColorsRecipe(List<RecipeHolder<CraftingRecipe>> recipes) {
		List<Optional<Ingredient>> ingredients = new ArrayList<>();
		ingredients.add(tagIngredient(ConventionalItemTags.YELLOW_DYES));
		ingredients.add(Optional.of(Ingredient.of(ModItems.BACKPACK.get())));
		ingredients.add(Optional.empty());
		ingredients.add(tagIngredient(ConventionalItemTags.LIME_DYES));
		ingredients.add(tagIngredient(ConventionalItemTags.BLUE_DYES));
		ingredients.add(tagIngredient(ConventionalItemTags.BLACK_DYES));

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
		recipes.add(new RecipeHolder<>(net.minecraft.resources.ResourceKey.create(Registries.RECIPE, id), createRecipe("", pattern, backpackOutput)));
	}

	private static void addSingleColorRecipes(List<RecipeHolder<CraftingRecipe>> recipes) {
		for (DyeColor color : DyeColor.values()) {
			ItemStack backpackOutput = new ItemStack(ModItems.BACKPACK.get());
			BackpackWrapper.fromStack(backpackOutput).setColors(color.getTextureDiffuseColor(), color.getTextureDiffuseColor());
			List<Optional<Ingredient>> ingredients = new ArrayList<>();
			ingredients.add(Optional.of(Ingredient.of(ModItems.BACKPACK.get())));
			Optional<Ingredient> dye = tagIngredient(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "dyes/" + color.getName())));
			if (dye.isEmpty()) {
				continue;
			}
			ingredients.add(dye);

			ShapedRecipePattern pattern = new ShapedRecipePattern(1, 2, ingredients, Optional.empty());
			Identifier id = Identifier.fromNamespaceAndPath(SophisticatedBackpacks.MOD_ID, "single_color_" + color.getSerializedName());
			recipes.add(new RecipeHolder<>(net.minecraft.resources.ResourceKey.create(Registries.RECIPE, id), createRecipe("", pattern, backpackOutput)));
		}
	}
}
