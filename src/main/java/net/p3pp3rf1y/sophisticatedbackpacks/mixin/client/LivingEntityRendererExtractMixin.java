package net.p3pp3rf1y.sophisticatedbackpacks.mixin.client;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackRenderStateData;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Resolves the backpack to draw during the extract pass.
 * <p>
 * 26.3 submit only sees the render state, so the backpack has to be looked up while the entity is
 * still available. Both riders and equipped backpacks are handled here so the layer itself stays
 * free of entity access.
 */
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererExtractMixin {
	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("RETURN"))
	private void sophisticatedBackpacks$extractBackpack(LivingEntity entity, LivingEntityRenderState state, float partialTick, CallbackInfo ci) {
		if (!(entity instanceof Player player)) {
			ItemStack chestStack = entity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
			BackpackRenderStateData.setBackpack(state, chestStack.getItem() instanceof BackpackItem ? chestStack : ItemStack.EMPTY, false);
			return;
		}

		PlayerInventoryProvider.RenderedBackpack rendered = PlayerInventoryProvider.get().getBackpackFromRendered(player).orElse(null);
		if (rendered == null) {
			BackpackRenderStateData.setBackpack(state, findInventoryBackpack(player), false);
			return;
		}
		BackpackRenderStateData.setBackpack(state, rendered.backpack(), rendered.isArmorSlot());
	}

	// Inventory slots are not registered as rendered handlers on this version, so scan them directly.
	private static ItemStack findInventoryBackpack(Player player) {
		for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
			if (stack.getItem() instanceof BackpackItem) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}
}
