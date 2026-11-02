package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedcore.api.IUpgradeRenderer;
import net.p3pp3rf1y.sophisticatedcore.client.render.UpgradeRenderRegistry;
import net.p3pp3rf1y.sophisticatedcore.renderdata.IUpgradeRenderData;
import net.p3pp3rf1y.sophisticatedcore.renderdata.RenderInfo;
import net.p3pp3rf1y.sophisticatedcore.renderdata.UpgradeRenderDataType;
import org.joml.Vector3f;

/**
 * Draws a backpack on a living entity's back.
 * <p>
 * 26.3 renders in two passes: extract fills a {@link LivingEntityRenderState}, submit draws from it.
 * Submit never sees the live entity, so the backpack stack is read during extract (see
 * {@code LivingEntityRendererExtractMixin}) and left on the state.
 */
public class BackpackLayerRenderer<S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
	public BackpackLayerRenderer(RenderLayerParent<S, M> entityRendererIn) {
		super(entityRendererIn);
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, S state, float limbSwing, float limbSwingAmount) {
		ItemStack backpack = BackpackRenderStateData.getBackpack(state);
		if (backpack.isEmpty()) {
			return;
		}

		// Layers are baked with the entity models, which is after renderers are created.
		BackpackModelManager.initModels();
		IBackpackModel model = BackpackModelManager.getBackpackModel(backpack.getItem());
		if (model == null) {
			return;
		}

		poseStack.pushPose();
		EquipmentSlot equipmentSlot = model.getRenderEquipmentSlot();
		boolean wearsArmor = (equipmentSlot != EquipmentSlot.CHEST || !BackpackRenderStateData.isArmorSlot(state))
				&& hasArmorInSlot(state, equipmentSlot);
		renderBackpack(getParentModel(), state, poseStack, collector, packedLight, backpack, wearsArmor, model);
		poseStack.popPose();
	}

	/**
	 * 26.3 dropped {@code Inventory.armor}, so the worn armor comes from the humanoid render state instead.
	 */
	private static boolean hasArmorInSlot(LivingEntityRenderState state, EquipmentSlot slot) {
		if (!(state instanceof HumanoidRenderState humanoid)) {
			return false;
		}
		ItemStack worn = switch (slot) {
			case HEAD -> humanoid.headEquipment;
			case CHEST -> humanoid.chestEquipment;
			case LEGS -> humanoid.legsEquipment;
			case FEET -> humanoid.feetEquipment;
			default -> ItemStack.EMPTY;
		};
		return !worn.isEmpty();
	}

	public static <M extends EntityModel<?>> void renderBackpack(M parentModel, LivingEntityRenderState state, PoseStack matrixStack,
			SubmitNodeCollector collector, int packedLight, ItemStack backpack, boolean wearsArmor, IBackpackModel model) {
		model.translateRotateAndScale(parentModel, state, matrixStack, wearsArmor);

		IBackpackWrapper wrapper = BackpackWrapper.fromStack(backpack);
		int clothColor = wrapper.getMainColor();
		int borderColor = wrapper.getAccentColor();
		model.render(parentModel, state, matrixStack, collector, packedLight, clothColor, borderColor, backpack.getItem(), wrapper.getRenderInfo());
		renderUpgrades(state, wrapper.getRenderInfo());
		renderItemShown(matrixStack, collector, packedLight, wrapper.getRenderInfo());
	}

	private static void renderItemShown(PoseStack matrixStack, SubmitNodeCollector collector, int packedLight, RenderInfo renderInfo) {
		renderInfo.getItemDisplayRenderInfo().getDisplayItem().ifPresent(displayItem -> {
			matrixStack.pushPose();
			matrixStack.translate(0, 0.9, -0.25);
			matrixStack.scale(0.5f, 0.5f, 0.5f);
			matrixStack.rotateDegrees(Axis.ZP, 180f + displayItem.getRotation());

			Minecraft minecraft = Minecraft.getInstance();
			ItemModelResolver resolver = minecraft.getItemModelResolver();
			ItemStackRenderState renderState = new ItemStackRenderState();
			resolver.updateForTopItem(renderState, displayItem.getItem(), ItemDisplayContext.FIXED, minecraft.level, null, 0);
			renderState.submit(matrixStack, collector, packedLight, OverlayTexture.NO_OVERLAY, 0);
			matrixStack.popPose();
		});
	}

	private static void renderUpgrades(LivingEntityRenderState state, RenderInfo renderInfo) {
		Level level = Minecraft.getInstance().level;
		if (level == null || Minecraft.getInstance().isPaused() || level.getRandom().nextInt(32) != 0) {
			return;
		}
		renderInfo.getUpgradeRenderData().forEach((type, data) -> UpgradeRenderRegistry.getUpgradeRenderer(type).ifPresent(renderer -> renderUpgrade(renderer, state, level, type, data)));
	}

	private static Vector3f getBackpackMiddleFacePoint(LivingEntityRenderState state, Vector3f vector) {
		Vector3f point = new Vector3f(vector);
		boolean crouching = state.hasPose(Pose.CROUCHING);
		point.rotate(Axis.XP.rotationDegrees(crouching ? 25 : 0));
		point.add(0, 0.8f, crouching ? 0.9f : 0.7f);
		point.rotate(Axis.YN.rotationDegrees(state.bodyRot - 180));
		point.add((float) state.x, (float) state.y, (float) state.z);
		return point;
	}

	private static <T extends IUpgradeRenderData> void renderUpgrade(IUpgradeRenderer<T> renderer, LivingEntityRenderState state, Level level,
			UpgradeRenderDataType<?> type, IUpgradeRenderData data) {
		//noinspection unchecked
		type.cast(data).ifPresent(renderData -> renderer.render(level, level.getRandom(), vector3d -> getBackpackMiddleFacePoint(state, vector3d), (T) renderData));
	}
}
