package net.p3pp3rf1y.sophisticatedbackpacks.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.crafting.IWrapperRecipe;
import net.p3pp3rf1y.sophisticatedcore.crafting.RecipeWrapperSerializer;

import java.util.List;
import java.util.Optional;

/**
 * 锻造台升级背包：结果背包沿用被升级背包的组件并刷新槽位数。
 * <p>
 * 26.3 的 SmithingTransformRecipe 不再暴露 template / base / addition / result 字段，
 * 且 assemble 已不再接收 registries，因此这里改为委托而不是继承。
 */
public class SmithingBackpackUpgradeRecipe implements SmithingRecipe, IWrapperRecipe<SmithingTransformRecipe> {
	private final SmithingTransformRecipe compose;

	public SmithingBackpackUpgradeRecipe(SmithingTransformRecipe compose) {
		this.compose = compose;
	}

	@Override
	public SmithingTransformRecipe getCompose() {
		return compose;
	}

	@Override
	public boolean showNotification() {
		return compose.showNotification();
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public boolean matches(SmithingRecipeInput input, Level level) {
		return compose.matches(input, level);
	}

	@Override
	public ItemStack assemble(SmithingRecipeInput inv) {
		ItemStack upgradedBackpack = compose.assemble(inv);
		if (SophisticatedCore.isLogicalServerThread()) {
			getBackpack(inv).map(ItemStack::getComponents).ifPresent(upgradedBackpack::applyComponents);
			IBackpackWrapper wrapper = BackpackWrapper.fromStack(upgradedBackpack);
			BackpackItem backpackItem = ((BackpackItem) upgradedBackpack.getItem());
			wrapper.setSlotNumbers(backpackItem.getNumberOfSlots(), backpackItem.getNumberOfUpgradeSlots());
		}
		return upgradedBackpack;
	}

	private Optional<ItemStack> getBackpack(SmithingRecipeInput inv) {
		ItemStack slotStack = inv.getItem(1);
		if (slotStack.getItem() instanceof BackpackItem) {
			return Optional.of(slotStack);
		}
		return Optional.empty();
	}

	@Override
	public Optional<Ingredient> templateIngredient() {
		return compose.templateIngredient();
	}

	@Override
	public Ingredient baseIngredient() {
		return compose.baseIngredient();
	}

	@Override
	public Optional<Ingredient> additionIngredient() {
		return compose.additionIngredient();
	}

	@Override
	public String group() {
		return compose.group();
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
	public RecipeType<SmithingRecipe> getType() {
		return compose.getType();
	}

	@Override
	public RecipeSerializer<? extends SmithingRecipe> getSerializer() {
		return ModItems.SMITHING_BACKPACK_UPGRADE_RECIPE_SERIALIZER.get();
	}

	public static class Serializer {
		private Serializer() {
		}

		public static RecipeSerializer<SmithingBackpackUpgradeRecipe> create() {
			return RecipeWrapperSerializer.create(SmithingBackpackUpgradeRecipe::new, SmithingTransformRecipe.SERIALIZER);
		}
	}
}
