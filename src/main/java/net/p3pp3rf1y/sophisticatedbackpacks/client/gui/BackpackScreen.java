package net.p3pp3rf1y.sophisticatedbackpacks.client.gui;

import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.client.KeybindHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BackpackOpenPayload;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.network.PacketDistributor;

public class BackpackScreen extends StorageScreenBase<BackpackContainer> {
	public static BackpackScreen constructScreen(BackpackContainer screenContainer, Inventory inv, Component title) {
		return new BackpackScreen(screenContainer, inv, title);
	}

	public BackpackScreen(BackpackContainer screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		int keyCode = event.key();
		int scanCode = event.keycode();
		int modifiers = event.modifiers();
		/*if (getFocused() != null) {
			return super.keyPressed(event);
		}*/
		if (keyCode == 256 || KeybindHandler.BACKPACK_OPEN_KEYBIND.matches(new net.minecraft.client.input.KeyEvent(keyCode, scanCode, modifiers))) {
			if (getMenu().isFirstLevelStorage() && (keyCode == 256 || mouseNotOverBackpack())) {
				if (getMenu().getBackpackContext().wasOpenFromInventory()) {
					this.minecraft.setScreenAndShow(new InventoryScreen(this.minecraft.player));
				} else {
					onClose();
				}
				return true;
			} else if (!getMenu().isFirstLevelStorage()) {
				PacketDistributor.sendToServer(new BackpackOpenPayload());
				return true;
			}
		}
		return super.keyPressed(event);
	}

	private boolean mouseNotOverBackpack() {
		Slot selectedSlot = sophisticatedCore_getSlotUnderMouse();
		return selectedSlot == null || !(selectedSlot.getItem().getItem() instanceof BackpackItem);
	}

	@Override
	protected String getStorageSettingsTabTooltip() {
		return SBPTranslationHelper.INSTANCE.translGui("settings.tooltip");
	}

	// 26.3：这里曾有一段「槽位数为 0 就 closeContainer」的逻辑，是排查期的临时补丁。
	// 它会让界面开一帧就被自己关掉（表现为“打不开、也不崩”），已删除。
}
