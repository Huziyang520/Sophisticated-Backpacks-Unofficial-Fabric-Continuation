package net.p3pp3rf1y.sophisticatedbackpacks.client.init;

import com.mojang.math.Transformation;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedExtraModel;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.Identifier;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackTintItemModel;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Wraps backpack item models so they can be tinted with per-stack colors.
 * <p>
 * 26.3 resolves item colors while baking the model, leaving no per-frame color callback, so the
 * baked model is wrapped and the tint layers are rewritten from the stack during update.
 */
public class BackpackModelModifier {
	private static final List<String> OVERLAY_PREFIXES = List.of(
			"backpack_fluid_left_", "backpack_fluid_right_", "backpack_charge_");
	private static final List<Identifier> OVERLAY_MODEL_IDS = buildOverlayModelIds();
	private static final ExtraModelKey<List<ResolvedModel>> OVERLAY_MODELS_KEY = ExtraModelKey.create();

	private static Set<Identifier> backpackItemIds;

	private BackpackModelModifier() {
	}

	public static void init() {
		ModelLoadingPlugin.register(context -> {
			context.addModel(OVERLAY_MODELS_KEY, new OverlayModelDependencies());
			context.modifyItemModelAfterBake().register((model, ctx) -> {
				if (isBackpackItem(ctx.itemId())) {
					ItemModel.BakingContext bakeContext = ctx.bakingContext();
					org.joml.Matrix4fc transformation = ctx.transformation();
					return new BackpackTintItemModel(model, bakeVariants(ctx.itemId(), bakeContext, transformation),
							bakeOverlayModels(0, bakeContext, transformation),
							bakeOverlayModels(1, bakeContext, transformation),
							bakeOverlayModels(2, bakeContext, transformation));
				}
				return model;
			});
		});
	}

	private static ItemModel[] bakeOverlayModels(int group, ItemModel.BakingContext bakeContext, org.joml.Matrix4fc transformation) {
		ItemModel[] models = new ItemModel[4];
		for (int level = 0; level < 4; level++) {
			Identifier modelId = OVERLAY_MODEL_IDS.get(group * 4 + level);
			models[level] = new CuboidItemModelWrapper.Unbaked(modelId, Optional.<Transformation>empty(), List.<ItemTintSource>of())
					.bake(bakeContext, transformation);
		}
		return models;
	}

	private static List<Identifier> buildOverlayModelIds() {
		List<Identifier> ids = new ArrayList<>(OVERLAY_PREFIXES.size() * 4);
		for (String prefix : OVERLAY_PREFIXES) {
			for (int level = 1; level <= 4; level++) {
				ids.add(Identifier.fromNamespaceAndPath(SophisticatedBackpacks.MOD_ID, "block/" + prefix + level));
			}
		}
		return List.copyOf(ids);
	}

	// The item bake pass owns the item baking context and resolves these ids itself; registering the
	// ids as dependencies here is what puts the orphan overlay models into 26.3 model discovery.
	private static final class OverlayModelDependencies implements UnbakedExtraModel<List<ResolvedModel>> {
		@Override
		public void resolveDependencies(ResolvableModel.Resolver resolver) {
			for (Identifier id : OVERLAY_MODEL_IDS) {
				resolver.markDependency(id);
			}
		}

		@Override
		public List<ResolvedModel> bake(ModelBaker baker) {
			List<ResolvedModel> resolved = new ArrayList<>(OVERLAY_MODEL_IDS.size());
			for (Identifier id : OVERLAY_MODEL_IDS) {
				ResolvedModel model = baker.getModel(id);
				if (model != null) {
					resolved.add(model);
				}
			}
			return List.copyOf(resolved);
		}
	}

	private static Map<String, ItemModel> bakeVariants(Identifier itemId, ItemModel.BakingContext bakeContext, org.joml.Matrix4fc transformation) {
		Map<String, ItemModel> variants = new HashMap<>();
		String tier = itemId.getPath();
		for (String left : List.of("pouch", "tank")) {
			for (String right : List.of("pouch", "tank")) {
				for (String battery : List.of("none", "battery")) {
					String key = left + "_" + right + "_" + battery;
					Identifier modelId = Identifier.fromNamespaceAndPath(SophisticatedBackpacks.MOD_ID, "block/" + tier + "_" + key);
					variants.put(key, new CuboidItemModelWrapper.Unbaked(modelId, Optional.<Transformation>empty(), List.<ItemTintSource>of())
							.bake(bakeContext, transformation));
				}
			}
		}
		return variants;
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