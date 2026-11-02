package net.p3pp3rf1y.sophisticatedbackpacks.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.crafting.IWrapperRecipe;
import net.p3pp3rf1y.sophisticatedcore.crafting.RecipeWrapperSerializer;

import java.util.Optional;

public class BackpackUpgradeRecipe extends ShapedRecipe implements IWrapperRecipe<ShapedRecipe> {
	private final ShapedRecipe compose;

	public BackpackUpgradeRecipe(ShapedRecipe compose) {
		super(compose.commonInfo, compose.bookInfo, compose.pattern, compose.result);
		this.compose = compose;
	}

	@Override
	public ShapedRecipe getCompose() {
		return compose;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public ItemStack assemble(CraftingInput inv) {
		ItemStack upgradedBackpack = super.assemble(inv);
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
	public RecipeSerializer<ShapedRecipe> getSerializer() {
		return (RecipeSerializer<ShapedRecipe>) (RecipeSerializer<?>) ModItems.BACKPACK_UPGRADE_RECIPE_SERIALIZER.get();
	}

	/**
	 * Wraps the vanilla serializer so this recipe still loads and syncs.
	 */
	public static net.minecraft.world.item.crafting.RecipeSerializer<BackpackUpgradeRecipe> createSerializer() {
		return RecipeWrapperSerializer.create(BackpackUpgradeRecipe::new, ShapedRecipe.SERIALIZER);
	}
}
