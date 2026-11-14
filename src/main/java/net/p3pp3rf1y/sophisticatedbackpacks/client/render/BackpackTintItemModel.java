package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedcore.platform.FluidStack;
import net.p3pp3rf1y.sophisticatedcore.renderdata.RenderInfo;
import net.p3pp3rf1y.sophisticatedcore.renderdata.TankPosition;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IRenderedBatteryUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IRenderedTankUpgrade;

import java.util.Map;
import java.util.Optional;

/**
 * Applies a backpack's cloth and border colors and layers the tank fluid and battery charge on top.
 * <p>
 * 26.3 has no item color registry, so every baked model gets its colors from tint layers during
 * update. Fluid and charge use separate baked block models, one per quarter level, drawn as extra
 * layers so the icon follows the backpack's contents without rebaking.
 */
public class BackpackTintItemModel implements ItemModel {
	private static final int CLOTH_TINT_INDEX = 0;
	private static final int BORDER_TINT_INDEX = 1;
	private static final int OVERLAY_TINT_INDEX = 0;
	private static final int CHARGE_COLOR = 0xFF3CE03C;

	private final ItemModel fallback;
	private final Map<String, ItemModel> variants;
	private final ItemModel[] leftFluidModels;
	private final ItemModel[] rightFluidModels;
	private final ItemModel[] chargeModels;

	public BackpackTintItemModel(ItemModel fallback, Map<String, ItemModel> variants, ItemModel[] leftFluidModels,
			ItemModel[] rightFluidModels, ItemModel[] chargeModels) {
		this.fallback = fallback;
		this.variants = variants;
		this.leftFluidModels = leftFluidModels;
		this.rightFluidModels = rightFluidModels;
		this.chargeModels = chargeModels;
	}

	@Override
	public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext context,
			ClientLevel level, ItemOwner owner, int seed) {
		IBackpackWrapper wrapper = BackpackWrapper.fromStack(stack);
		RenderInfo renderInfo = wrapper.getRenderInfo();
		ItemModel selected = variants.getOrDefault(getVariantKey(renderInfo), fallback);
		int baseLayer = state.activeLayerCount;
		selected.update(state, stack, resolver, context, level, owner, seed);
		if (state.isEmpty() || state.layers == null || state.activeLayerCount <= baseLayer || state.layers[baseLayer] == null) {
			return;
		}

		IntList baseTints = state.layers[baseLayer].tintLayers();
		baseTints.clear();
		setTint(baseTints, CLOTH_TINT_INDEX, wrapper.getMainColor());
		setTint(baseTints, BORDER_TINT_INDEX, wrapper.getAccentColor());
		addFluidLayer(state, stack, resolver, context, level, owner, seed, renderInfo, TankPosition.LEFT, leftFluidModels);
		addFluidLayer(state, stack, resolver, context, level, owner, seed, renderInfo, TankPosition.RIGHT, rightFluidModels);
		addChargeLayer(state, stack, resolver, context, level, owner, seed, renderInfo, chargeModels);
	}

	private static void addFluidLayer(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver,
			ItemDisplayContext context, ClientLevel level, ItemOwner owner, int seed, RenderInfo renderInfo,
			TankPosition position, ItemModel[] models) {
		IRenderedTankUpgrade.TankRenderInfo tankInfo = renderInfo.getTankRenderInfos().get(position);
		if (tankInfo == null) {
			return;
		}
		Optional<FluidStack> fluid = tankInfo.getFluid();
		if (fluid.isEmpty() || tankInfo.getFillRatio() <= 0) {
			return;
		}
		int modelIndex = getLevelIndex(tankInfo.getFillRatio());
		if (modelIndex < 0 || modelIndex >= models.length) {
			return;
		}
		int layer = state.activeLayerCount;
		models[modelIndex].update(state, stack, resolver, context, level, owner, seed);
		if (state.activeLayerCount > layer && state.layers[layer] != null) {
			int color = FluidVariantRendering.getColor(fluid.get().getVariant());
			setTint(state.layers[layer].tintLayers(), OVERLAY_TINT_INDEX, color | 0xFF000000);
		}
	}

	private static void addChargeLayer(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver,
			ItemDisplayContext context, ClientLevel level, ItemOwner owner, int seed, RenderInfo renderInfo, ItemModel[] models) {
		Optional<IRenderedBatteryUpgrade.BatteryRenderInfo> battery = renderInfo.getBatteryRenderInfo();
		if (battery.isEmpty() || battery.get().getChargeRatio() <= 0) {
			return;
		}
		int modelIndex = getLevelIndex(battery.get().getChargeRatio());
		if (modelIndex < 0 || modelIndex >= models.length) {
			return;
		}
		int layer = state.activeLayerCount;
		models[modelIndex].update(state, stack, resolver, context, level, owner, seed);
		if (state.activeLayerCount > layer && state.layers[layer] != null) {
			setTint(state.layers[layer].tintLayers(), OVERLAY_TINT_INDEX, CHARGE_COLOR);
		}
	}

	private static int getLevelIndex(float ratio) {
		return Mth.clamp(Mth.ceil(ratio * 4), 1, 4) - 1;
	}

	private static String getVariantKey(RenderInfo renderInfo) {
		boolean leftTank = renderInfo.getTankRenderInfos().containsKey(TankPosition.LEFT);
		boolean rightTank = renderInfo.getTankRenderInfos().containsKey(TankPosition.RIGHT);
		boolean battery = renderInfo.getBatteryRenderInfo().isPresent();
		return (leftTank ? "tank" : "pouch") + "_" + (rightTank ? "tank" : "pouch") + "_" + (battery ? "battery" : "none");
	}

	private static void setTint(IntList tints, int tintIndex, int color) {
		while (tints.size() <= tintIndex) {
			tints.add(-1);
		}
		tints.set(tintIndex, color);
	}
}
