package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;

import org.jetbrains.annotations.Nullable;

/**
 * Item tint sources for the backpack item model.
 * <p>
 * 26.3 removed Fabric's {@code ColorProviderRegistry}; item colours are now declared by the item definition
 * ({@code assets/<ns>/items/<id>.json}) as a list of {@link ItemTintSource} entries that are addressed by the
 * {@code tintindex} written on the model faces. The list of known tint source types lives in
 * {@link ItemTintSources} and has no public registration API, but its id mapper is late bound and the codec resolves
 * ids when a model is decoded, so adding entries to it after the class is initialised is enough (the field is opened
 * up through the access widener).
 */
public final class BackpackItemTints {
	public static final Identifier MAIN_COLOR_ID = SophisticatedBackpacks.getRL("backpack_main_color");
	public static final Identifier ACCENT_COLOR_ID = SophisticatedBackpacks.getRL("backpack_accent_color");

	private static final ItemTintSource MAIN_COLOR = new BackpackColorTint(true);
	private static final ItemTintSource ACCENT_COLOR = new BackpackColorTint(false);

	private BackpackItemTints() {
	}

	/** Registers both tint sources; called from the client initialiser before any resource reload. */
	public static void register() {
		ItemTintSources.ID_MAPPER.put(MAIN_COLOR_ID, MapCodec.unit(MAIN_COLOR));
		ItemTintSources.ID_MAPPER.put(ACCENT_COLOR_ID, MapCodec.unit(ACCENT_COLOR));
	}

	/** Tints either the cloth (main colour) or the border (accent colour) of a backpack, as the old colour provider did. */
	private record BackpackColorTint(boolean mainColor) implements ItemTintSource {
		@Override
		public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
			IBackpackWrapper wrapper = BackpackWrapper.fromStack(stack);
			return mainColor ? wrapper.getMainColor() : wrapper.getAccentColor();
		}

		@Override
		public MapCodec<? extends ItemTintSource> type() {
			return MapCodec.unit(this);
		}
	}
}
