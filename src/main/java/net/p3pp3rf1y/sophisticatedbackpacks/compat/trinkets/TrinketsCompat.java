package net.p3pp3rf1y.sophisticatedbackpacks.compat.trinkets;

import eu.pb4.trinkets.api.TrinketInventory;
import eu.pb4.trinkets.api.TrinketsApi;
import eu.pb4.trinkets.api.callback.TrinketCallback;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.CompatModIds;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.compat.ICompat;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 把 Trinkets 背部的饰品槽接进玩家背包查找链，背包可以放在背部并照常打开、tick 与渲染。
 */
public class TrinketsCompat implements ICompat {
	private static final BackpackTrinket TRINKET_BACKPACK = new BackpackTrinket();
	private static final int TAGS_REFRESH_COOLDOWN = 100;

	private final Set<String> backpackTrinketSlots = new CopyOnWriteArraySet<>();
	private long lastSlotsRefresh = -TAGS_REFRESH_COOLDOWN - 1;

	@Nullable
	public static TrinketInventory getTrinketInventory(Player player, String slotId) {
		return TrinketsApi.getAttachment(player).getInventory(slotId);
	}

	public static <T> T getFromTrinketInventory(Player player, String slotId, Function<TrinketInventory, T> getFromInventory, T defaultValue) {
		TrinketInventory inventory = getTrinketInventory(player, slotId);
		return inventory == null ? defaultValue : getFromInventory.apply(inventory);
	}

	public static boolean isTrinketContainer(Container container) {
		return container instanceof TrinketInventory;
	}

	public static String getIdentifierForSlot(Container container) {
		return container instanceof TrinketInventory trinketInventory ? trinketInventory.slotType().getId() : "";
	}

	@Override
	public void init() {
		// ownRenderer=false：复用胸甲槽那条绘制链（LivingEntityRendererExtractMixin → BackpackLayerRenderer）。
		PlayerInventoryProvider.get().addPlayerInventoryHandler(CompatModIds.TRINKETS, this::getBackpackTrinketSlots,
				(player, slotId) -> getFromTrinketInventory(player, slotId, TrinketInventory::getContainerSize, 0),
				(player, slotId, slot) -> getFromTrinketInventory(player, slotId, inventory -> inventory.getItem(slot), ItemStack.EMPTY),
				false, true, false, true);
	}

	@Override
	public void setup() {
		for (Supplier<BackpackItem> backpack : ModItems.BACKPACKS) {
			TrinketCallback.setCallback(backpack.get(), TRINKET_BACKPACK);
		}
	}

	// 哪些槽能放背包由数据标签决定，刷新有冷却以免每帧重算。
	private Set<String> getBackpackTrinketSlots(Player player, long gameTime) {
		if (lastSlotsRefresh + TAGS_REFRESH_COOLDOWN < gameTime) {
			lastSlotsRefresh = gameTime;

			backpackTrinketSlots.clear();
			ItemStack probe = new ItemStack(ModItems.BACKPACK.get());
			TrinketsApi.getAttachment(player).getInventories().forEach((slotId, inventory) -> {
				if (inventory.getContainerSize() > 0 && inventory.slotType().validatorCheck(probe, inventory.getSlotAccess(0), player)) {
					backpackTrinketSlots.add(slotId);
				}
			});
		}
		return backpackTrinketSlots;
	}
}
