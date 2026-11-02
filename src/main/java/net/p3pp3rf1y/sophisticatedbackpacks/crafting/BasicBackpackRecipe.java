package net.p3pp3rf1y.sophisticatedbackpacks.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.crafting.IWrapperRecipe;
import net.p3pp3rf1y.sophisticatedcore.crafting.RecipeWrapperSerializer;

public class BasicBackpackRecipe extends ShapedRecipe implements IWrapperRecipe<ShapedRecipe> {
	private final ShapedRecipe compose;

	public BasicBackpackRecipe(ShapedRecipe compose) {
		super(compose.commonInfo, compose.bookInfo, compose.pattern, compose.result);
		this.compose = compose;
	}

	@Override
	public ShapedRecipe getCompose() {
		return compose;
	}

	@Override
	public ItemStack assemble(CraftingInput inv) {
		ItemStack result = super.assemble(inv);
		removeUuid(result);
		return result;
	}

	private void removeUuid(ItemStack backpack) {
		BackpackWrapper.fromStack(backpack).removeContentsUuid();
	}

	@Override
	public RecipeSerializer<ShapedRecipe> getSerializer() {
		return (RecipeSerializer<ShapedRecipe>) (RecipeSerializer<?>) ModItems.BASIC_BACKPACK_RECIPE_SERIALIZER.get();
	}

	/**
	 * Wraps the vanilla serializer so this recipe still loads and syncs.
	 */
	public static net.minecraft.world.item.crafting.RecipeSerializer<BasicBackpackRecipe> createSerializer() {
		return RecipeWrapperSerializer.create(BasicBackpackRecipe::new, ShapedRecipe.SERIALIZER);
	}
}
