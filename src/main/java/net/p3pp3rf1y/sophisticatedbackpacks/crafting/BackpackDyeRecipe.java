package net.p3pp3rf1y.sophisticatedbackpacks.crafting;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.crafting.StorageDyeRecipeBase;
import net.p3pp3rf1y.sophisticatedcore.util.ColorHelper;

import java.util.List;

public class BackpackDyeRecipe extends StorageDyeRecipeBase {
	/**
	 * 26.3 turned recipe serializers into a codec record, so the category travels in the JSON.
	 */
	public static final com.mojang.serialization.MapCodec<BackpackDyeRecipe> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(CustomRecipe::category)
			).apply(instance, BackpackDyeRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BackpackDyeRecipe> STREAM_CODEC = StreamCodec.composite(
			CraftingBookCategory.STREAM_CODEC, CustomRecipe::category,
			BackpackDyeRecipe::new);
	public BackpackDyeRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return (RecipeSerializer<? extends CustomRecipe>) (RecipeSerializer<?>) ModItems.BACKPACK_DYE_RECIPE_SERIALIZER.get();
	}

	@Override
	protected boolean isDyeableStorageItem(ItemStack stack) {
		return stack.getItem() instanceof BackpackItem;
	}

	@Override
	protected void applyColors(ItemStack coloredStorage, List<DyeColor> mainDyes, List<DyeColor> trimDyes) {
		IBackpackWrapper coloredWrapper = BackpackWrapper.fromStack(coloredStorage);
		coloredWrapper.setColors(ColorHelper.calculateColor(coloredWrapper.getMainColor(), BackpackWrapper.DEFAULT_MAIN_COLOR, mainDyes),
				ColorHelper.calculateColor(coloredWrapper.getAccentColor(), BackpackWrapper.DEFAULT_ACCENT_COLOR, trimDyes)
		);
	}
}
