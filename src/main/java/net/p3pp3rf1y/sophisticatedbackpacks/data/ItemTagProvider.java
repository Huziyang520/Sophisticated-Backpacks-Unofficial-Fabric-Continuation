package net.p3pp3rf1y.sophisticatedbackpacks.data;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeItemBase;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends FabricTagProvider.ItemTagProvider {
	public ItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
		super(output, completableFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider pProvider) {
		FabricTagBuilder upgradeTag = getOrCreateTagBuilder(ModItems.BACKPACK_UPGRADE_TAG);
		BuiltInRegistries.ITEM.entrySet().stream()
				.filter(entry -> entry.getKey().location().getNamespace().equals(SophisticatedBackpacks.MOD_ID) && entry.getValue() instanceof UpgradeItemBase)
				.map(Map.Entry::getValue).forEach(item -> {
					Identifier location = BuiltInRegistries.ITEM.getKey(item);
					if (location.getPath().contains("/")) {
						upgradeTag.addOptional(location);
					} else {
						upgradeTag.add(item);
					}
				});
	}
}
