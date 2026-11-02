package net.p3pp3rf1y.sophisticatedbackpacks.data;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.providers.number.ints.ConstantValue;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;

import java.util.List;

/**
 * Injects backpacks and upgrades into vanilla loot chests.
 * <p>
 * 26.3 has no datagen-time hook for modifying existing loot tables, so this runs on the server's
 * loot table load instead: each table is rebuilt with one extra weighted pool that only rolls when
 * the original table loads.
 */
public final class LootTableInjector {
	private LootTableInjector() {
	}

	private record Injection(ResourceKey<LootTable> table, List<ItemEntry> entries, int emptyWeight) {
	}

	private record ItemEntry(Item item, int weight) {
	}

	private static final List<Injection> INJECTIONS = List.of(
			new Injection(BuiltInLootTables.SIMPLE_DUNGEON, List.of(
					new ItemEntry(ModItems.BACKPACK.get(), 5),
					new ItemEntry(ModItems.COPPER_BACKPACK.get(), 3),
					new ItemEntry(ModItems.PICKUP_UPGRADE.get(), 2)), 90),
			new Injection(BuiltInLootTables.ABANDONED_MINESHAFT, List.of(
					new ItemEntry(ModItems.BACKPACK.get(), 7),
					new ItemEntry(ModItems.COPPER_BACKPACK.get(), 5),
					new ItemEntry(ModItems.IRON_BACKPACK.get(), 3),
					new ItemEntry(ModItems.GOLD_BACKPACK.get(), 1),
					new ItemEntry(ModItems.MAGNET_UPGRADE.get(), 2)), 84),
			new Injection(BuiltInLootTables.DESERT_PYRAMID, List.of(
					new ItemEntry(ModItems.COPPER_BACKPACK.get(), 5),
					new ItemEntry(ModItems.IRON_BACKPACK.get(), 3),
					new ItemEntry(ModItems.GOLD_BACKPACK.get(), 1),
					new ItemEntry(ModItems.MAGNET_UPGRADE.get(), 2)), 89),
			new Injection(BuiltInLootTables.SHIPWRECK_TREASURE, List.of(
					new ItemEntry(ModItems.IRON_BACKPACK.get(), 4),
					new ItemEntry(ModItems.GOLD_BACKPACK.get(), 2),
					new ItemEntry(ModItems.ADVANCED_MAGNET_UPGRADE.get(), 2)), 92),
			new Injection(BuiltInLootTables.WOODLAND_MANSION, List.of(
					new ItemEntry(ModItems.IRON_BACKPACK.get(), 4),
					new ItemEntry(ModItems.GOLD_BACKPACK.get(), 2),
					new ItemEntry(ModItems.ADVANCED_MAGNET_UPGRADE.get(), 2)), 92),
			new Injection(BuiltInLootTables.NETHER_BRIDGE, List.of(
					new ItemEntry(ModItems.IRON_BACKPACK.get(), 5),
					new ItemEntry(ModItems.GOLD_BACKPACK.get(), 3),
					new ItemEntry(ModItems.FEEDING_UPGRADE.get(), 2)), 90),
			new Injection(BuiltInLootTables.BASTION_TREASURE, List.of(
					new ItemEntry(ModItems.IRON_BACKPACK.get(), 3),
					new ItemEntry(ModItems.GOLD_BACKPACK.get(), 5),
					new ItemEntry(ModItems.FEEDING_UPGRADE.get(), 2)), 90),
			new Injection(BuiltInLootTables.END_CITY_TREASURE, List.of(
					new ItemEntry(ModItems.DIAMOND_BACKPACK.get(), 3),
					new ItemEntry(ModItems.GOLD_BACKPACK.get(), 5),
					new ItemEntry(ModItems.ADVANCED_MAGNET_UPGRADE.get(), 2)), 90)
	);

	public static void register() {
		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			if (!source.isBuiltin()) {
				return;
			}
			for (Injection injection : INJECTIONS) {
				if (injection.table().equals(key)) {
					tableBuilder.withPool(buildPool(injection));
					return;
				}
			}
		});
	}

	private static LootPool.Builder buildPool(Injection injection) {
		LootPool.Builder pool = LootPool.lootPool().setRolls(Holder.direct(new ConstantValue(1)));
		for (ItemEntry entry : injection.entries()) {
			LootPoolEntryContainer.Builder<?> lootEntry = LootItem.lootTableItem(entry.item()).setWeight(entry.weight());
			pool.add(lootEntry);
		}
		pool.add(EmptyLootItem.emptyItem().setWeight(injection.emptyWeight()));
		return pool;
	}
}
