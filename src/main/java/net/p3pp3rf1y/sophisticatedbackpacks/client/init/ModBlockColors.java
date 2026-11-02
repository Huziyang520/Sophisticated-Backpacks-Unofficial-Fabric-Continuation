package net.p3pp3rf1y.sophisticatedbackpacks.client.init;

import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.world.level.block.Block;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlockEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedcore.util.WorldHelper;

import java.util.function.Supplier;

/**
 * Tints a placed backpack with its own cloth and border colors.
 * <p>
 * 26.3 collects tints through a factory that fills an index-addressed list, so the entries are
 * appended in tint index order rather than returned per lookup.
 */
public class ModBlockColors {
	private ModBlockColors() {
	}

	public static void registerBlockColorHandlers() {
		BlockColorRegistry.register((state, blockDisplayReader, pos, tints) -> {
			if (pos == null) {
				addDefaults(tints);
				return;
			}
			WorldHelper.getBlockEntity(blockDisplayReader, pos, BackpackBlockEntity.class).ifPresentOrElse(
					backpack -> {
						tints.add(backpack.getBackpackWrapper().getMainColor());
						tints.add(backpack.getBackpackWrapper().getAccentColor());
					},
					() -> addDefaults(tints));
		}, ModBlocks.BACKPACKS.stream().map(Supplier::get).toArray(Block[]::new));
	}

	private static void addDefaults(it.unimi.dsi.fastutil.ints.IntList tints) {
		tints.add(BackpackWrapper.DEFAULT_MAIN_COLOR);
		tints.add(BackpackWrapper.DEFAULT_ACCENT_COLOR);
	}
}