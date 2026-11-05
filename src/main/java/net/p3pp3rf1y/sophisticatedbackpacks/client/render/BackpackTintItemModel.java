package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedcore.renderdata.RenderInfo;
import net.p3pp3rf1y.sophisticatedcore.renderdata.TankPosition;

import java.util.Map;

/**
 * Applies a backpack's own cloth and border colors to its item model.
 * <p>
 * 26.3 removed the item color registry; a baked model carries one color per tint index instead.
 * The delegate draws every quads batch into a single layer, so the colors are written into that layer.
 * <p>
 * The variant models are baked from the block-side models so the icon reflects installed tanks
 * and battery upgrades instead of always showing the pouch/pouch/none shape.
 */
public class BackpackTintItemModel implements ItemModel {
	private static final int CLOTH_TINT_INDEX = 0;
	private static final int BORDER_TINT_INDEX = 1;

	private final ItemModel fallback;
	private final Map<String, ItemModel> variants;

	public BackpackTintItemModel(ItemModel fallback, Map<String, ItemModel> variants) {
		this.fallback = fallback;
		this.variants = variants;
	}

	@Override
	public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext context,
			ClientLevel level, ItemOwner owner, int seed) {
		IBackpackWrapper wrapper = BackpackWrapper.fromStack(stack);
		ItemModel selected = variants.getOrDefault(getVariantKey(wrapper.getRenderInfo()), fallback);
		selected.update(state, stack, resolver, context, level, owner, seed);
		if (state.isEmpty() || state.layers == null || state.activeLayerCount < 1 || state.layers[0] == null) {
			return;
		}

		IntList tints = state.layers[0].tintLayers();
		tints.clear();
		setTint(tints, CLOTH_TINT_INDEX, wrapper.getMainColor());
		setTint(tints, BORDER_TINT_INDEX, wrapper.getAccentColor());
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
