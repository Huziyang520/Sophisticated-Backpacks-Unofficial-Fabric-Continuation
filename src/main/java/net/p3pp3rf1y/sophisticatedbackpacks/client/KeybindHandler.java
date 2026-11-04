package net.p3pp3rf1y.sophisticatedbackpacks.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPTranslationHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.CompatModIds;
import net.p3pp3rf1y.sophisticatedbackpacks.network.*;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.network.PacketDistributor;
import net.p3pp3rf1y.sophisticatedcore.util.CapabilityHelper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KeybindHandler {
	private KeybindHandler() {
	}

	private static final int CHEST_SLOT_INDEX = 38;
	private static final int OFFHAND_SLOT_INDEX = 40;
	private static final KeyMapping.Category SBP_KEY_CATEGORY = KeyMapping.Category.register(SophisticatedBackpacks.getRL("sophisticatedbackpacks"));
	// 26.3 keys are SDL scancodes and "unbound" is 0; any other value is read straight out of the keyboard state buffer.
	private static final int UNBOUND_KEY = InputConstants.UNKNOWN.getValue();
	public static final KeyMapping BACKPACK_TOGGLE_UPGRADE_5 = new KeyMapping(SBPTranslationHelper.INSTANCE.translKeybind("toggle_upgrade_5"),
			UNBOUND_KEY, SBP_KEY_CATEGORY);
	public static final KeyMapping BACKPACK_TOGGLE_UPGRADE_4 = new KeyMapping(SBPTranslationHelper.INSTANCE.translKeybind("toggle_upgrade_4"),
			UNBOUND_KEY, SBP_KEY_CATEGORY);
	public static final KeyMapping BACKPACK_TOGGLE_UPGRADE_3 = new KeyMapping(SBPTranslationHelper.INSTANCE.translKeybind("toggle_upgrade_3"),
			UNBOUND_KEY, SBP_KEY_CATEGORY);
	public static final KeyMapping BACKPACK_TOGGLE_UPGRADE_2 = new KeyMapping(SBPTranslationHelper.INSTANCE.translKeybind("toggle_upgrade_2"),
			InputConstants.KEY_X, SBP_KEY_CATEGORY);
	public static final KeyMapping BACKPACK_TOGGLE_UPGRADE_1 = new KeyMapping(SBPTranslationHelper.INSTANCE.translKeybind("toggle_upgrade_1"),
			InputConstants.KEY_Z, SBP_KEY_CATEGORY);

	public static final Map<Integer, KeyMapping> UPGRADE_SLOT_TOGGLE_KEYBINDS = Map.of(
			0, BACKPACK_TOGGLE_UPGRADE_1,
			1, BACKPACK_TOGGLE_UPGRADE_2,
			2, BACKPACK_TOGGLE_UPGRADE_3,
			3, BACKPACK_TOGGLE_UPGRADE_4,
			4, BACKPACK_TOGGLE_UPGRADE_5
	);
	public static final KeyMapping SORT_KEYBIND = new KeyMapping(SBPTranslationHelper.INSTANCE.translKeybind("sort"),
            InputConstants.Type.MOUSE, InputConstants.MOUSE_BUTTON_MIDDLE, SBP_KEY_CATEGORY); // BackpackGuiKeyConflictContext.INSTANCE
	public static final KeyMapping TOOL_SWAP_KEYBIND = new KeyMapping(SBPTranslationHelper.INSTANCE.translKeybind("tool_swap"),
		    UNBOUND_KEY, SBP_KEY_CATEGORY);
	public static final KeyMapping INVENTORY_INTERACTION_KEYBIND = new KeyMapping(SBPTranslationHelper.INSTANCE.translKeybind("inventory_interaction"),
			InputConstants.KEY_C, SBP_KEY_CATEGORY);
	public static final KeyMapping BACKPACK_OPEN_KEYBIND = new KeyMapping(SBPTranslationHelper.INSTANCE.translKeybind("open_backpack"),
			InputConstants.KEY_B, SBP_KEY_CATEGORY); // BackpackKeyConflictContext.INSTANCE

	public static void register() {
		if (!FabricLoader.getInstance().isModLoaded(CompatModIds.MKB)) {
			ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
				ScreenKeyboardEvents.allowKeyPress(screen).register(KeybindHandler::handleGuiKeyPress);
				ScreenMouseEvents.allowMouseClick(screen).register(KeybindHandler::handleGuiMouseKeyPress);
			});
		}

		ClientTickEvents.END_CLIENT_TICK.register(KeybindHandler::handleKeyInputEvent);
	}

	private static final List<KeyMapping> KEYBINDS = List.of(BACKPACK_TOGGLE_UPGRADE_1, BACKPACK_TOGGLE_UPGRADE_2,
			BACKPACK_TOGGLE_UPGRADE_3, BACKPACK_TOGGLE_UPGRADE_4, BACKPACK_TOGGLE_UPGRADE_5, SORT_KEYBIND, TOOL_SWAP_KEYBIND,
			INVENTORY_INTERACTION_KEYBIND, BACKPACK_OPEN_KEYBIND);
	private static final int KEYBOARD_KEY_COUNT = 512;
	private static boolean legacyKeybindsRepaired;

	/**
	 * Repairs keybinds saved by builds that wrote GLFW key codes into scancode slots: an out of range value makes
	 * {@code KeyMapping.setAll} throw while grabbing the mouse, which leaves the player unable to look around.
	 * Runs once; if any of our binds is out of range every bind of this mod goes back to its default.
	 */
	private static void repairLegacyKeybinds(Minecraft minecraft) {
		if (legacyKeybindsRepaired) {
			return;
		}
		legacyKeybindsRepaired = true;
		if (KEYBINDS.stream().noneMatch(KeybindHandler::isKeyOutOfRange)) {
			return;
		}
		KEYBINDS.forEach(keybind -> keybind.setKey(keybind.getDefaultKey()));
		KeyMapping.resetMapping();
		minecraft.options.save();
	}

	private static boolean isKeyOutOfRange(KeyMapping keybind) {
		InputConstants.Key key = InputConstants.getKey(keybind.saveString());
		return key.getType() == InputConstants.Type.KEYBOARD && (key.getValue() < 0 || key.getValue() >= KEYBOARD_KEY_COUNT);
	}

	public static void registerKeyMappings() {
		KeyMappingHelper.registerKeyMapping(BACKPACK_OPEN_KEYBIND);
		KeyMappingHelper.registerKeyMapping(INVENTORY_INTERACTION_KEYBIND);
		KeyMappingHelper.registerKeyMapping(TOOL_SWAP_KEYBIND);
		KeyMappingHelper.registerKeyMapping(SORT_KEYBIND);
		UPGRADE_SLOT_TOGGLE_KEYBINDS.forEach((slot, keybind) -> KeyMappingHelper.registerKeyMapping(keybind));
	}

	public static boolean handleGuiKeyPress(Screen screen, KeyEvent event) {
		if (SORT_KEYBIND.matches(event) && tryCallSort(screen)) {
			return false;
		} else if (BACKPACK_OPEN_KEYBIND.matches(event) && sendBackpackOpenOrCloseMessage()) {
			return false;
		}
		return true;
	}

	public static boolean handleGuiMouseKeyPress(Screen screen, MouseButtonEvent event) {
		if (SORT_KEYBIND.matchesMouse(event) && tryCallSort(screen)) {
			return false;
		} else if (BACKPACK_OPEN_KEYBIND.matchesMouse(event) && sendBackpackOpenOrCloseMessage()) {
			return false;
		}
        return true;
	}

	private static void handleKeyInputEvent(Minecraft minecraft) {
		repairLegacyKeybinds(minecraft);
		if (BACKPACK_OPEN_KEYBIND.consumeClick()) {
			sendBackpackOpenOrCloseMessage();
		} else if (INVENTORY_INTERACTION_KEYBIND.consumeClick()) {
			sendInteractWithInventoryMessage();
		} else if (TOOL_SWAP_KEYBIND.consumeClick()) {
			sendToolSwapMessage();
		} else {
			for (Map.Entry<Integer, KeyMapping> slotKeybind : UPGRADE_SLOT_TOGGLE_KEYBINDS.entrySet()) {
				if (slotKeybind.getValue().consumeClick()) {
					PacketDistributor.sendToServer(new UpgradeTogglePayload(slotKeybind.getKey()));
				}
			}
		}
	}

	public static boolean tryCallSort(Screen gui) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null && mc.player.containerMenu instanceof BackpackContainer container && gui instanceof BackpackScreen screen) {
			MouseHandler mh = mc.mouseHandler;
			double mouseX = mh.getScaledXPos(mc.getWindow());
			double mouseY = mh.getScaledYPos(mc.getWindow());
			Slot selectedSlot = screen.sophisticatedCore_findSlot(mouseX, mouseY);
			if (selectedSlot == null || container.isNotPlayersInventorySlot(selectedSlot.index)) {
				container.sort();
				return true;
			}
		}
		return false;
	}

	private static void sendToolSwapMessage() {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null || mc.hitResult == null) {
			return;
		}
		if (player.getMainHandItem().getItem() instanceof BackpackItem) {
			player.sendSystemMessage(Component.translatable("gui.sophisticatedbackpacks.status.unable_to_swap_tool_for_backpack"));
			return;
		}
		HitResult rayTrace = mc.hitResult;
		if (rayTrace.getType() == HitResult.Type.BLOCK) {
			BlockHitResult blockRayTraceResult = (BlockHitResult) rayTrace;
			BlockPos pos = blockRayTraceResult.getBlockPos();
			PacketDistributor.sendToServer(new BlockToolSwapPayload(pos));
		} else if (rayTrace.getType() == HitResult.Type.ENTITY) {
			EntityHitResult entityRayTraceResult = (EntityHitResult) rayTrace;
			PacketDistributor.sendToServer(new EntityToolSwapPayload(entityRayTraceResult.getEntity().getId()));
		}
	}

	private static void sendInteractWithInventoryMessage() {
		Minecraft mc = Minecraft.getInstance();
		HitResult rayTrace = mc.hitResult;
		if (rayTrace == null || rayTrace.getType() != HitResult.Type.BLOCK) {
			return;
		}
		BlockHitResult blockraytraceresult = (BlockHitResult) rayTrace;
		BlockPos pos = blockraytraceresult.getBlockPos();

		if (Boolean.FALSE.equals(CapabilityHelper.getFromItemHandler(mc.level, pos, itemHandler -> true, false))) {
			return;
		}

		PacketDistributor.sendToServer(new InventoryInteractionPayload(pos, blockraytraceresult.getDirection()));
	}

	public static boolean sendBackpackOpenOrCloseMessage() {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.gui.screen() == null) {
			PacketDistributor.sendToServer(new BackpackOpenPayload());
			return false;
		}

		Screen screen = minecraft.gui.screen();
		if (screen instanceof AbstractContainerScreen<?> containerScreen) {
			Slot slot = containerScreen.sophisticatedCore_getSlotUnderMouse();

			if (slot != null && (slot.container instanceof Inventory || isTrinket(slot.container))) {
				Optional<PlayerInventoryReturn> handler = getPlayerInventory(slot);

				if (handler.isPresent() && slot.getItem().getItem() instanceof BackpackItem) {
					PacketDistributor.sendToServer(new BackpackOpenPayload(slot.getContainerSlot(), handler.get().identifier(), handler.get().handlerName()));
					return true;
				}
			}
			if (screen instanceof BackpackScreen && slot != null && slot.getItem().getItem() instanceof BackpackItem && slot.getItem().getCount() == 1) {
				PacketDistributor.sendToServer(new BackpackOpenPayload(slot.index));
				return true;
			}
		}
		return false;
	}

	private static boolean isTrinket(Container container) {
		return false;
	}

	record PlayerInventoryReturn(String identifier, String handlerName) {}

	private static Optional<PlayerInventoryReturn> getPlayerInventory(Slot slot) {
		int slotIndex = slot.getContainerSlot();
		if (slotIndex == CHEST_SLOT_INDEX) {
			return Optional.of(new PlayerInventoryReturn("", PlayerInventoryProvider.ARMOR_INVENTORY));
		} else if (slotIndex == OFFHAND_SLOT_INDEX) {
			return Optional.of(new PlayerInventoryReturn("", PlayerInventoryProvider.OFFHAND_INVENTORY));
		} else if (isTrinket(slot.container)) {
			return Optional.empty();
		} else if (slotIndex >= 0 && slotIndex < 36) {
			return Optional.of(new PlayerInventoryReturn("", PlayerInventoryProvider.MAIN_INVENTORY));
		}

		return Optional.empty();
	}
}
