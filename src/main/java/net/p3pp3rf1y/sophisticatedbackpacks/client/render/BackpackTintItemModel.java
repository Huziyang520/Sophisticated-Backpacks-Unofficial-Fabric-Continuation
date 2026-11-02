package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;

/**
 * Applies a backpack's own cloth and border colors to its item model.
 * <p>
 * 26.3 removed the item color registry; each model layer carries a resolved color list instead.
 * The delegate draws the shapes, then the first two layers are recolored from the stack.
 */
public class BackpackTintItemModel implements ItemModel {
	private static final int CLOTH_LAYER = 0;
	private static final int BORDER_LAYER = 1;

	private final ItemModel delegate;

	public BackpackTintItemModel(ItemModel delegate) {
		this.delegate = delegate;
	}

	@Override
	public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext context,
			ClientLevel level, ItemOwner owner, int seed) {
		delegate.update(state, stack, resolver, context, level, owner, seed);
		if (state.isEmpty()) {
			return;
		}

		IBackpackWrapper wrapper = BackpackWrapper.fromStack(stack);
		applyTint(state, CLOTH_LAYER, wrapper.getMainColor());
		applyTint(state, BORDER_LAYER, wrapper.getAccentColor());
	}

	private static void applyTint(ItemStackRenderState state, int layerIndex, int color) {
		ItemStackRenderState.LayerRenderState[] layers = state.layers;
		if (layers == null || layerIndex >= state.activeLayerCount) {
			return;
		}
		ItemStackRenderState.LayerRenderState layer = layers[layerIndex];
		if (layer == null || layer.tintLayers() == null || layer.tintLayers().size() <= layerIndex) {
			return;
		}
		layer.tintLayers().set(layerIndex, color);
	}
}