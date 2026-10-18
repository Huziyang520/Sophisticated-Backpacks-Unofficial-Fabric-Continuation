package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
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
		lastRequestTime = 0;
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
}
