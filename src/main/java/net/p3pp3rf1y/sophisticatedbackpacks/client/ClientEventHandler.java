package net.p3pp3rf1y.sophisticatedbackpacks.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackBlockTints;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackItemTints;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.ClientBackpackContentsTooltip;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BlockPickPayload;
import net.p3pp3rf1y.sophisticatedbackpacks.network.RequestPlayerSettingsPayload;
import net.p3pp3rf1y.sophisticatedcore.event.client.ClientLifecycleEvents;
import net.p3pp3rf1y.sophisticatedcore.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

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
		// 26.3 移除了 Fabric 的 ColorProviderRegistry，物品染色改由数据驱动的 ItemTintSource 完成
		BackpackItemTints.register();
		BackpackBlockTints.register();

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

	// 26.3 的 Fabric API 已删 ClientPickBlockApplyCallback，改由 MinecraftMixin 在拾取路径前调用
	public static void handleBlockPick(@Nullable Player player, @Nullable HitResult target) {
		if (player == null || target == null || player.isCreative() || target.getType() != HitResult.Type.BLOCK) {
			return;
		}

		Level level = player.level();
		BlockPos pos = ((BlockHitResult) target).getBlockPos();
		BlockState state = level.getBlockState(pos);
		if (state.isAir()) {
			return;
		}

		ItemStack result = state.getCloneItemStack(level, pos, false);
		if (result.isEmpty() || player.getInventory().findSlotMatchingItem(result) > -1) {
			return;
		}

		PacketDistributor.sendToServer(new BlockPickPayload(result));
	}
}
