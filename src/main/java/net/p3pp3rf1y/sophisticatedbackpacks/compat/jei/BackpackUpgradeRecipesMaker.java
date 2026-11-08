package net.p3pp3rf1y.sophisticatedbackpacks.compat.jei;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Synthetic JEI recipes for backpack tiers.
 * <p>
 * 26.3 clients no longer keep a queryable RecipeManager, so the upgrade recipes are rebuilt
 * locally from the same shaped patterns the datapack generators use. Packed patterns keep the
 * recipe encodable when JEI serializes a bookmark.
 */
public final class BackpackUpgradeRecipesMaker {
	private BackpackUpgradeRecipesMaker() {
	}

	public static List<RecipeHolder<CraftingRecipe>> getRecipes() {
		List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
		Ingredient leather = ofTag("leathers");
		Ingredient string = ofTag("strings");
		Ingredient woodenChest = ofTag("chests/wooden");
		Ingredient copper = ofTag("ingots/copper");
		Ingredient iron = ofTag("ingots/iron");
		Ingredient gold = ofTag("ingots/gold");
		Ingredient diamond = ofTag("gems/diamond");
		Ingredient backpack = Ingredient.of(ModItems.BACKPACK.get());
		Ingredient copperBackpack = Ingredient.of(ModItems.COPPER_BACKPACK.get());
		Ingredient ironBackpack = Ingredient.of(ModItems.IRON_BACKPACK.get());
		Ingredient goldBackpack = Ingredient.of(ModItems.GOLD_BACKPACK.get());

		add(recipes, "basic_backpack", Map.of('S', string, 'L', leather, 'C', woodenChest),
				List.of("SLS", "SCS", "LLL"), ModItems.BACKPACK.get());
		add(recipes, "copper_backpack", Map.of('C', copper, 'B', backpack),
				List.of("CCC", "CBC", "CCC"), ModItems.COPPER_BACKPACK.get());
		add(recipes, "iron_backpack", Map.of('I', iron, 'B', backpack),
				List.of("III", "IBI", "III"), ModItems.IRON_BACKPACK.get());
		add(recipes, "iron_backpack_from_copper", Map.of('I', iron, 'B', copperBackpack),
				List.of(" I ", "IBI", " I "), ModItems.IRON_BACKPACK.get());
		add(recipes, "gold_backpack", Map.of('G', gold, 'B', ironBackpack),
				List.of("GGG", "GBG", "GGG"), ModItems.GOLD_BACKPACK.get());
		add(recipes, "diamond_backpack", Map.of('D', diamond, 'B', goldBackpack),
				List.of("DDD", "DBD", "DDD"), ModItems.DIAMOND_BACKPACK.get());
		return recipes;
	}

	private static void add(List<RecipeHolder<CraftingRecipe>> recipes, String name,
			Map<Character, Ingredient> key, List<String> rows, ItemLike result) {
		ShapedRecipePattern pattern = ShapedRecipePattern.of(key, rows);
		Identifier id = Identifier.fromNamespaceAndPath(SophisticatedBackpacks.MOD_ID, "jei/" + name);
		recipes.add(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, id),
				new ShapedRecipe(RecipeBuilder.createCraftingCommonInfo(true),
						new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, ""),
						pattern, ItemStackTemplate.fromNonEmptyStack(new ItemStack(result)))));
	}

	private static Ingredient ofTag(String path) {
		TagKey<Item> tag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", path));
		List<Holder<Item>> holders = new ArrayList<>();
		BuiltInRegistries.ITEM.getTagOrEmpty(tag).forEach(holders::add);
		return Ingredient.of(HolderSet.direct(holders));
	}
}
