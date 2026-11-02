package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlockEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedcore.platform.FluidStack;
import net.p3pp3rf1y.sophisticatedcore.renderdata.RenderInfo;
import net.p3pp3rf1y.sophisticatedcore.renderdata.TankPosition;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IRenderedBatteryUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IRenderedTankUpgrade;

import javax.annotation.Nullable;

/**
 * Draws the block form of a backpack.
 * <p>
 * 26.3 renders block entities in two passes: extract fills a render state, submit draws from it.
 * Everything that needs the live block entity (block state flags, tank fluid, battery charge) is
 * captured during extract so submit stays a pure drawing step.
 */
public class BackpackBlockEntityRenderer implements BlockEntityRenderer<BackpackBlockEntity, BackpackBlockEntityRenderer.State> {
	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(BackpackBlockEntity blockEntity, State state, float partialTick, Vec3 cameraPos,
			ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);

		BlockState blockState = blockEntity.getBlockState();
		state.facing = blockState.getValue(BackpackBlock.FACING);
		state.showLeftTank = blockState.getValue(BackpackBlock.LEFT_TANK);
		state.showRightTank = blockState.getValue(BackpackBlock.RIGHT_TANK);
		state.showBattery = blockState.getValue(BackpackBlock.BATTERY);

		IBackpackWrapper wrapper = blockEntity.getBackpackWrapper();
		RenderInfo renderInfo = wrapper.getRenderInfo();
		// Idempotent: also covers worlds where no player layer has baked the model yet.
		BackpackModelManager.initModels();
		state.model = BackpackModelManager.getBackpackModel(wrapper.getBackpack().getItem());

		state.leftFluid = getFluid(renderInfo, TankPosition.LEFT);
		state.leftFill = getFill(renderInfo, TankPosition.LEFT);
		state.rightFluid = getFluid(renderInfo, TankPosition.RIGHT);
		state.rightFill = getFill(renderInfo, TankPosition.RIGHT);

		state.chargeRatio = renderInfo.getBatteryRenderInfo().map(IRenderedBatteryUpgrade.BatteryRenderInfo::getChargeRatio).orElse(0F);

		ItemModelResolver resolver = Minecraft.getInstance().getItemModelResolver();
		state.displayItem = renderInfo.getItemDisplayRenderInfo().getDisplayItem().orElse(null);
		state.displayRotation = state.displayItem == null ? 0F : state.displayItem.getRotation();
		// Items are baked into the render state here because the item model resolver needs a level.
		ItemStack itemStack = state.displayItem == null ? ItemStack.EMPTY : state.displayItem.getItem();
		resolver.updateForTopItem(state.displayItemState, itemStack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);
	}

	@Nullable
	private static FluidStack getFluid(RenderInfo renderInfo, TankPosition position) {
		IRenderedTankUpgrade.TankRenderInfo tankInfo = renderInfo.getTankRenderInfos().get(position);
		return tankInfo == null ? null : tankInfo.getFluid().orElse(null);
	}

	private static float getFill(RenderInfo renderInfo, TankPosition position) {
		IRenderedTankUpgrade.TankRenderInfo tankInfo = renderInfo.getTankRenderInfos().get(position);
		return tankInfo == null ? 0F : tankInfo.getFillRatio();
	}

	@Override
	public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
		if (state.model == null) {
			return;
		}

		poseStack.pushPose();
		poseStack.translate(0.5, 0, 0.5);
		poseStack.rotateDegrees(Axis.YN, state.facing.toYRot());
		poseStack.pushPose();
		poseStack.scale(6 / 10f, 6 / 10f, 6 / 10f);
		poseStack.rotateDegrees(Axis.ZP, 180);
		poseStack.translate(0, -2.5, 0);

		if (state.showLeftTank && state.leftFluid != null) {
			poseStack.pushPose();
			poseStack.translate(1.45, 0, 0);
			state.model.renderFluid(poseStack, collector, state.lightCoords, state.leftFluid, state.leftFill, true);
			poseStack.popPose();
		}
		if (state.showRightTank && state.rightFluid != null) {
			poseStack.pushPose();
			poseStack.translate(-1.45, 0, 0);
			state.model.renderFluid(poseStack, collector, state.lightCoords, state.rightFluid, state.rightFill, false);
			poseStack.popPose();
		}
		poseStack.popPose();

		if (state.showBattery && state.chargeRatio > 0.1F) {
			poseStack.pushPose();
			poseStack.rotateDegrees(Axis.XN, 180);
			poseStack.translate(0, -1.5, 0);
			state.model.renderBatteryCharge(poseStack, collector, state.lightCoords, state.chargeRatio);
			poseStack.popPose();
		}

		renderItemDisplay(state, poseStack, collector);
		poseStack.popPose();
	}

	private void renderItemDisplay(State state, PoseStack poseStack, SubmitNodeCollector collector) {
		if (state.displayItem == null) {
			return;
		}
		poseStack.pushPose();
		poseStack.translate(0, 0.6, 0.25);
		poseStack.scale(0.5f, 0.5f, 0.5f);
		poseStack.rotateDegrees(Axis.XN, 180);
		poseStack.rotateDegrees(Axis.ZP, 180f + state.displayRotation);
		state.displayItemState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
		poseStack.popPose();
	}

	/**
	 * Per-frame data captured during extract.
	 */
	public static class State extends BlockEntityRenderState {
		public Direction facing = Direction.NORTH;
		public boolean showLeftTank;
		public boolean showRightTank;
		public boolean showBattery;
		@Nullable
		public IBackpackModel model;
		@Nullable
		public FluidStack leftFluid;
		public float leftFill;
		@Nullable
		public FluidStack rightFluid;
		public float rightFill;
		public float chargeRatio;
		@Nullable
		public RenderInfo.DisplayItem displayItem;
		public float displayRotation;
		public final ItemStackRenderState displayItemState = new ItemStackRenderState();
	}
}
