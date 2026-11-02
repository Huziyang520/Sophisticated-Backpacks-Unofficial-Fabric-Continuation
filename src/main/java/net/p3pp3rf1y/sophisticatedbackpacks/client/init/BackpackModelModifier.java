package net.p3pp3rf1y.sophisticatedbackpacks.client.init;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.resources.Identifier;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackTintItemModel;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;

import java.util.HashSet;
import java.util.Set;

/**
 * Wraps backpack item models so they can be tinted with per-stack colors.
 * <p>
 * 26.3 resolves item colors while baking the model, leaving no per-frame color callback, so the
 * baked model is wrapped and the tint layers are rewritten from the stack during update.
 */
public class BackpackModelModifier {
	private static Set<Identifier> backpackItemIds;

	private BackpackModelModifier() {
	}

	public static void init() {
		ModelLoadingPlugin.register(context -> context.modifyItemModelAfterBake().register((model, ctx) -> {
			if (isBackpackItem(ctx.itemId())) {
				return new BackpackTintItemModel(model);
			}
			return model;
		}));
	}

	private static boolean isBackpackItem(Identifier itemId) {
		if (!SophisticatedBackpacks.MOD_ID.equals(itemId.getNamespace())) {
			return false;
		}
		if (backpackItemIds == null) {
			backpackItemIds = new HashSet<>();
			ModItems.BACKPACKS.forEach(backpack -> {
				if (backpack.get() instanceof BackpackItem) {
					backpackItemIds.add(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(backpack.get()));
				}
			});
		}
		return backpackItemIds.contains(itemId);
	}
}