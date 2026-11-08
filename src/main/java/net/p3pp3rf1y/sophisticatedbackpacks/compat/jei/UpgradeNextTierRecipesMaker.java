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
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Synthetic JEI recipes for upgrade tier upgrades.
 * <p>
 * These are special shaped recipes that 26.3 does not expose to the client, so they are rebuilt
 * locally from the same patterns the datapack generator writes.
 */
public final class UpgradeNextTierRecipesMaker {
	private UpgradeNextTierRecipesMaker() {
	}

	public static List<RecipeHolder<CraftingRecipe>> getRecipes() {
		List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
		add(recipes, "advanced_compacting_upgrade", Map.of('C', ingredient("sophisticatedbackpacks:compacting_upgrade"), 'D', ingredient("#c:gems/diamond"), 'G', ingredient("#c:ingots/gold"), 'R', ingredient("#c:dusts/redstone")),
				List.of(" D ", "GCG", "RRR"), "sophisticatedbackpacks:advanced_compacting_upgrade");
		add(recipes, "advanced_deposit_upgrade", Map.of('D', ingredient("#c:gems/diamond"), 'G', ingredient("#c:ingots/gold"), 'R', ingredient("#c:dusts/redstone"), 'V', ingredient("sophisticatedbackpacks:deposit_upgrade")),
				List.of(" D ", "GVG", "RRR"), "sophisticatedbackpacks:advanced_deposit_upgrade");
		add(recipes, "advanced_feeding_upgrade", Map.of('D', ingredient("#c:gems/diamond"), 'G', ingredient("#c:ingots/gold"), 'R', ingredient("#c:dusts/redstone"), 'V', ingredient("sophisticatedbackpacks:feeding_upgrade")),
				List.of(" D ", "GVG", "RRR"), "sophisticatedbackpacks:advanced_feeding_upgrade");
		add(recipes, "advanced_filter_upgrade", Map.of('G', ingredient("#c:ingots/gold"), 'P', ingredient("sophisticatedbackpacks:filter_upgrade"), 'R', ingredient("#c:dusts/redstone")),
				List.of("GPG", "RRR"), "sophisticatedbackpacks:advanced_filter_upgrade");
		add(recipes, "advanced_magnet_upgrade", Map.of('E', ingredient("#c:ender_pearls"), 'I', ingredient("#c:ingots/iron"), 'L', ingredient("#c:gems/lapis"), 'P', ingredient("sophisticatedbackpacks:advanced_pickup_upgrade"), 'R', ingredient("#c:dusts/redstone")),
				List.of("EIE", "IPI", "R L"), "sophisticatedbackpacks:advanced_magnet_upgrade");
		add(recipes, "advanced_magnet_upgrade_from_basic", Map.of('D', ingredient("#c:gems/diamond"), 'G', ingredient("#c:ingots/gold"), 'M', ingredient("sophisticatedbackpacks:magnet_upgrade"), 'R', ingredient("#c:dusts/redstone")),
				List.of(" D ", "GMG", "RRR"), "sophisticatedbackpacks:advanced_magnet_upgrade");
		add(recipes, "advanced_pickup_upgrade", Map.of('D', ingredient("#c:gems/diamond"), 'G', ingredient("#c:ingots/gold"), 'P', ingredient("sophisticatedbackpacks:pickup_upgrade"), 'R', ingredient("#c:dusts/redstone")),
				List.of(" D ", "GPG", "RRR"), "sophisticatedbackpacks:advanced_pickup_upgrade");
		add(recipes, "advanced_pump_upgrade", Map.of('D', ingredient("#c:gems/diamond"), 'G', ingredient("#c:ingots/gold"), 'I', ingredient("minecraft:dispenser"), 'P', ingredient("sophisticatedbackpacks:pump_upgrade"), 'R', ingredient("#c:dusts/redstone")),
				List.of("DID", "GPG", "RRR"), "sophisticatedbackpacks:advanced_pump_upgrade");
		add(recipes, "advanced_refill_upgrade", Map.of('D', ingredient("#c:gems/diamond"), 'F', ingredient("sophisticatedbackpacks:refill_upgrade"), 'G', ingredient("#c:ingots/gold"), 'R', ingredient("#c:dusts/redstone")),
				List.of(" D ", "GFG", "RRR"), "sophisticatedbackpacks:advanced_refill_upgrade");
		add(recipes, "advanced_restock_upgrade", Map.of('D', ingredient("#c:gems/diamond"), 'G', ingredient("#c:ingots/gold"), 'R', ingredient("#c:dusts/redstone"), 'V', ingredient("sophisticatedbackpacks:restock_upgrade")),
				List.of(" D ", "GVG", "RRR"), "sophisticatedbackpacks:advanced_restock_upgrade");
		add(recipes, "advanced_tool_swapper_upgrade", Map.of('D', ingredient("#c:gems/diamond"), 'G', ingredient("#c:ingots/gold"), 'R', ingredient("#c:dusts/redstone"), 'V', ingredient("sophisticatedbackpacks:tool_swapper_upgrade")),
				List.of(" D ", "GVG", "RRR"), "sophisticatedbackpacks:advanced_tool_swapper_upgrade");
		add(recipes, "advanced_void_upgrade", Map.of('D', ingredient("#c:gems/diamond"), 'G', ingredient("#c:ingots/gold"), 'R', ingredient("#c:dusts/redstone"), 'V', ingredient("sophisticatedbackpacks:void_upgrade")),
				List.of(" D ", "GVG", "RRR"), "sophisticatedbackpacks:advanced_void_upgrade");
		add(recipes, "auto_blasting_upgrade", Map.of('D', ingredient("#c:gems/diamond"), 'G', ingredient("#c:ingots/gold"), 'H', ingredient("minecraft:hopper"), 'R', ingredient("#c:dusts/redstone"), 'S', ingredient("sophisticatedbackpacks:blasting_upgrade")),
				List.of("DHD", "RSH", "GHG"), "sophisticatedbackpacks:auto_blasting_upgrade");
		add(recipes, "auto_smelting_upgrade", Map.of('D', ingredient("#c:gems/diamond"), 'G', ingredient("#c:ingots/gold"), 'H', ingredient("minecraft:hopper"), 'R', ingredient("#c:dusts/redstone"), 'S', ingredient("sophisticatedbackpacks:smelting_upgrade")),
				List.of("DHD", "RSH", "GHG"), "sophisticatedbackpacks:auto_smelting_upgrade");
		add(recipes, "auto_smoking_upgrade", Map.of('D', ingredient("#c:gems/diamond"), 'G', ingredient("#c:ingots/gold"), 'H', ingredient("minecraft:hopper"), 'R', ingredient("#c:dusts/redstone"), 'S', ingredient("sophisticatedbackpacks:smoking_upgrade")),
				List.of("DHD", "RSH", "GHG"), "sophisticatedbackpacks:auto_smoking_upgrade");
		add(recipes, "battery_upgrade", Map.of('B', ingredient("sophisticatedbackpacks:upgrade_base"), 'G', ingredient("#c:ingots/gold"), 'R', ingredient("#c:storage_blocks/redstone")),
				List.of("GRG", "RBR", "GRG"), "sophisticatedbackpacks:battery_upgrade");
		add(recipes, "magnet_upgrade", Map.of('E', ingredient("#c:ender_pearls"), 'I', ingredient("#c:ingots/iron"), 'L', ingredient("#c:gems/lapis"), 'P', ingredient("sophisticatedbackpacks:pickup_upgrade"), 'R', ingredient("#c:dusts/redstone")),
				List.of("EIE", "IPI", "R L"), "sophisticatedbackpacks:magnet_upgrade");
		add(recipes, "pump_upgrade", Map.of('B', ingredient("sophisticatedbackpacks:upgrade_base"), 'G', ingredient("#c:glass_blocks"), 'P', ingredient("minecraft:piston"), 'S', ingredient("minecraft:sticky_piston"), 'U', ingredient("minecraft:bucket")),
				List.of("GUG", "PBS", "GUG"), "sophisticatedbackpacks:pump_upgrade");
		add(recipes, "tank_upgrade", Map.of('B', ingredient("sophisticatedbackpacks:upgrade_base"), 'G', ingredient("#c:glass_blocks")),
				List.of("GGG", "GBG", "GGG"), "sophisticatedbackpacks:tank_upgrade");
		return recipes;
	}

	private static void add(List<RecipeHolder<CraftingRecipe>> recipes, String name,
			Map<Character, Ingredient> key, List<String> rows, String resultId) {
		ItemStack result = new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.parse(resultId)));
		if (result.isEmpty()) {
			return;
		}
		ShapedRecipePattern pattern = ShapedRecipePattern.of(key, rows);
		Identifier id = Identifier.fromNamespaceAndPath(SophisticatedBackpacks.MOD_ID, "jei/upgrade_next_tier/" + name);
		recipes.add(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, id),
				new ShapedRecipe(RecipeBuilder.createCraftingCommonInfo(true),
						new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, ""),
						pattern, ItemStackTemplate.fromNonEmptyStack(result))));
	}

	private static Ingredient ingredient(String value) {
		if (value.startsWith("#")) {
			TagKey<Item> tag = TagKey.create(Registries.ITEM, Identifier.parse(value.substring(1)));
			List<Holder<Item>> holders = new ArrayList<>();
			BuiltInRegistries.ITEM.getTagOrEmpty(tag).forEach(holders::add);
			return Ingredient.of(HolderSet.direct(holders));
		}
		return Ingredient.of(BuiltInRegistries.ITEM.getValue(Identifier.parse(value)));
	}
}
