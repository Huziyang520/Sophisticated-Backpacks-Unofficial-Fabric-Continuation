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
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.crafting.IWrapperRecipe;
import net.p3pp3rf1y.sophisticatedcore.crafting.RecipeWrapperSerializer;

import java.util.List;
import java.util.Optional;

/**
 * 升级背包的配方：结果背包沿用被升级背包的组件并刷新槽位数。
 * <p>
 * 26.3 的 ShapedRecipe 不再暴露 pattern / result 字段，改为委托（与核心仓 UpgradeNextTierRecipe 同一写法）。
 */
public class BackpackUpgradeRecipe implements CraftingRecipe, IWrapperRecipe<ShapedRecipe> {
	private final ShapedRecipe compose;

	public BackpackUpgradeRecipe(ShapedRecipe compose) {
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
		ItemStack upgradedBackpack = compose.assemble(inv);
		getBackpack(inv).map(ItemStack::getComponents).ifPresent(upgradedBackpack::applyComponents);
		IBackpackWrapper wrapper = BackpackWrapper.fromStack(upgradedBackpack);

		BackpackItem backpackItem = ((BackpackItem) upgradedBackpack.getItem());
		wrapper.setSlotNumbers(backpackItem.getNumberOfSlots(), backpackItem.getNumberOfUpgradeSlots());

		return upgradedBackpack;
	}

	private Optional<ItemStack> getBackpack(CraftingInput inv) {
		for (int slot = 0; slot < inv.size(); slot++) {
			ItemStack slotStack = inv.getItem(slot);
			if (slotStack.getItem() instanceof BackpackItem) {
				return Optional.of(slotStack);
			}
		}

		return Optional.empty();
	}

	@Override
	public boolean isSpecial() {
		return true;
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
		return ModItems.BACKPACK_UPGRADE_RECIPE_SERIALIZER.get();
	}

	public static class Serializer {
		private Serializer() {
		}

		public static RecipeSerializer<BackpackUpgradeRecipe> create() {
			return RecipeWrapperSerializer.create(BackpackUpgradeRecipe::new, ShapedRecipe.SERIALIZER);
		}
	}
}
