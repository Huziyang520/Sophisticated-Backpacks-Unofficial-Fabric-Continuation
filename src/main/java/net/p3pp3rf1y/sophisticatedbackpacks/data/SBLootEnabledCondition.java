package net.p3pp3rf1y.sophisticatedbackpacks.data;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;

public class SBLootEnabledCondition implements LootItemCondition {

	private static final SBLootEnabledCondition INSTANCE = new SBLootEnabledCondition();
	public static final MapCodec<SBLootEnabledCondition> CODEC = MapCodec.unit(INSTANCE).stable();
	private SBLootEnabledCondition() {
	}

	@Override
	public MapCodec<? extends LootItemCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(LootContext lootContext) {
		return Boolean.TRUE.equals(Config.COMMON.chestLootEnabled.get());
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder implements LootItemCondition.Builder {
		@Override
		public LootItemCondition build() {
			return new SBLootEnabledCondition();
		}
	}
}
