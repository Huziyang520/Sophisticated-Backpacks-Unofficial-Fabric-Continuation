package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.client.render.RenderStateData;

/**
 * Carries the rendered backpack from the extract pass to the submit pass.
 * <p>
 * 26.3 renders entities in two passes and submit only receives the render state, so the backpack
 * stack is resolved during extract and stashed here.
 */
public final class BackpackRenderStateData {
	private static final RenderStateDataKey<ItemStack> BACKPACK = RenderStateData.createKey("sophisticatedbackpacks:rendered_backpack");
	private static final RenderStateDataKey<Boolean> ARMOR_SLOT = RenderStateData.createKey("sophisticatedbackpacks:backpack_in_armor_slot");

	private BackpackRenderStateData() {
	}

	/**
	 * Stashes the backpack to draw for this frame; {@link ItemStack#EMPTY} clears it.
	 */
	public static void setBackpack(LivingEntityRenderState state, ItemStack backpack, boolean isArmorSlot) {
		RenderStateData.set(state, BACKPACK, backpack);
		RenderStateData.set(state, ARMOR_SLOT, isArmorSlot);
	}

	public static ItemStack getBackpack(LivingEntityRenderState state) {
		return RenderStateData.getOrDefault(state, BACKPACK, ItemStack.EMPTY);
	}

	public static boolean isArmorSlot(LivingEntityRenderState state) {
		return Boolean.TRUE.equals(RenderStateData.get(state, ARMOR_SLOT));
	}
}
