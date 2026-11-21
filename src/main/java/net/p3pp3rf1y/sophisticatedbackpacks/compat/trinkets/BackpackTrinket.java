package net.p3pp3rf1y.sophisticatedbackpacks.compat.trinkets;

import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.callback.TrinketCallback;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 让背部饰品槽里的背包按胸甲槽处理，使升级的 tick 照常运行。
 */
public class BackpackTrinket implements TrinketCallback {
	@Override
	public void tick(ItemStack stack, TrinketSlotAccess slot, LivingEntity entity) {
		stack.inventoryTick(entity.level(), entity, EquipmentSlot.CHEST);
	}
}
