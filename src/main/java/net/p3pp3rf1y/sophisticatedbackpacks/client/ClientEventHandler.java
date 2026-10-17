package net.p3pp3rf1y.sophisticatedbackpacks.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.ClientBackpackContentsTooltip;
import net.p3pp3rf1y.sophisticatedbackpacks.network.RequestPlayerSettingsPayload;
import net.p3pp3rf1y.sophisticatedcore.event.client.ClientLifecycleEvents;
import net.p3pp3rf1y.sophisticatedcore.network.PacketDistributor;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.EVERLASTING_BACKPACK_ITEM_ENTITY;

/**
 * 客户端事件注册。
 * <p>
 * 26.3 删除了整套渲染模型 / 图层 API（{@code RenderType}、{@code BuiltinItemRendererRegistry}、
 * {@code EntityModelLayerRegistry}、{@code BlockRenderLayerMap} 等），对应的背包渲染实现
 * （动态模型、方块实体渲染、玩家背部图层、物品内渲染）已整体移到 {@code src/parked/}，
 * 所以这里只保留仍然可用的注册。
 */
public class ClientEventHandler {
	private ClientEventHandler() {
	}

	public static void registerHandlers() {
		registerEntityRenderers();
		// 26.3 移除了 Fabric 的 ColorProviderRegistry，物品/方块染色改由模型 tint source 数据驱动，暂未移植

		ClientLifecycleEvents.CLIENT_LEVEL_LOAD.register(ClientBackpackContentsTooltip::onWorldLoad);
		ClientPlayConnectionEvents.JOIN.register(ClientEventHandler::onPlayerLoggingIn);
		ClientLifecycleEvents.CLIENT_LEVEL_LOAD.register(BackpackStorage::onClientWorldLoad);
	}

	private static void onPlayerLoggingIn(ClientPacketListener clientPacketListener, PacketSender packetSender, Minecraft minecraft) {
		PacketDistributor.sendToServer(new RequestPlayerSettingsPayload());
	}

	private static void registerEntityRenderers() {
		EntityRendererRegistry.register(EVERLASTING_BACKPACK_ITEM_ENTITY.get(), ItemEntityRenderer::new);
	}
}
