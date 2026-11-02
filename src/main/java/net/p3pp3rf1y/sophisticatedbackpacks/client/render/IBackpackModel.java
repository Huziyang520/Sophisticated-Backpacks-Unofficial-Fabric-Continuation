package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.p3pp3rf1y.sophisticatedcore.platform.FluidStack;
import net.p3pp3rf1y.sophisticatedcore.renderdata.RenderInfo;

/**
 * Draws a backpack onto a living entity's body.
 * <p>
 * 26.3 removed {@code MultiBufferSource} from layer rendering in favour of
 * {@link SubmitNodeCollector}, and submit only receives a {@link LivingEntityRenderState} rather than
 * the live entity. The state carries what the pose animation needs (body rotation, crouching, age).
 */
public interface IBackpackModel {
	<M extends EntityModel<?>> void render(M parentModel, LivingEntityRenderState state, PoseStack poseStack,
			SubmitNodeCollector collector, int packedLight, int clothColor, int borderColor, Item backpackItem, RenderInfo renderInfo);

	void renderBatteryCharge(PoseStack matrixStack, SubmitNodeCollector collector, int packedLight, float chargeRatio);

	void renderFluid(PoseStack matrixStack, SubmitNodeCollector collector, int packedLight, FluidStack fluid, float fill, boolean left);

	EquipmentSlot getRenderEquipmentSlot();

	<M extends EntityModel<?>> void translateRotateAndScale(M parentModel, LivingEntityRenderState state, PoseStack matrixStack, boolean wearsArmor);
}
