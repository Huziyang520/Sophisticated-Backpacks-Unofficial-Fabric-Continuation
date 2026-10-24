package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.network.RequestBackpackInventoryContentsPayload;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.client.render.ClientStorageContentsTooltipBase;
import net.p3pp3rf1y.sophisticatedcore.network.PacketDistributor;

import java.util.UUID;

public class ClientBackpackContentsTooltip extends ClientStorageContentsTooltipBase {
	private final ItemStack backpack;

	public static void onWorldLoad(Minecraft mc, ClientLevel level) {
		refreshContents();
	}

	@Override
	protected IStorageWrapper getStorageWrapper() {
		return BackpackWrapper.fromStack(backpack);
	}

	public ClientBackpackContentsTooltip(BackpackItem.BackpackContentsTooltip tooltip) {
		backpack = tooltip.getBackpack();
	}

	@Override
	protected void sendInventorySyncRequest(UUID uuid) {
		PacketDistributor.sendToServer(new RequestBackpackInventoryContentsPayload(uuid));
	}

	/**
	 * 背包内容不跟着物品走：它们存在服务端的 {@code BackpackStorage} 里，客户端只能靠
	 * {@code request_backpack_inventory_contents → backpack_contents} 往返拿到。
	 * <p>
	 * 基类默认恒返回 {@code true}，于是「还没同步到客户端」的每一帧都被判成空背包，
	 * shift 提示永远显示「未加装升级或物品存储」。这里按「这只 uuid 的内容有没有被服务端回过包」判定：
	 * 收到过 → 内容已知（真空就显示 empty）；没收到过 → 显示 syncing。
	 * 没有 storage uuid 的背包从没写过内容，属于真正的空背包。
	 */
	@Override
	protected boolean hasContentsData(IStorageWrapper wrapper) {
		return wrapper.getContentsUuid()
				.map(uuid -> BackpackStorage.get().hasSyncedContents(uuid))
				.orElse(true);
	}
}
