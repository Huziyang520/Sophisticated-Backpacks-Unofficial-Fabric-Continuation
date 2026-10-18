package net.p3pp3rf1y.sophisticatedbackpacks.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.crafting.IWrapperRecipe;
import net.p3pp3rf1y.sophisticatedcore.crafting.RecipeWrapperSerializer;

import java.util.List;

/**
 * 背包基础配方：合成时清掉复制过来的内容 UUID。
 * <p>
 * 26.3 的 ShapedRecipe 把构造信息收进 CommonInfo / CraftingBookInfo 且不再暴露字段，
 * 所以这里改用委托而不是继承（与核心仓 UpgradeNextTierRecipe 同一写法）。
 */
public class BasicBackpackRecipe implements CraftingRecipe, IWrapperRecipe<ShapedRecipe> {
	private final ShapedRecipe compose;

	public BasicBackpackRecipe(ShapedRecipe compose) {
		this.compose = compose;
	}

	@Override
	public ShapedRecipe getCompose() {
		return compose;
	}

	@Override
	public boolean matches(CraftingInput inv, Level level) {
		return compose.matches(inv, level);
	}

	@Override
	public ItemStack assemble(CraftingInput inv) {
		ItemStack result = compose.assemble(inv);
		removeUuid(result);
		return result;
	}

	private void removeUuid(ItemStack backpack) {
		BackpackWrapper.fromStack(backpack).removeContentsUuid();
	}

	/**
	 * Not special on purpose: 26.3 的 JEI 只接管 {@code !Recipe#isSpecial()} 的合成配方
	 * （{@code CraftingCategoryExtension#isHandled}），而 {@code RecipeManager} 也只把非 special 的
	 * 配方放进 learnableRecipes 同步给客户端 —— 标成 special 会让配方在 JEI 与配方书里同时消失。
	 */
	@Override
	public boolean isSpecial() {
		return false;
	}

	@Override
	public String group() {
		return compose.group();
	}

	@Override
	public boolean showNotification() {
		return compose.showNotification();
	}

	@Override
	public CraftingBookCategory category() {
		return compose.category();
	}

	@Override
	public RecipeBookCategory recipeBookCategory() {
		return compose.recipeBookCategory();
	}

	@Override
	public PlacementInfo placementInfo() {
		return compose.placementInfo();
	}

	@Override
	public List<RecipeDisplay> display() {
		return compose.display();
	}

	@Override
	public RecipeType<CraftingRecipe> getType() {
		return compose.getType();
	}

	@Override
	public RecipeSerializer<? extends CraftingRecipe> getSerializer() {
		return ModItems.BASIC_BACKPACK_RECIPE_SERIALIZER.get();
	}

	public static class Serializer {
		private Serializer() {
		}

		public static RecipeSerializer<BasicBackpackRecipe> create() {
			return RecipeWrapperSerializer.create(BasicBackpackRecipe::new, ShapedRecipe.SERIALIZER);
		}
	}
}
