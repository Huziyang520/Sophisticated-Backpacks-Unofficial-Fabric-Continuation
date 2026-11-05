package net.p3pp3rf1y.sophisticatedbackpacks.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.*;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BlockPickPayload;
import net.p3pp3rf1y.sophisticatedbackpacks.network.RequestPlayerSettingsPayload;
import net.p3pp3rf1y.sophisticatedcore.event.client.ClientLifecycleEvents;
import net.p3pp3rf1y.sophisticatedcore.network.PacketDistributor;

import java.util.Map;
import java.util.function.Supplier;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.*;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.EVERLASTING_BACKPACK_ITEM_ENTITY;

public class ClientEventHandler {
	private ClientEventHandler() {
	}

	private static final String BACKPACK_REG_NAME = "backpack";
	public static final ModelLayerLocation BACKPACK_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(SophisticatedBackpacks.MOD_ID, BACKPACK_REG_NAME), "main");
	private static boolean backpackLayerRegistered = false;

	public static void registerHandlers() {
		registerLayer();
		registerEntityRenderers();
		registerReloadListener();
		registerModelLayers();
		registerBackpackClientExtension();
		net.p3pp3rf1y.sophisticatedbackpacks.client.init.ModBlockColors.registerBlockColorHandlers();
		net.p3pp3rf1y.sophisticatedbackpacks.client.init.BackpackModelModifier.init();

		ClientLifecycleEvents.CLIENT_LEVEL_LOAD.register(ClientBackpackContentsTooltip::onWorldLoad);
		ClientPlayConnectionEvents.JOIN.register(ClientEventHandler::onPlayerLoggingIn);
		ClientLifecycleEvents.CLIENT_LEVEL_LOAD.register(BackpackStorage::onClientWorldLoad);
	}

	private static void onPlayerLoggingIn(ClientPacketListener clientPacketListener, PacketSender packetSender, Minecraft minecraft) {
		PacketDistributor.sendToServer(new RequestPlayerSettingsPayload());
	}


	public static void registerReloadListener() {
		registerBackpackLayer(); //event.registerReloadListener((ResourceManagerReloadListener) resourceManager -> registerBackpackLayer());
	}

	private static void registerEntityRenderers() {
		EntityRendererRegistry.register(EVERLASTING_BACKPACK_ITEM_ENTITY.get(), ItemEntityRenderer::new);
		// 26.3 has no public BlockEntityRenderers.register; Fabric exposes the registry instead.
		net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry
				.register(BACKPACK_TILE_TYPE.get(), context -> new BackpackBlockEntityRenderer());
	}

	/**
	 * Publishes the backpack layer so it can be baked once the layer set is built.
	 */
	private static void registerModelLayers() {
		net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry
				.registerModelLayer(BACKPACK_LAYER, BackpackModel::createBodyLayer);
	}

	public static void registerLayer() {
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void registerBackpackLayer() {
		if (backpackLayerRegistered) {
			return;
		}
		backpackLayerRegistered = true;
		LivingEntityRenderLayerRegistrationCallback.EVENT.register((entityType, livingEntityRenderer, registrationHelper, context) ->
				registrationHelper.register(new BackpackLayerRenderer(livingEntityRenderer)));
	}

	public static ItemStack handleBlockPick(Player player, HitResult target, ItemStack stack) {
		if (player.isCreative() || target.getType() != HitResult.Type.BLOCK) {
			return stack;
		}
		Level level = player.level();
		BlockPos pos = ((BlockHitResult) target).getBlockPos();
		BlockState state = level.getBlockState(pos);

		if (state.isAir()) {
			return stack;
		}

		ItemStack result = state.getBlock().asItem().getDefaultInstance();

		if (result.isEmpty() || player.getInventory().findSlotMatchingItem(result) > -1) {
			return stack;
		}

		PacketDistributor.sendToServer(new BlockPickPayload(result));
		return stack;
	}

	private static void registerBackpackClientExtension() {
	}
}
