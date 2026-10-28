package net.p3pp3rf1y.sophisticatedbackpacks.init;

import com.mojang.serialization.MapCodec;
import net.p3pp3rf1y.sophisticatedcore.platform.DeferredHolder;
import net.p3pp3rf1y.sophisticatedcore.platform.DeferredRegister;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.BlockHitResult;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackSettingsContainerMenu;
import net.p3pp3rf1y.sophisticatedbackpacks.crafting.BackpackDyeRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.crafting.BackpackUpgradeRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.crafting.BasicBackpackRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.crafting.SmithingBackpackUpgradeRecipe;
import net.p3pp3rf1y.sophisticatedcore.crafting.RecipeWrapperSerializer;
import net.p3pp3rf1y.sophisticatedbackpacks.data.CopyBackpackDataFunction;
import net.p3pp3rf1y.sophisticatedbackpacks.data.SBLootEnabledCondition;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.anvil.AnvilUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.anvil.AnvilUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.anvil.AnvilUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.everlasting.EverlastingBackpackItemEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.everlasting.EverlastingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.inception.InceptionUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.inception.InceptionUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.inception.InceptionUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.refill.RefillUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.refill.RefillUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.refill.RefillUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.restock.RestockUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.restock.RestockUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.smithing.SmithingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.smithing.SmithingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.smithing.SmithingUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.toolswapper.ToolSwapperUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.toolswapper.ToolSwapperUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.toolswapper.ToolSwapperUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerRegistry;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedcore.util.EmptyItemHandler;
import net.p3pp3rf1y.sophisticatedcore.util.IMenuTypeExtension;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilteredUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.compacting.CompactingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.compacting.CompactingUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.compacting.CompactingUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.*;
import net.p3pp3rf1y.sophisticatedcore.upgrades.crafting.CraftingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.crafting.CraftingUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.crafting.CraftingUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.feeding.FeedingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.feeding.FeedingUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.feeding.FeedingUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.filter.FilterUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.filter.FilterUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.infinity.InfinityUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.JukeboxUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.JukeboxUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.JukeboxUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.MagnetUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.MagnetUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.MagnetUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pickup.PickupUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pickup.PickupUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pump.PumpUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pump.PumpUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pump.PumpUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stack.StackUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stonecutter.StonecutterUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stonecutter.StonecutterUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stonecutter.StonecutterUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.xppump.XpPumpUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.xppump.XpPumpUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.xppump.XpPumpUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.util.ItemBase;

import java.util.List;
import java.util.function.Supplier;

public class ModItems {
	private ModItems() {
	}

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, SophisticatedBackpacks.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SophisticatedBackpacks.MOD_ID);
	// 26.3 的战利品函数 / 条件注册表直接持有 MapCodec，不再有 *Type 包装类
	public static final DeferredRegister<MapCodec<? extends LootItemFunction>> LOOT_FUNCTION_TYPES = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, SophisticatedBackpacks.MOD_ID);
	public static final DeferredRegister<MapCodec<? extends LootItemCondition>> LOOT_CONDITION_TYPES = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, SophisticatedBackpacks.MOD_ID);
	private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, SophisticatedBackpacks.MOD_ID);
	private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, SophisticatedBackpacks.MOD_ID);

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// ITEMS
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Supplier<BackpackItem> BACKPACK = ITEMS.register("backpack",
			() -> new BackpackItem(Config.SERVER.leatherBackpack.inventorySlotCount::get, Config.SERVER.leatherBackpack.upgradeSlotCount::get, ModBlocks.BACKPACK));
	public static final Supplier<BackpackItem> COPPER_BACKPACK = ITEMS.register("copper_backpack",
			() -> new BackpackItem(Config.SERVER.copperBackpack.inventorySlotCount::get, Config.SERVER.copperBackpack.upgradeSlotCount::get, ModBlocks.COPPER_BACKPACK));
	public static final Supplier<BackpackItem> IRON_BACKPACK = ITEMS.register("iron_backpack",
			() -> new BackpackItem(Config.SERVER.ironBackpack.inventorySlotCount::get, Config.SERVER.ironBackpack.upgradeSlotCount::get, ModBlocks.IRON_BACKPACK));
	public static final Supplier<BackpackItem> GOLD_BACKPACK = ITEMS.register("gold_backpack",
			() -> new BackpackItem(Config.SERVER.goldBackpack.inventorySlotCount::get, Config.SERVER.goldBackpack.upgradeSlotCount::get, ModBlocks.GOLD_BACKPACK));
	public static final Supplier<BackpackItem> DIAMOND_BACKPACK = ITEMS.register("diamond_backpack",
			() -> new BackpackItem(Config.SERVER.diamondBackpack.inventorySlotCount::get, Config.SERVER.diamondBackpack.upgradeSlotCount::get, ModBlocks.DIAMOND_BACKPACK));
	public static final Supplier<BackpackItem> NETHERITE_BACKPACK = ITEMS.register("netherite_backpack",
			() -> new BackpackItem(Config.SERVER.netheriteBackpack.inventorySlotCount::get, Config.SERVER.netheriteBackpack.upgradeSlotCount::get, ModBlocks.NETHERITE_BACKPACK, Item.Properties::fireResistant));

	public static final List<Supplier<BackpackItem>> BACKPACKS = List.of(BACKPACK, COPPER_BACKPACK, IRON_BACKPACK, GOLD_BACKPACK, DIAMOND_BACKPACK, NETHERITE_BACKPACK);

	public static final Identifier BACKPACK_UPGRADE_TAG_NAME = Identifier.fromNamespaceAndPath(SophisticatedBackpacks.MOD_ID, "upgrade");

	public static final TagKey<Item> BACKPACK_UPGRADE_TAG = TagKey.create(Registries.ITEM, BACKPACK_UPGRADE_TAG_NAME);

	public static final DeferredHolder<Item, PickupUpgradeItem> PICKUP_UPGRADE = ITEMS.register("pickup_upgrade",
			() -> new PickupUpgradeItem(Config.SERVER.pickupUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, PickupUpgradeItem> ADVANCED_PICKUP_UPGRADE = ITEMS.register("advanced_pickup_upgrade",
			() -> new PickupUpgradeItem(Config.SERVER.advancedPickupUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, FilterUpgradeItem> FILTER_UPGRADE = ITEMS.register("filter_upgrade",
			() -> new FilterUpgradeItem(Config.SERVER.filterUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, FilterUpgradeItem> ADVANCED_FILTER_UPGRADE = ITEMS.register("advanced_filter_upgrade",
			() -> new FilterUpgradeItem(Config.SERVER.advancedFilterUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, MagnetUpgradeItem> MAGNET_UPGRADE = ITEMS.register("magnet_upgrade",
			() -> new MagnetUpgradeItem(Config.SERVER.magnetUpgrade.magnetRange::get, Config.SERVER.magnetUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, MagnetUpgradeItem> ADVANCED_MAGNET_UPGRADE = ITEMS.register("advanced_magnet_upgrade",
			() -> new MagnetUpgradeItem(Config.SERVER.advancedMagnetUpgrade.magnetRange::get, Config.SERVER.advancedMagnetUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, FeedingUpgradeItem> FEEDING_UPGRADE = ITEMS.register("feeding_upgrade",
			() -> new FeedingUpgradeItem(Config.SERVER.feedingUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, FeedingUpgradeItem> ADVANCED_FEEDING_UPGRADE = ITEMS.register("advanced_feeding_upgrade",
			() -> new FeedingUpgradeItem(Config.SERVER.advancedFeedingUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, CompactingUpgradeItem> COMPACTING_UPGRADE = ITEMS.register("compacting_upgrade",
			() -> new CompactingUpgradeItem(false, Config.SERVER.compactingUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, CompactingUpgradeItem> ADVANCED_COMPACTING_UPGRADE = ITEMS.register("advanced_compacting_upgrade",
			() -> new CompactingUpgradeItem(true, Config.SERVER.advancedCompactingUpgrade.filterSlots::get, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, VoidUpgradeItem> VOID_UPGRADE = ITEMS.register("void_upgrade",
			() -> new VoidUpgradeItem(Config.SERVER.voidUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, VoidUpgradeItem> ADVANCED_VOID_UPGRADE = ITEMS.register("advanced_void_upgrade",
			() -> new VoidUpgradeItem(Config.SERVER.advancedVoidUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, RestockUpgradeItem> RESTOCK_UPGRADE = ITEMS.register("restock_upgrade",
			() -> new RestockUpgradeItem(Config.SERVER.restockUpgrade.filterSlots::get));
	public static final DeferredHolder<Item, RestockUpgradeItem> ADVANCED_RESTOCK_UPGRADE = ITEMS.register("advanced_restock_upgrade",
			() -> new RestockUpgradeItem(Config.SERVER.advancedRestockUpgrade.filterSlots::get));
	public static final DeferredHolder<Item, DepositUpgradeItem> DEPOSIT_UPGRADE = ITEMS.register("deposit_upgrade",
			() -> new DepositUpgradeItem(Config.SERVER.depositUpgrade.filterSlots::get));
	public static final DeferredHolder<Item, DepositUpgradeItem> ADVANCED_DEPOSIT_UPGRADE = ITEMS.register("advanced_deposit_upgrade",
			() -> new DepositUpgradeItem(Config.SERVER.advancedDepositUpgrade.filterSlots::get));
	public static final DeferredHolder<Item, RefillUpgradeItem> REFILL_UPGRADE = ITEMS.register("refill_upgrade",
			() -> new RefillUpgradeItem(Config.SERVER.refillUpgrade.filterSlots::get, false, false));
	public static final DeferredHolder<Item, RefillUpgradeItem> ADVANCED_REFILL_UPGRADE = ITEMS.register("advanced_refill_upgrade",
			() -> new RefillUpgradeItem(Config.SERVER.advancedRefillUpgrade.filterSlots::get, true, true));
	public static final DeferredHolder<Item, InceptionUpgradeItem> INCEPTION_UPGRADE = ITEMS.register("inception_upgrade",
			InceptionUpgradeItem::new);
	public static final DeferredHolder<Item, EverlastingUpgradeItem> EVERLASTING_UPGRADE = ITEMS.register("everlasting_upgrade",
			EverlastingUpgradeItem::new);
	public static final DeferredHolder<Item, SmeltingUpgradeItem> SMELTING_UPGRADE = ITEMS.register("smelting_upgrade",
			() -> new SmeltingUpgradeItem(Config.SERVER.smeltingUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, AutoSmeltingUpgradeItem> AUTO_SMELTING_UPGRADE = ITEMS.register("auto_smelting_upgrade",
			() -> new AutoSmeltingUpgradeItem(Config.SERVER.autoSmeltingUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, SmokingUpgradeItem> SMOKING_UPGRADE = ITEMS.register("smoking_upgrade",
			() -> new SmokingUpgradeItem(Config.SERVER.smokingUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, AutoSmokingUpgradeItem> AUTO_SMOKING_UPGRADE = ITEMS.register("auto_smoking_upgrade",
			() -> new AutoSmokingUpgradeItem(Config.SERVER.autoSmokingUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, BlastingUpgradeItem> BLASTING_UPGRADE = ITEMS.register("blasting_upgrade",
			() -> new BlastingUpgradeItem(Config.SERVER.blastingUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, AutoBlastingUpgradeItem> AUTO_BLASTING_UPGRADE = ITEMS.register("auto_blasting_upgrade",
			() -> new AutoBlastingUpgradeItem(Config.SERVER.autoBlastingUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, CraftingUpgradeItem> CRAFTING_UPGRADE = ITEMS.register("crafting_upgrade", () -> new CraftingUpgradeItem(Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, StonecutterUpgradeItem> STONECUTTER_UPGRADE = ITEMS.register("stonecutter_upgrade", () -> new StonecutterUpgradeItem(Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, StackUpgradeItem> STACK_UPGRADE_STARTER_TIER = ITEMS.register("stack_upgrade_starter_tier", () ->
			new StackUpgradeItem(1.5D, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, StackUpgradeItem> STACK_UPGRADE_TIER_1 = ITEMS.register("stack_upgrade_tier_1", () ->
			new StackUpgradeItem(2, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, StackUpgradeItem> STACK_UPGRADE_TIER_2 = ITEMS.register("stack_upgrade_tier_2", () ->
			new StackUpgradeItem(4, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, StackUpgradeItem> STACK_UPGRADE_TIER_3 = ITEMS.register("stack_upgrade_tier_3", () ->
			new StackUpgradeItem(8, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, StackUpgradeItem> STACK_UPGRADE_TIER_4 = ITEMS.register("stack_upgrade_tier_4", () ->
			new StackUpgradeItem(16, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, StackUpgradeItem> STACK_DOWNGRADE_TIER_1 = ITEMS.register("stack_downgrade_tier_1", () ->
			new StackUpgradeItem(0.125, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, StackUpgradeItem> STACK_DOWNGRADE_TIER_2 = ITEMS.register("stack_downgrade_tier_2", () ->
			new StackUpgradeItem(0.0625, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, StackUpgradeItem> STACK_DOWNGRADE_TIER_3 = ITEMS.register("stack_downgrade_tier_3", () ->
			new StackUpgradeItem(0.03125, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, StackUpgradeItem> STACK_UPGRADE_OMEGA_TIER = ITEMS.register("stack_upgrade_omega_tier", () ->
			new StackUpgradeItem(Integer.MAX_VALUE, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, JukeboxUpgradeItem> JUKEBOX_UPGRADE = ITEMS.register("jukebox_upgrade", () -> new JukeboxUpgradeItem(Config.SERVER.maxUpgradesPerStorage, () -> 1, () -> 1));
	public static final DeferredHolder<Item, JukeboxUpgradeItem> ADVANCED_JUKEBOX_UPGRADE = ITEMS.register("advanced_jukebox_upgrade", () -> new JukeboxUpgradeItem(Config.SERVER.maxUpgradesPerStorage, Config.SERVER.advancedJukeboxUpgrade.numberOfSlots::get, Config.SERVER.advancedJukeboxUpgrade.slotsInRow::get));
	public static final DeferredHolder<Item, ToolSwapperUpgradeItem> TOOL_SWAPPER_UPGRADE = ITEMS.register("tool_swapper_upgrade",
			() -> new ToolSwapperUpgradeItem(false, false));
	public static final DeferredHolder<Item, ToolSwapperUpgradeItem> ADVANCED_TOOL_SWAPPER_UPGRADE = ITEMS.register("advanced_tool_swapper_upgrade",
			() -> new ToolSwapperUpgradeItem(true, true));
	public static final DeferredHolder<Item, TankUpgradeItem> TANK_UPGRADE = ITEMS.register("tank_upgrade", () -> new TankUpgradeItem(Config.SERVER.tankUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, PumpUpgradeItem> PUMP_UPGRADE = ITEMS.register("pump_upgrade", () -> new PumpUpgradeItem(false, true, Config.SERVER.pumpUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, PumpUpgradeItem> ADVANCED_PUMP_UPGRADE = ITEMS.register("advanced_pump_upgrade", () -> new PumpUpgradeItem(true, true, Config.SERVER.pumpUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, XpPumpUpgradeItem> XP_PUMP_UPGRADE = ITEMS.register("xp_pump_upgrade", () -> new XpPumpUpgradeItem(Config.SERVER.xpPumpUpgrade, Config.SERVER.maxUpgradesPerStorage));
	public static final DeferredHolder<Item, AnvilUpgradeItem> ANVIL_UPGRADE = ITEMS.register("anvil_upgrade", AnvilUpgradeItem::new);
	public static final DeferredHolder<Item, SmithingUpgradeItem> SMITHING_UPGRADE = ITEMS.register("smithing_upgrade", SmithingUpgradeItem::new);
	public static final DeferredHolder<Item, InfinityUpgradeItem> INFINITY_UPGRADE = ITEMS.register("infinity_upgrade", () -> new InfinityUpgradeItem(Config.SERVER.maxUpgradesPerStorage, true));
	public static final DeferredHolder<Item, InfinityUpgradeItem> SURVIVAL_INFINITY_UPGRADE = ITEMS.register("survival_infinity_upgrade", () -> new InfinityUpgradeItem(Config.SERVER.maxUpgradesPerStorage, false));

	public static final Supplier<ItemBase> UPGRADE_BASE = ITEMS.register("upgrade_base", () -> new ItemBase(net.p3pp3rf1y.sophisticatedcore.platform.RegistryIdContext.applyToItem(new Item.Properties()).stacksTo(16)));

	public static final Supplier<CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("main", () ->
			CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0).icon(() -> new ItemStack(ModItems.BACKPACK.get()))
					.title(Component.translatable("itemGroup.sophisticatedbackpacks"))
					.displayItems((featureFlags, output) -> BuiltInRegistries.ITEM.stream()
							.filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(SophisticatedBackpacks.MOD_ID))
							.filter(ItemBase.class::isInstance).map(ItemBase.class::cast)
							.forEach(item -> item.addCreativeTabItems(output::accept)))
					.build());

	public static final Supplier<MenuType<BackpackContainer>> BACKPACK_CONTAINER_TYPE = MENU_TYPES.register("backpack",
			() -> IMenuTypeExtension.create(BackpackContainer::fromBuffer));

	public static final Supplier<MenuType<BackpackSettingsContainerMenu>> SETTINGS_CONTAINER_TYPE = MENU_TYPES.register("settings",
			() -> IMenuTypeExtension.create(BackpackSettingsContainerMenu::fromBuffer));

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// ENTITY_TYPES
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	public static final Supplier<EntityType<EverlastingBackpackItemEntity>> EVERLASTING_BACKPACK_ITEM_ENTITY = ENTITY_TYPES.register(
			"everlasting_backpack_item", () -> EntityType.Builder.of(EverlastingBackpackItemEntity::new, MobCategory.MISC)
					.sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(20)
					.build(ResourceKey.create(Registries.ENTITY_TYPE, SophisticatedBackpacks.getRL("everlasting_backpack_item")))
	);

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// RECIPE_SERIALIZERS
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, SophisticatedBackpacks.MOD_ID);

	// 26.3 没有 SimpleCraftingRecipeSerializer，自定义配方统一走核心仓的 RecipeWrapperSerializer
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BackpackDyeRecipe>> BACKPACK_DYE_RECIPE_SERIALIZER =
			RECIPE_SERIALIZERS.register("backpack_dye", () -> RecipeWrapperSerializer.unit(new BackpackDyeRecipe()));
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BackpackUpgradeRecipe>> BACKPACK_UPGRADE_RECIPE_SERIALIZER =
			RECIPE_SERIALIZERS.register("backpack_upgrade", BackpackUpgradeRecipe.Serializer::create);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SmithingBackpackUpgradeRecipe>> SMITHING_BACKPACK_UPGRADE_RECIPE_SERIALIZER =
			RECIPE_SERIALIZERS.register("smithing_backpack_upgrade", SmithingBackpackUpgradeRecipe.Serializer::create);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicBackpackRecipe>> BASIC_BACKPACK_RECIPE_SERIALIZER =
			RECIPE_SERIALIZERS.register("basic_backpack", BasicBackpackRecipe.Serializer::create);

	public static final Supplier<MapCodec<? extends LootItemFunction>> COPY_BACKPACK_DATA = LOOT_FUNCTION_TYPES.register("copy_backpack_data", () -> CopyBackpackDataFunction.CODEC);
	public static final Supplier<MapCodec<? extends LootItemCondition>> LOOT_ENABLED_CONDITION = LOOT_CONDITION_TYPES.register("loot_enabled", () -> SBLootEnabledCondition.CODEC);

	public static void registerHandlers() {
		ITEMS.register();
		ModDataComponents.register();
		CREATIVE_MODE_TABS.register();
		MENU_TYPES.register();
		ENTITY_TYPES.register();
		RECIPE_SERIALIZERS.register();
		LOOT_FUNCTION_TYPES.register();
		LOOT_CONDITION_TYPES.register();

		registerContainers();
		registerCapabilities();
	}

	public static final UpgradeContainerType<PickupUpgradeWrapper, ContentsFilteredUpgradeContainer<PickupUpgradeWrapper>> PICKUP_BASIC_TYPE = new UpgradeContainerType<>(ContentsFilteredUpgradeContainer::new);
	public static final UpgradeContainerType<PickupUpgradeWrapper, ContentsFilteredUpgradeContainer<PickupUpgradeWrapper>> PICKUP_ADVANCED_TYPE = new UpgradeContainerType<>(ContentsFilteredUpgradeContainer::new);
	public static final UpgradeContainerType<MagnetUpgradeWrapper, MagnetUpgradeContainer> MAGNET_BASIC_TYPE = new UpgradeContainerType<>(MagnetUpgradeContainer::new);
	public static final UpgradeContainerType<MagnetUpgradeWrapper, MagnetUpgradeContainer> MAGNET_ADVANCED_TYPE = new UpgradeContainerType<>(MagnetUpgradeContainer::new);
	public static final UpgradeContainerType<FeedingUpgradeWrapper, FeedingUpgradeContainer> FEEDING_TYPE = new UpgradeContainerType<>(FeedingUpgradeContainer::new);
	public static final UpgradeContainerType<FeedingUpgradeWrapper, FeedingUpgradeContainer> ADVANCED_FEEDING_TYPE = new UpgradeContainerType<>(FeedingUpgradeContainer::new);
	public static final UpgradeContainerType<CompactingUpgradeWrapper, CompactingUpgradeContainer> COMPACTING_TYPE = new UpgradeContainerType<>(CompactingUpgradeContainer::new);
	public static final UpgradeContainerType<CompactingUpgradeWrapper, CompactingUpgradeContainer> ADVANCED_COMPACTING_TYPE = new UpgradeContainerType<>(CompactingUpgradeContainer::new);
	public static final UpgradeContainerType<VoidUpgradeWrapper, VoidUpgradeContainer> VOID_TYPE = new UpgradeContainerType<>(VoidUpgradeContainer::new);
	public static final UpgradeContainerType<VoidUpgradeWrapper, VoidUpgradeContainer> ADVANCED_VOID_TYPE = new UpgradeContainerType<>(VoidUpgradeContainer::new);
	public static final UpgradeContainerType<RestockUpgradeWrapper, ContentsFilteredUpgradeContainer<RestockUpgradeWrapper>> RESTOCK_TYPE = new UpgradeContainerType<>(ContentsFilteredUpgradeContainer::new);
	public static final UpgradeContainerType<RestockUpgradeWrapper, ContentsFilteredUpgradeContainer<RestockUpgradeWrapper>> ADVANCED_RESTOCK_TYPE = new UpgradeContainerType<>(ContentsFilteredUpgradeContainer::new);
	public static final UpgradeContainerType<DepositUpgradeWrapper, DepositUpgradeContainer> DEPOSIT_TYPE = new UpgradeContainerType<>(DepositUpgradeContainer::new);
	public static final UpgradeContainerType<DepositUpgradeWrapper, DepositUpgradeContainer> ADVANCED_DEPOSIT_TYPE = new UpgradeContainerType<>(DepositUpgradeContainer::new);
	public static final UpgradeContainerType<RefillUpgradeWrapper, RefillUpgradeContainer> REFILL_TYPE = new UpgradeContainerType<>(RefillUpgradeContainer::new);
	public static final UpgradeContainerType<RefillUpgradeWrapper, RefillUpgradeContainer> ADVANCED_REFILL_TYPE = new UpgradeContainerType<>(RefillUpgradeContainer::new);
	public static final UpgradeContainerType<CookingUpgradeWrapper.SmeltingUpgradeWrapper, CookingUpgradeContainer<SmeltingRecipe, CookingUpgradeWrapper.SmeltingUpgradeWrapper>> SMELTING_TYPE = new UpgradeContainerType<>(CookingUpgradeContainer::new);
	public static final UpgradeContainerType<AutoCookingUpgradeWrapper.AutoSmeltingUpgradeWrapper, AutoCookingUpgradeContainer<SmeltingRecipe, AutoCookingUpgradeWrapper.AutoSmeltingUpgradeWrapper>> AUTO_SMELTING_TYPE = new UpgradeContainerType<>(AutoCookingUpgradeContainer::new);
	public static final UpgradeContainerType<CookingUpgradeWrapper.SmokingUpgradeWrapper, CookingUpgradeContainer<SmokingRecipe, CookingUpgradeWrapper.SmokingUpgradeWrapper>> SMOKING_TYPE = new UpgradeContainerType<>(CookingUpgradeContainer::new);
	public static final UpgradeContainerType<AutoCookingUpgradeWrapper.AutoSmokingUpgradeWrapper, AutoCookingUpgradeContainer<SmokingRecipe, AutoCookingUpgradeWrapper.AutoSmokingUpgradeWrapper>> AUTO_SMOKING_TYPE = new UpgradeContainerType<>(AutoCookingUpgradeContainer::new);
	public static final UpgradeContainerType<CookingUpgradeWrapper.BlastingUpgradeWrapper, CookingUpgradeContainer<BlastingRecipe, CookingUpgradeWrapper.BlastingUpgradeWrapper>> BLASTING_TYPE = new UpgradeContainerType<>(CookingUpgradeContainer::new);
	public static final UpgradeContainerType<AutoCookingUpgradeWrapper.AutoBlastingUpgradeWrapper, AutoCookingUpgradeContainer<BlastingRecipe, AutoCookingUpgradeWrapper.AutoBlastingUpgradeWrapper>> AUTO_BLASTING_TYPE = new UpgradeContainerType<>(AutoCookingUpgradeContainer::new);
	public static final UpgradeContainerType<CraftingUpgradeWrapper, CraftingUpgradeContainer> CRAFTING_TYPE = new UpgradeContainerType<>(CraftingUpgradeContainer::new);
	public static final UpgradeContainerType<InceptionUpgradeWrapper, InceptionUpgradeContainer> INCEPTION_TYPE = new UpgradeContainerType<>(InceptionUpgradeContainer::new);
	public static final UpgradeContainerType<StonecutterUpgradeWrapper, StonecutterUpgradeContainer> STONECUTTER_TYPE = new UpgradeContainerType<>(StonecutterUpgradeContainer::new);
	public static final UpgradeContainerType<JukeboxUpgradeWrapper, JukeboxUpgradeContainer> JUKEBOX_TYPE = new UpgradeContainerType<>(JukeboxUpgradeContainer::new);
	public static final UpgradeContainerType<JukeboxUpgradeWrapper, JukeboxUpgradeContainer> ADVANCED_JUKEBOX_TYPE = new UpgradeContainerType<>(JukeboxUpgradeContainer::new);
	public static final UpgradeContainerType<ToolSwapperUpgradeWrapper, ToolSwapperUpgradeContainer> TOOL_SWAPPER_TYPE = new UpgradeContainerType<>(ToolSwapperUpgradeContainer::new);
	public static final UpgradeContainerType<TankUpgradeWrapper, TankUpgradeContainer> TANK_TYPE = new UpgradeContainerType<>(TankUpgradeContainer::new);
	public static final UpgradeContainerType<PumpUpgradeWrapper, PumpUpgradeContainer> PUMP_TYPE = new UpgradeContainerType<>(PumpUpgradeContainer::new);
	public static final UpgradeContainerType<PumpUpgradeWrapper, PumpUpgradeContainer> ADVANCED_PUMP_TYPE = new UpgradeContainerType<>(PumpUpgradeContainer::new);
	public static final UpgradeContainerType<XpPumpUpgradeWrapper, XpPumpUpgradeContainer> XP_PUMP_TYPE = new UpgradeContainerType<>(XpPumpUpgradeContainer::new);
	public static final UpgradeContainerType<AnvilUpgradeWrapper, AnvilUpgradeContainer> ANVIL_TYPE = new UpgradeContainerType<>(AnvilUpgradeContainer::new);
	public static final UpgradeContainerType<SmithingUpgradeWrapper, SmithingUpgradeContainer> SMITHING_TYPE = new UpgradeContainerType<>(SmithingUpgradeContainer::new);

	public static void registerContainers() {
		UpgradeContainerRegistry.register(PICKUP_UPGRADE.getId(), PICKUP_BASIC_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_PICKUP_UPGRADE.getId(), PICKUP_ADVANCED_TYPE);
		UpgradeContainerRegistry.register(FILTER_UPGRADE.getId(), FilterUpgradeContainer.BASIC_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_FILTER_UPGRADE.getId(), FilterUpgradeContainer.ADVANCED_TYPE);
		UpgradeContainerRegistry.register(MAGNET_UPGRADE.getId(), MAGNET_BASIC_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_MAGNET_UPGRADE.getId(), MAGNET_ADVANCED_TYPE);
		UpgradeContainerRegistry.register(FEEDING_UPGRADE.getId(), FEEDING_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_FEEDING_UPGRADE.getId(), ADVANCED_FEEDING_TYPE);
		UpgradeContainerRegistry.register(COMPACTING_UPGRADE.getId(), COMPACTING_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_COMPACTING_UPGRADE.getId(), ADVANCED_COMPACTING_TYPE);
		UpgradeContainerRegistry.register(VOID_UPGRADE.getId(), VOID_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_VOID_UPGRADE.getId(), ADVANCED_VOID_TYPE);
		UpgradeContainerRegistry.register(RESTOCK_UPGRADE.getId(), RESTOCK_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_RESTOCK_UPGRADE.getId(), ADVANCED_RESTOCK_TYPE);
		UpgradeContainerRegistry.register(DEPOSIT_UPGRADE.getId(), DEPOSIT_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_DEPOSIT_UPGRADE.getId(), ADVANCED_DEPOSIT_TYPE);
		UpgradeContainerRegistry.register(REFILL_UPGRADE.getId(), REFILL_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_REFILL_UPGRADE.getId(), ADVANCED_REFILL_TYPE);
		UpgradeContainerRegistry.register(SMELTING_UPGRADE.getId(), SMELTING_TYPE);
		UpgradeContainerRegistry.register(AUTO_SMELTING_UPGRADE.getId(), AUTO_SMELTING_TYPE);
		UpgradeContainerRegistry.register(SMOKING_UPGRADE.getId(), SMOKING_TYPE);
		UpgradeContainerRegistry.register(AUTO_SMOKING_UPGRADE.getId(), AUTO_SMOKING_TYPE);
		UpgradeContainerRegistry.register(BLASTING_UPGRADE.getId(), BLASTING_TYPE);
		UpgradeContainerRegistry.register(AUTO_BLASTING_UPGRADE.getId(), AUTO_BLASTING_TYPE);
		UpgradeContainerRegistry.register(CRAFTING_UPGRADE.getId(), CRAFTING_TYPE);
		UpgradeContainerRegistry.register(INCEPTION_UPGRADE.getId(), INCEPTION_TYPE);
		UpgradeContainerRegistry.register(STONECUTTER_UPGRADE.getId(), STONECUTTER_TYPE);
		UpgradeContainerRegistry.register(JUKEBOX_UPGRADE.getId(), JUKEBOX_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_JUKEBOX_UPGRADE.getId(), ADVANCED_JUKEBOX_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_TOOL_SWAPPER_UPGRADE.getId(), TOOL_SWAPPER_TYPE);
		UpgradeContainerRegistry.register(TANK_UPGRADE.getId(), TANK_TYPE);
		UpgradeContainerRegistry.register(PUMP_UPGRADE.getId(), PUMP_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_PUMP_UPGRADE.getId(), ADVANCED_PUMP_TYPE);
		UpgradeContainerRegistry.register(XP_PUMP_UPGRADE.getId(), XP_PUMP_TYPE);
		UpgradeContainerRegistry.register(ANVIL_UPGRADE.getId(), ANVIL_TYPE);
		UpgradeContainerRegistry.register(SMITHING_UPGRADE.getId(), SMITHING_TYPE);
	}

	public static void registerDispenseBehavior() {
		ModItems.BACKPACKS.forEach(backpack -> DispenserBlock.registerBehavior(backpack.get(), new BackpackDispenseBehavior()));
	}

	public static void registerCauldronInteractions() {
		// 26.3 删了 CauldronInteraction.WATER 且 Dispatcher.put 是包私有，只能从 UseBlockCallback 接
		UseBlockCallback.EVENT.register(ModItems::tryWashBackpack);
	}

	private static InteractionResult tryWashBackpack(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
		ItemStack stack = player.getItemInHand(hand);
		if (!(stack.getItem() instanceof BackpackItem)) {
			return InteractionResult.PASS;
		}

		BlockPos pos = hitResult.getBlockPos();
		BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof LayeredCauldronBlock) || state.getValue(LayeredCauldronBlock.LEVEL) <= 0) {
			return InteractionResult.PASS;
		}

		IBackpackWrapper wrapper = BackpackWrapper.fromStack(stack);
		if (wrapper.getMainColor() == BackpackWrapper.DEFAULT_MAIN_COLOR && wrapper.getAccentColor() == BackpackWrapper.DEFAULT_ACCENT_COLOR) {
			return InteractionResult.PASS;
		}

		if (!level.isClientSide()) {
			wrapper.setColors(BackpackWrapper.DEFAULT_MAIN_COLOR, BackpackWrapper.DEFAULT_ACCENT_COLOR);
			LayeredCauldronBlock.lowerFillLevel(state, level, pos);
		}

		return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
	}

	private static void registerCapabilities() {
		var backpacks = ModItems.BACKPACKS.stream().map(Supplier::get).toArray(BackpackItem[]::new);
		ItemStorage.ITEM.registerForItems((stack, ctx) -> {
					IBackpackWrapper backpackWrapper = BackpackWrapper.fromStack(stack);
					return backpackWrapper.getContentsUuid().isEmpty() ? EmptyItemHandler.INSTANCE : backpackWrapper.getInventoryForInputOutput();
				}, backpacks);
		FluidStorage.ITEM.registerForItems((stack, ctx) -> {
					if (Boolean.FALSE.equals(Config.SERVER.itemFluidHandlerEnabled.get())) {
						return null;
					}
					return BackpackWrapper.fromStack(stack).getItemFluidHandler().orElse(null);
				}, backpacks);
	}

	private static class BackpackDispenseBehavior extends OptionalDispenseItemBehavior {
		@Override
		protected ItemStack execute(BlockSource source, ItemStack stack) {
			setSuccess(false);
			Item item = stack.getItem();
			if (item instanceof BackpackItem backpackItem) {
				Direction dispenserDirection = source.state().getValue(DispenserBlock.FACING);
				BlockPos blockpos = source.pos().relative(dispenserDirection);
				Direction against = source.level().isEmptyBlock(blockpos.below()) ? dispenserDirection.getOpposite() : Direction.UP;

				setSuccess(backpackItem.tryPlace(null, dispenserDirection.getAxis() == Direction.Axis.Y ? Direction.NORTH : dispenserDirection.getOpposite(), new DirectionalPlaceContext(source.level(), blockpos, dispenserDirection, stack, against)).consumesAction());
			}

			return stack;
		}
	}
}
